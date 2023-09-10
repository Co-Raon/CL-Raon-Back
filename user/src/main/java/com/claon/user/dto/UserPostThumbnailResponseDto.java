package com.claon.user.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserPostThumbnailResponseDto {
    private final String postId;
    private final String thumbnailUrl;
    private final List<ClimbingHistoryResponseDto> climbingHistories;

    private UserPostThumbnailResponseDto(
            String postId,
            String thumbnailUrl,
            List<ClimbingHistoryResponseDto> climbingHistories
    ) {
        this.postId = postId;
        this.thumbnailUrl = thumbnailUrl;
        this.climbingHistories = climbingHistories;
    }

    public static UserPostThumbnailResponseDto from(
            String postId,
            String thumbnailUrl,
            List<ClimbingHistoryResponseDto> climbingHistories

    ) {
        return new UserPostThumbnailResponseDto(
                postId,
                thumbnailUrl,
                climbingHistories
        );
    }
}
