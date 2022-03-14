package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class CommentResponse {

    private Long id;
    private BlogerResponse user;
    private Long blogId;
    private Long blogUserId;
    private Long parentId;
    private String content;
    private String createdAt;
    private String updatedAt;
    private Boolean isDeleted;
}
