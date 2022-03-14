package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.CommentCreateRequest;
import com.young.blogusbackend.dto.CommentResponse;
import com.young.blogusbackend.dto.CommentUpdateRequest;
import com.young.blogusbackend.dto.GenericResponse;
import com.young.blogusbackend.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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

    @GetMapping("/blog/{id}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponse> getCommentsByBlogId(@PathVariable Long id) {
        return commentService.getCommentsByBlogId(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse updateComment(
            @PathVariable Long id,
            @RequestBody CommentUpdateRequest updateRequest
    ) {
        return commentService.updateComment(id, updateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponse deleteComment(@PathVariable Long id) {
        return commentService.deleteComment(id);
    }
}
