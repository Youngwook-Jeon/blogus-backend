package com.young.blogusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor @AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "이메일이 필요합니다.")
    @Email(message = "유효한 이메일 주소가 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호가 필요합니다.")
    @Pattern(
            regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,50}$",
            message = "적어도 하나의 영문자, 숫자, 특수문자를 포함하여 8자리 이상이어야 합니다."
    )
    private String password;
}
