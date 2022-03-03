package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogRequest;
import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BlogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "category", source = "category")
    public abstract Blog blogRequestToBlog(BlogRequest blogRequest, Bloger bloger, Category category);

    @Mapping(target = "createdAt", expression = "java(blog.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(blog.getUpdatedAt().toString())")
    public abstract BlogResponse blogToBlogResponse(Blog blog);
}
