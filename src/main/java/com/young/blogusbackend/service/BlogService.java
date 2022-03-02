package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.BlogRequest;
import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {

    private final BlogRepository blogRepository;

    public BlogResponse createBlog(BlogRequest blogRequest) {
        return null;
    }
}
