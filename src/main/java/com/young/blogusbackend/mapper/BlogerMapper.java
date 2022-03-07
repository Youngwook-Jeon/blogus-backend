package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.model.Bloger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BlogerMapper {

    @Mapping(target = "createdAt", expression = "java(bloger.getCreatedAt().toString())")
    BlogerResponse blogerToBlogerResponse(Bloger bloger);
}
