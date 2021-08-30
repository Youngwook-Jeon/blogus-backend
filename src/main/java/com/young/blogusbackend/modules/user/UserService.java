package com.young.blogusbackend.modules.user;

import com.young.blogusbackend.infra.response.GenericResponse;

import javax.mail.MessagingException;

public interface UserService {

    GenericResponse register(UserDto userDto) throws MessagingException;
}
