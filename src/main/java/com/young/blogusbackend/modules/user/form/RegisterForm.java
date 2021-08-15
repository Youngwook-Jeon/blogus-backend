package com.young.blogusbackend.modules.user.form;

import com.young.blogusbackend.modules.user.validator.ValidAccount;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class RegisterForm {

    @NotEmpty(message = "이름이 필요합니다.")
    private String name;

    @NotEmpty(message = "이메일이나 휴대전화 번호를 입력하세요.")
    @ValidAccount
    private String account;

    @NotEmpty(message = "패스워드가 필요합니다.")
    @Size(min = 6, max = 30, message = "패스워드를 6자 이상, 30자 이하로 입력하세요.")
    private String password;
}
