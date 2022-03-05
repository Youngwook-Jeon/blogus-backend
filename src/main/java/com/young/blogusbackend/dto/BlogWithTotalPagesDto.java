package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class BlogWithTotalPagesDto {

    private List<BlogResponse> blogs;
    private Integer total;
}
