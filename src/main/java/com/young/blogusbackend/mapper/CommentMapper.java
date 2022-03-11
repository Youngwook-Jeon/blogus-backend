package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.dto.CommentCreateRequest;
import com.young.blogusbackend.dto.CommentResponse;
import com.young.blogusbackend.model.Blog;
import com.young.blogusbackend.model.Bloger;
import com.young.blogusbackend.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bloger", source = "bloger")
    @Mapping(target = "content", source = "createRequest.content")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "blog", source = "blog")
    Comment commentCreateRequestToComment(CommentCreateRequest createRequest, Bloger bloger, Blog blog);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "user", source = "comment.bloger")
    @Mapping(target = "createdAt", expression = "java(comment.getCreatedAt().toString())")
    @Mapping(target = "updatedAt", expression = "java(comment.getUpdatedAt().toString())")
    CommentResponse commentToCommentResponse(Comment comment);

    @Mapping(target = "createdAt", expression = "java(bloger.getCreatedAt().toString())")
    BlogerResponse blogerToBlogerResponse(Bloger bloger);

    List<CommentResponse> commentListToCommentResponseList(List<Comment> commentList);
}
