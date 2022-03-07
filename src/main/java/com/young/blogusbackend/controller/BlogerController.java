package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.GenericResponse;
import com.young.blogusbackend.dto.UpdateBlogerRequest;
import com.young.blogusbackend.service.BlogerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users") @RequiredArgsConstructor
public class BlogerController {

    private final BlogerService blogerService;

    @PatchMapping("/update_profile")
    @ResponseStatus(HttpStatus.OK)
    public GenericResponse updateUserProfile(@Valid @RequestBody UpdateBlogerRequest updateBlogerRequest) {
        blogerService.updateUserProfile(updateBlogerRequest);
        return new GenericResponse("유저 정보가 업데이트되었습니다.");
    }
}
