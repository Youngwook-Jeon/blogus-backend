package com.young.blogusbackend.modules.user;

import com.young.blogusbackend.infra.exception.AccountExistsException;
import com.young.blogusbackend.infra.mail.MailService;
import com.young.blogusbackend.infra.response.GenericResponse;
import com.young.blogusbackend.infra.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;

import static com.young.blogusbackend.modules.user.validator.ValidAccountValidator.validEmail;

@Service(value = "userService")
@Transactional @RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final String CLIENT_URL = "http://localhost:3000";
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final JwtUtil jwtUtil;
    private final MailService mailService;

    @Override
    public GenericResponse register(UserDto userDto) throws MessagingException {
        if (userRepository.findByAccount(userDto.getAccount()) != null) {
            throw new AccountExistsException("이미 존재하는 계정입니다.");
        }

        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User userInDb = userRepository.save(user);
        String activeToken = jwtUtil.generateActiveToken(userDto.getName(), userDto.getAccount(), userDto.getPassword());
        String url = CLIENT_URL + "/active/" + activeToken;

        if (validEmail(userDto.getAccount())) {
            mailService.sendMail(userDto.getAccount(), url, "이메일 주소 검증하기");
            return new GenericResponse("가입에 성공했습니다. 이메일을 확인해 주세요.");
        }
        return new GenericResponse("이메일 가입이 아닙니다.");
    }

//    private UserResponse completeUserResponse(UserResponse userResponse) {
//        userResponse.setStatus("OK");
//        userResponse.setMessage("회원가입에 성공했습니다.");
//        userResponse.setActiveToken(
//                jwtUtil.generateActiveToken(
//                        userResponse.getName(),
//                        userResponse.getAccount(),
//                        userResponse.getPassword()
//                )
//        );
//
//        return userResponse;
//    }
}
