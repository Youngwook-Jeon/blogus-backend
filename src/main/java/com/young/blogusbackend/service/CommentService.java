package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.CommentCreateRequest;
import com.young.blogusbackend.dto.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    public CommentResponse createComment(CommentCreateRequest createRequest) {
        return null;
    }
}
