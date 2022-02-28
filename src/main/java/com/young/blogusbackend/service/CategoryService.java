package com.young.blogusbackend.service;

import com.young.blogusbackend.dto.CategoryRequest;
import com.young.blogusbackend.dto.CategoryResponse;
import com.young.blogusbackend.exception.SpringBlogusException;
import com.young.blogusbackend.model.Category;
import com.young.blogusbackend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest categoryRequest) {
        String name = categoryRequest.getName().toLowerCase(Locale.ROOT);
        Optional<Category> categoryInDb = categoryRepository.findByName(name);
        if (categoryInDb.isPresent()) {
            throw new SpringBlogusException("이미 존재하는 카테고리입니다.");
        }
        Category newCategory = Category.builder()
                .name(name)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        categoryRepository.save(newCategory);

        return getCategoryResponse(newCategory);
    }

    private CategoryResponse getCategoryResponse(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
