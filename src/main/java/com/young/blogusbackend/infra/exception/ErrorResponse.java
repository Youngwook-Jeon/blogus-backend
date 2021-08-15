package com.young.blogusbackend.infra.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
public class ErrorResponse {

    private LocalDateTime timestamp;
    private String message;
}
