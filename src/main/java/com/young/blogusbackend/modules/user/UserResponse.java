package com.young.blogusbackend.modules.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponse {

    private String status;
    private String message;
    private String name;
    private String account;
    private String password;
    private String activeToken;
}
