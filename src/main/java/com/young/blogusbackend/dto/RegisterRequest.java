package com.young.blogusbackend.dto;

import com.young.blogusbackend.dto.validator.PasswordMatch;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data @PasswordMatch
@NoArgsConstructor @AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "이름이 필요합니다.")
    @Length(max = 20, message = "이름의 길이는 최대 20자입니다.")
    private String name;

    @NotBlank(message = "이메일이 필요합니다.")
    @Email(message = "유효한 이메일 주소가 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호가 필요합니다.")
    private String password;

    @NotBlank(message = "확인 비밀번호가 필요합니다.")
    private String cfPassword;
}
