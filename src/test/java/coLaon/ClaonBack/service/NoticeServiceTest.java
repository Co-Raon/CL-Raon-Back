package coLaon.ClaonBack.service;

import coLaon.ClaonBack.common.domain.Pagination;
import coLaon.ClaonBack.common.domain.PaginationFactory;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.notice.domain.Notice;
import coLaon.ClaonBack.notice.dto.NoticeCreateRequestDto;
import coLaon.ClaonBack.notice.dto.NoticeResponseDto;
import coLaon.ClaonBack.notice.repository.NoticeRepository;
import coLaon.ClaonBack.notice.service.NoticeService;

import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class NoticeServiceTest {
    @Mock
    UserRepository userRepository;
    @Mock
    NoticeRepository noticeRepository;

    @Spy
    PaginationFactory paginationFactory = new PaginationFactory();

    @InjectMocks
    NoticeService noticeService;

    private User adminUser, generalUser;

    @BeforeEach
    void setUp() {
        this.generalUser = User.of(
                "test@gmail.com",
                "1234567890",
                "test",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId"
        );
        ReflectionTestUtils.setField(this.generalUser, "id", "generalUserId");

        this.adminUser = User.of(
                "coraon.dev@gmail.com",
                "test2345!!",
                "test2",
                175.0F,
                178.0F,
                "",
                "",
                "instagramId2"
        );
        ReflectionTestUtils.setField(this.adminUser, "id", "adminUserId");
    }

    @Test
    @DisplayName("Success case for get notice")
    void successGetNoticeList() {
        // given
        Pageable pageable = PageRequest.of(0, 2);
        Notice sampleNotice = Notice.of("asdf", "asdfaf", this.adminUser);
        given(this.noticeRepository.findAllWithPagination(pageable)).willReturn(new PageImpl<>(List.of(sampleNotice), pageable, 1));

        // when
        Pagination<NoticeResponseDto> pagination = this.noticeService.getNoticeList(pageable);

        // then
        assertThat(pagination.getResults().size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Success case for create notice")
    void successCreateNotice() {
        NoticeCreateRequestDto dto = new NoticeCreateRequestDto("asdf", "ASdf");
        Notice notice = Notice.of(
                dto.getTitle(),
                dto.getContent(),
                this.adminUser
        );

        try (MockedStatic<Notice> mockedNotice = mockStatic(Notice.class)) {
            // given
            given(this.userRepository.findById(this.adminUser.getId())).willReturn(Optional.ofNullable(this.adminUser));

            mockedNotice.when(() -> Notice.of(
                    dto.getTitle(),
                    dto.getContent(),
                    this.adminUser
            )).thenReturn(notice);

            given(this.noticeRepository.save(notice)).willReturn(notice);

            // when
            NoticeResponseDto result = this.noticeService.createNotice(this.adminUser.getId(), dto);

            // then
            assertThat(result)
                    .extracting("title", "content")
                    .contains("asdf", "ASdf");
        }
    }

    @Test
    @DisplayName("Fail case for create notice")
    void failCreateNotice() {
        // given
        NoticeCreateRequestDto dto = new NoticeCreateRequestDto("asdf", "ASdf");
        given(this.userRepository.findById(this.generalUser.getId())).willReturn(Optional.ofNullable(this.generalUser));

        // when
        final UnauthorizedException ex = Assertions.assertThrows(
                UnauthorizedException.class,
                () -> this.noticeService.createNotice(this.generalUser.getId(), dto)
        );

        // then
        assertThat(ex)
                .extracting("errorCode", "message")
                .contains(ErrorCode.NOT_ACCESSIBLE, "접근 권한이 없습니다.");
    }
}
