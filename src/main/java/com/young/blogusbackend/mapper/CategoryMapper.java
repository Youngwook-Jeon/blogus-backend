package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.dto.CategoryResponse;
import com.young.blogusbackend.dto.CategoryWithBlogsDto;
import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse categoryToCategoryResponse(Category category);

    List<CategoryWithBlogsDto> categoryListToDtoList(List<Category> categoryList);

    default CategoryWithBlogsDto categoryToDto(Category category) {
        return CategoryWithBlogsDto.builder()
                .id(category.getId())
                .name(category.getName())
                .count(category.getBlogs().size())
                .blogs(
                        category.getBlogs().stream()
                                .map(this::blogToBlogResponse)
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Mapping(target = "createdAt", expression = "java(blog.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(blog.getUpdatedAt().toString())")
    @Mapping(target = "user", source = "blog.bloger")
    BlogResponse blogToBlogResponse(Blog blog);

    @Mapping(target = "createdAt", expression = "java(bloger.getCreatedAt().toString())")
    BlogerResponse blogerToBlogerResponse(Bloger bloger);
}
