package com.young.blogusbackend.modules.user;

import lombok.*;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class UserResponse {

    private String status;
    private String message;
    private String name;
    private String account;
    private String password;
    private String activeToken;
}
