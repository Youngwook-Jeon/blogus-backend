package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "블로그의 아이디가 필요합니다.")
    private Long blogId;

    @NotNull(message = "블로그 작성 유저의 아이디가 필요합니다.")
    private Long blogUserId;

    private Long parentId;

    @NotBlank(message = "댓글의 내용을 입력하세요.")
    @Length(max = 200, message = "댓글은 200자 이하로 입력하세요.")
    private String content;
}
