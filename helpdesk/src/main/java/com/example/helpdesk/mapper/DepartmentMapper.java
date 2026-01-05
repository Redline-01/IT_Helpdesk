package com.example.helpdesk.mapper;

import com.example.helpdesk.dto.DepartmentDto;
import com.example.helpdesk.entity.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface DepartmentMapper {

    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    @Mapping(source = "manager.id", target = "managerId")
    @Mapping(source = "manager.username", target = "managerName")
    @Mapping(target = "userCount", expression = "java(department.getUsers() == null ? 0 : department.getUsers().size())")
    @Mapping(target = "ticketCount", expression = "java(department.getTickets() == null ? 0 : department.getTickets().size())")
    DepartmentDto toDto(Department department);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "manager", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "tickets", ignore = true)
    Department toEntity(DepartmentDto dto);

    default String map(Department department) {
        return department != null ? department.getName() : null;
    }

    default Long mapToId(Department department) {
        return department != null ? department.getId() : null;
    }

}
