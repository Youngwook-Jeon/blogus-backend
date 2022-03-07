package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor @NoArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "비밀번호가 필요합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,50}$",
            message = "적어도 하나의 영문자, 숫자, 특수문자를 포함하여 8자리 이상이어야 합니다."
    )
    private String password;
}
