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
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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

    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAllByOrderByCreatedAtDesc();

        return mapCategoriesToResponses(categories);
    }

    private List<CategoryResponse> mapCategoriesToResponses(List<Category> categories) {
        return categories.stream()
                .map(this::getCategoryResponse)
                .collect(Collectors.toList());
    }

    public void updateCategory(Long id, CategoryRequest categoryRequest) {
        Category category = findCategoryById(id);
        category.setName(categoryRequest.getName().toLowerCase(Locale.ROOT));
        category.setUpdatedAt(Instant.now());
        categoryRepository.save(category);
    }

    private Category findCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new SpringBlogusException("존재하지 않는 카테고리입니다."));
    }
}
