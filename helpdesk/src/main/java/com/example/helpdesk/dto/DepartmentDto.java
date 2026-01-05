package com.example.helpdesk.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentDto {

    private Long id;
    private String name;
    private String description;
    private Long managerId;
    private String managerName;
    private int userCount;
    private int ticketCount;
    private LocalDateTime createdAt;

}
