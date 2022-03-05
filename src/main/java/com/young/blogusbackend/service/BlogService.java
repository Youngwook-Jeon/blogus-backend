package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.BlogRequest;
import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.dto.CategoryWithBlogsDto;
import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.mapper.BlogMapper;
import com.young.blogusbackend.mapper.CategoryMapper;
import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Category;
import com.young.blogusbackend.repository.BlogRepository;
import com.young.blogusbackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BlogService {

    private final AuthService authService;
    private final BlogRepository blogRepository;
    private final CategoryRepository categoryRepository;
    private final BlogMapper blogMapper;
    private final CategoryMapper categoryMapper;

    public BlogResponse createBlog(BlogRequest blogRequest) {
        Bloger currentUser = authService.getCurrentUser();
        Category category = categoryRepository.findByName(blogRequest.getCategory())
                .orElseThrow(() -> new SpringBlogusException("존재하지 않는 카테고리입니다."));
        Blog blog = blogMapper.blogRequestToBlog(blogRequest, currentUser, category);
        blogRepository.save(blog);
        return blogMapper.blogToBlogResponse(blog);
    }

    public List<CategoryWithBlogsDto> getHomeBlogs() {
        List<Category> categoryList = categoryRepository.findAll();
        return categoryMapper.categoryListToDtoList(categoryList);
    }
}
