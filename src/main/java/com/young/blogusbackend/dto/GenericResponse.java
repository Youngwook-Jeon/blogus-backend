package com.young.blogusbackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenericResponse {

    private String msg;

    public GenericResponse(String msg) {
        this.msg = msg;
    }
}
