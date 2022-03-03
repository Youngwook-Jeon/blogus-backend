package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class BlogResponse {

    private Long id;
    private String title;
    private String content;
    private String description;
    private String thumbnail;
    private String createdAt;
    private String updatedAt;
    private BlogerResponse user;
    private CategoryResponse category;
}
