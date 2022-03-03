package com.young.blogusbackend.mapper;

import com.young.blogusbackend.dto.BlogerResponse;
import com.young.blogusbackend.model.Bloger;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BlogerMapper {

    BlogerResponse blogerToBlogerResponse(Bloger bloger);
}
