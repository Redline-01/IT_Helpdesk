package com.example.helpdesk.mapper;

import com.example.helpdesk.dto.DepartmentDto;
import com.example.helpdesk.entity.Department;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    DepartmentDto toDto(Department department);

    Department toEntity(DepartmentDto dto);

    List<DepartmentDto> toDtoList(List<Department> departments);
}
