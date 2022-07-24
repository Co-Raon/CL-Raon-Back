package coLaon.ClaonBack.post.domain;

import coLaon.ClaonBack.center.domain.HoldInfo;
import coLaon.ClaonBack.common.domain.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.annotation.Nullable;
import javax.persistence.*;

@Getter
@Entity
@Table(name = "tb_climbing_history")
@NoArgsConstructor
public class ClimbingHistory extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "hold_info_id", nullable = false)
    private HoldInfo holdInfo;

    @Column(nullable = false)
    private Integer climbingCount;

    private ClimbingHistory(Post post, HoldInfo holdInfo, Integer climbingCount) {
        this.post = post;
        this.holdInfo = holdInfo;
        this.climbingCount = climbingCount;
    }

    private ClimbingHistory(String id, Post post, HoldInfo holdInfo) {
        super(id);
        this.post = post;
        this.holdInfo = holdInfo;
    }

    public static ClimbingHistory of(Post post, HoldInfo holdInfo, Integer climbingCount) {
        return new ClimbingHistory(
                post,
                holdInfo,
                climbingCount
        );
    }

    public static ClimbingHistory of(String id, Post post, HoldInfo holdInfo) {
        return new ClimbingHistory(
                id,
                post,
                holdInfo
        );
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
