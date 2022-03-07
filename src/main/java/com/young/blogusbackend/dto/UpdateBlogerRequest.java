package com.young.blogusbackend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor @NoArgsConstructor
public class UpdateBlogerRequest {

    @URL(message = "올바른 URL 형식이 아닙니다.")
    private String avatar;

    @NotBlank(message = "이름이 필요합니다.")
    @Length(max = 20, message = "이름의 길이는 최대 20자입니다.")
    private String name;
}
