package com.young.blogusbackend.modules.user;

import com.young.blogusbackend.modules.user.form.RegisterForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController @RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PostMapping("/register")
    public UserResponse register(@RequestBody @Valid RegisterForm registerForm) {
        UserDto userDto = modelMapper.map(registerForm, UserDto.class);
        return userService.register(userDto);
    }
}
