package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.CategoryResponse;
import com.young.blogusbackend.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponse categoryToCategoryResponse(Category category);
}
