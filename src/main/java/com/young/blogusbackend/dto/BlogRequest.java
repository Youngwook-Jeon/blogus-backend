package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor @NoArgsConstructor
public class BlogRequest {

    @NotBlank(message = "블로그 제목이 필요합니다.")
    @Length(min = 10, max = 50, message = "제목의 길이는 10~50자입니다.")
    private String title;

    @NotBlank(message = "블로그 내용이 필요합니다.")
    @Length(min = 2000, message = "내용은 2000자 이상이어야 합니다.")
    private String content;

    @NotBlank(message = "블로그 설명이 필요합니다.")
    @Length(min = 50, max = 200, message = "50~200자로 블로그 설명이 필요합니다.")
    private String description;

    @NotBlank(message = "블로그 썸네일이 필요합니다.")
    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String thumbnail;

    @NotBlank(message = "블로그 썸네일이 필요합니다.")
    private String category;
}
