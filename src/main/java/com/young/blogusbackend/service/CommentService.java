package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.CommentCreateRequest;
import com.young.blogusbackend.dto.CommentResponse;
import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.mapper.CommentMapper;
import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Comment;
import com.young.blogusbackend.repository.BlogRepository;
import com.young.blogusbackend.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final BlogRepository blogRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuthService authService;

    public CommentResponse createComment(CommentCreateRequest createRequest) {
        Blog blog = blogRepository.findById(createRequest.getBlogId())
                .orElseThrow(() -> new SpringBlogusException("존재하지 않는 블로그입니다."));
        Bloger bloger = authService.getCurrentUser();
        Comment comment = commentMapper.commentCreateRequestToComment(createRequest, bloger, blog);
        commentRepository.save(comment);
        return commentMapper.commentToCommentResponse(comment);
    }

    public List<CommentResponse> getCommentsByBlogId(Long blogId) {
        List<Comment> commentList = commentRepository.findAllByBlogId(blogId);
        return commentMapper.commentListToCommentResponseList(commentList);
    }
}
