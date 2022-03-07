package com.young.blogusbackend.controller;

import com.young.blogusbackend.dto.BlogRequest;
import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.dto.BlogWithTotalPagesDto;
import com.young.blogusbackend.dto.CategoryWithBlogsDto;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Category;
import com.young.blogusbackend.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/blogs/category/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogWithTotalPagesDto getBlogsByCategory(
            @PathVariable("id") Category category,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return blogService.getBlogsByCategory(category, pageable);
    }

    @GetMapping("/blogs/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogWithTotalPagesDto getBlogsByUser(
            @PathVariable("id") Bloger bloger,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return blogService.getBlogsByUser(bloger, pageable);
    }

    @GetMapping("/blogs/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BlogResponse getBlogById(@PathVariable Long id) {
        return blogService.getBlogById(id);
    }

}
