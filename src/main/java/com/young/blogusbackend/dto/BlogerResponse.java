package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@AllArgsConstructor @NoArgsConstructor
public class BlogerResponse {

    private Long id;
    private String name;
    private String email;
    private String avatar;
    private String role;
    private boolean enabled;
    private String createdAt;
}
