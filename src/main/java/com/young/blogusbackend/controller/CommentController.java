package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.CommentCreateRequest;
import com.young.blogusbackend.dto.CommentResponse;
import com.young.blogusbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(@Valid @RequestBody CommentCreateRequest createRequest) {
        return commentService.createComment(createRequest);
    }
}
