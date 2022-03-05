package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.BlogRequest;
import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.dto.CategoryWithBlogsDto;
import com.young.blogusbackend.model.Category;
import com.young.blogusbackend.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/blogs")
    @ResponseStatus(HttpStatus.CREATED)
    public BlogResponse createBlog(@Valid @RequestBody BlogRequest blogRequest) {
        return blogService.createBlog(blogRequest);
    }

    @GetMapping("/home/blogs")
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryWithBlogsDto> getHomeBlogs() {
        return blogService.getHomeBlogs();
    }
}
