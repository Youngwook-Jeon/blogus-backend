package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor @AllArgsConstructor
public class CategoryRequest {

    @NotBlank(message = "카테고리 이름이 필요합니다.")
    @Length(max = 50, message = "카테고리 이름의 길이는 최대 50자입니다.")
    private String name;
}
