package coLaon.ClaonBack.post.Service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.repository.PostLikeRepository;
import coLaon.ClaonBack.post.repository.PostRepository;
import coLaon.ClaonBack.user.domain.User;
import coLaon.ClaonBack.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public LikeResponseDto createLike(String userId, LikeRequestDto likeRequestDto) {
        User liker = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Post post = postRepository.findById(likeRequestDto.getPostId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        return LikeResponseDto.from(postLikeRepository.save(
                PostLike.of(liker, post))
        );
    }

    @Transactional
    public LikeResponseDto deleteLike(String userId, LikeRequestDto likeRequestDto) {
        User liker = userRepository.findById(userId).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "유저 정보가 없습니다."
                )
        );

        Post post = postRepository.findById(likeRequestDto.getPostId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        PostLike like = postLikeRepository.findByLikerAndPost(liker, post);
        postLikeRepository.deleteById(like.getId());
        return LikeResponseDto.from(like);
    }
}
