package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.dto.RegisterRequest;
import com.young.blogusbackend.model.Bloger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogerMapper {

    @Mapping(target = "createdAt", expression = "java(bloger.getCreatedAt().toString())")
    BlogerResponse blogerToBlogerResponse(Bloger bloger);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "enabled", constant = "false")
    @Mapping(target = "refreshToken", ignore = true)
    @Mapping(target = "role", constant = "ROLE_USER")
    Bloger registerRequestToBlog(RegisterRequest registerRequest);
}
