package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CommentUpdateRequest {

    @NotBlank(message = "댓글의 내용을 입력하세요.")
    @Length(max = 200, message = "댓글은 200자 이하로 입력하세요.")
    private String content;
}
