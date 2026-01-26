package com.example.helpdesk.mapper;

import com.example.helpdesk.dto.UserDto;
import com.example.helpdesk.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "password", ignore = true)
    UserDto toDto(User user);

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdTickets", ignore = true)
    @Mapping(target = "assignedTickets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User toEntity(UserDto dto);

    List<UserDto> toDtoList(List<User> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdTickets", ignore = true)
    @Mapping(target = "assignedTickets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    void updateEntity(UserDto dto, @MappingTarget User user);
}
