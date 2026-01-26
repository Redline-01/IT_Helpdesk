package com.example.helpdesk.mapper;

import com.example.helpdesk.dto.TicketCreateDto;
import com.example.helpdesk.dto.TicketDto;
import com.example.helpdesk.dto.TicketUpdateDto;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TicketMapper {

    @Mapping(target = "createdByUsername", source = "createdBy.username")
    @Mapping(target = "createdByFullName", expression = "java(getFullName(ticket.getCreatedBy()))")
    @Mapping(target = "assignedToUsername", source = "assignedTo.username")
    @Mapping(target = "assignedToFullName", expression = "java(getFullName(ticket.getAssignedTo()))")
    @Mapping(target = "departmentName", source = "department.name")
    TicketDto toDto(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Ticket toEntity(TicketCreateDto dto);

    List<TicketDto> toDtoList(List<Ticket> tickets);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "resolvedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    void updateEntity(TicketUpdateDto dto, @MappingTarget Ticket ticket);

    default String getFullName(User user) {
        if (user == null) {
            return null;
        }
        if (user.getFirstName() != null && user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        }
        return user.getUsername();
    }
}
