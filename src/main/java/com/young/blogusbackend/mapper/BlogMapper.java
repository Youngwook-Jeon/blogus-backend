package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogRequest;
import com.young.blogusbackend.dto.BlogResponse;
import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class BlogMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "category", source = "category")
    public abstract Blog blogRequestToBlog(BlogRequest blogRequest, Bloger bloger, Category category);

    @Mapping(target = "createdAt", expression = "java(blog.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(blog.getUpdatedAt().toString())")
    @Mapping(target = "user", source = "blog.bloger")
    public abstract BlogResponse blogToBlogResponse(Blog blog);

    public abstract List<BlogResponse> blogListToBlogResponseList(List<Blog> blogList);

    @Mapping(target = "createdAt", expression = "java(bloger.getCreatedAt().toString())")
    public abstract BlogerResponse blogerToBlogerResponse(Bloger bloger);
}
