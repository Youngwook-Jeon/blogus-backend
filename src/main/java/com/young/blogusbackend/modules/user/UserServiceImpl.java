package com.young.blogusbackend.modules.user;

import com.young.blogusbackend.infra.exception.AccountExistsException;
import com.young.blogusbackend.infra.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "userService")
@Transactional @RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;

    @Override
    public UserResponse register(UserDto userDto) {
        if (userRepository.findByAccount(userDto.getAccount()) != null) {
            throw new AccountExistsException("이미 존재하는 계정입니다.");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User userInDb = userRepository.save(user);
        UserResponse userResponse = modelMapper.map(userInDb, UserResponse.class);
        return completeUserResponse(userResponse);
    }

    private UserResponse completeUserResponse(UserResponse userResponse) {
        userResponse.setStatus("OK");
        userResponse.setMessage("회원가입에 성공했습니다.");
        userResponse.setActiveToken(
                jwtUtil.generateActiveToken(
                        userResponse.getName(),
                        userResponse.getAccount(),
                        userResponse.getPassword()
                )
        );

        return userResponse;
    }
}
