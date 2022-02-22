package com.young.blogusbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.young.blogusbackend.model.Bloger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuthenticationResponse {

    private String msg;

    @JsonProperty("access_token")
    private String accessToken;

    private Bloger user;
}
