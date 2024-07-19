package com.example.demo.mapper;

import com.example.demo.repository.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openapitools.model.UserDto;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {

    User dtoToModel(UserDto user);

    @Mapping(target = "status", ignore = true)
    @Mapping(target = "userId", source = "id")
    UserDto modelToDto(User user);
}
