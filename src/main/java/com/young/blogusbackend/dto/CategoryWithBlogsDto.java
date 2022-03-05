package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class CategoryWithBlogsDto {

    private Long id; // category id
    private String name; // category name
    private Integer count; // number of blogs associated to a category
    private List<BlogResponse> blogs;
}
