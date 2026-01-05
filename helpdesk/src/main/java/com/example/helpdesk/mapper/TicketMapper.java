package com.example.helpdesk.mapper;

import com.example.helpdesk.dto.TicketCreateDto;
import com.example.helpdesk.dto.TicketDto;
import com.example.helpdesk.dto.TicketUpdateDto;
import com.example.helpdesk.entity.Department;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.TicketAttachment;
import com.example.helpdesk.entity.TicketComment;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring",
        uses = {UserMapper.class, DepartmentMapper.class},
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface TicketMapper {

    TicketMapper INSTANCE = Mappers.getMapper(TicketMapper.class);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.username", target = "createdByUsername")
    @Mapping(source = "assignedTo.id", target = "assignedToId")
    @Mapping(source = "assignedTo.username", target = "assignedToUsername")
    @Mapping(source = "department", target = "departmentId", qualifiedByName = "departmentToId")
    @Mapping(source = "department", target = "departmentName", qualifiedByName = "departmentToName")
    @Mapping(source = "comments", target = "commentCount", qualifiedByName = "countComments")
    @Mapping(source = "attachments", target = "attachmentCount", qualifiedByName = "countAttachments")
    TicketDto toDto(Ticket ticket);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "assignedTo", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "closedAt", ignore = true)
    @Mapping(target = "status", constant = "OPEN")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "attachments", ignore = true)
    Ticket toEntity(TicketCreateDto dto);

    @Named("departmentToId")
    default Long departmentToId(Department department) {
        return department != null ? department.getId() : null;
    }

    @Named("departmentToName")
    default String departmentToName(Department department) {
        return department != null ? department.getName() : null;
    }

    @Named("countComments")
    default long countComments(List<TicketComment> comments) {
        return comments != null ? comments.size() : 0;
    }

    @Named("countAttachments")
    default long countAttachments(List<TicketAttachment> attachments) {
        return attachments != null ? attachments.size() : 0;
    }

}
