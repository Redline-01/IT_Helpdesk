package com.example.helpdesk.mapper;

import com.example.helpdesk.dto.UserDto;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.TicketStatus;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring",
        uses = DepartmentMapper.class,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        imports = {Collectors.class, TicketStatus.class})
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(target = "roles", expression = "java(mapRoles(user))")
    @Mapping(source = "createdTickets", target = "openTicketsCount", qualifiedByName = "countOpenTickets")
    @Mapping(source = "assignedTickets", target = "assignedTicketsCount", qualifiedByName = "countAssignedTickets")
    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdTickets", ignore = true)
    @Mapping(target = "assignedTickets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User toEntity(UserDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "createdTickets", ignore = true)
    @Mapping(target = "assignedTickets", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    void updateUserFromDto(UserDto dto, @MappingTarget User user);

    default Set<String> mapRoles(User user) {
        return user.getRoles().stream()
                .map(role -> String.valueOf(role.getName()))
                .collect(Collectors.toSet());
    }

    @Named("countOpenTickets")
    default int countOpenTickets(List<Ticket> tickets) {
        return tickets == null ? 0 : (int) tickets.stream()
                .filter(t -> t.getStatus() == TicketStatus.OPEN)
                .count();
    }

    @Named("countAssignedTickets")
    default int countAssignedTickets(List<Ticket> tickets) {
        return tickets == null ? 0 : tickets.size();
    }

    default List<UserDto> toDtoList(List<User> users) {
        if (users == null) {
            return Collections.emptyList();
        }
        return users.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}
