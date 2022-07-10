package coLaon.ClaonBack.post.Service;

import coLaon.ClaonBack.common.exception.BadRequestException;
import coLaon.ClaonBack.common.exception.ErrorCode;
import coLaon.ClaonBack.common.exception.UnauthorizedException;
import coLaon.ClaonBack.common.validator.ContentsCountValidator;
import coLaon.ClaonBack.common.validator.ContentsUrlFormatValidator;
import coLaon.ClaonBack.common.validator.Validator;
import coLaon.ClaonBack.post.domain.Post;
import coLaon.ClaonBack.post.domain.PostLike;
import coLaon.ClaonBack.post.dto.LikeRequestDto;
import coLaon.ClaonBack.post.dto.LikeResponseDto;
import coLaon.ClaonBack.post.dto.PostCreateRequestDto;
import coLaon.ClaonBack.post.dto.PostResponseDto;
import coLaon.ClaonBack.post.repository.PostContentsRepository;
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
    private final PostContentsRepository postContentsRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public PostResponseDto createPost(String userId, PostCreateRequestDto postCreateRequestDto) {
        User writer = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Validator validator = new ContentsCountValidator(postCreateRequestDto.getContentsSet());
        validator.linkWith(ContentsUrlFormatValidator.of(postCreateRequestDto.getContentsSet()));
        validator.validate();

        Post post = Post.of(
                postCreateRequestDto.getCenterName(),
                postCreateRequestDto.getHoldName(),
                postCreateRequestDto.getContent(),
                writer,
                postCreateRequestDto.getContentsSet()
        );

        postCreateRequestDto.getContentsSet().forEach(p -> {
            p.setPost(post);
        });
        postCreateRequestDto.getContentsSet().forEach(postContentsRepository::save);

        return PostResponseDto.from(
                this.postRepository.save(post)
        );
    }

    @Transactional
    public LikeResponseDto createLike(String userId, LikeRequestDto likeRequestDto) {
        User liker = userRepository.findById(userId).orElseThrow(
                () -> new UnauthorizedException(
                        ErrorCode.USER_DOES_NOT_EXIST,
                        "이용자를 찾을 수 없습니다."
                )
        );

        Post post = postRepository.findById(likeRequestDto.getPostId()).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "등반 정보가 없습니다."
                )
        );

        postLikeRepository.findByLikerAndPost(liker, post).ifPresent(
                like -> {
                    throw new BadRequestException(
                            ErrorCode.ROW_ALREADY_EXIST,
                            "이미 좋아요 한 게시글입니다."
                    );
                }
        );

        return LikeResponseDto.from(
                postLikeRepository.save(PostLike.of(liker, post)),
                postLikeRepository.countByPost_Id(post.getId())
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

        PostLike like = postLikeRepository.findByLikerAndPost(liker, post).orElseThrow(
                () -> new BadRequestException(
                        ErrorCode.ROW_DOES_NOT_EXIST,
                        "해당 게시글에 좋아요 하지 않았습니다."
                )
        );

        postLikeRepository.deleteById(like.getId());
        return LikeResponseDto.from(
                like,
                postLikeRepository.countByPost_Id(like.getPost().getId())
        );
    }
}
