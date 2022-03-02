package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class BlogResponse {

    private Long id;
    private String title;
    private String content;
    private String description;
    private String thumbnail;
    private Instant createdAt;
    private Instant updatedAt;
    private BlogerResponse bloger;
    private CategoryResponse category;
}