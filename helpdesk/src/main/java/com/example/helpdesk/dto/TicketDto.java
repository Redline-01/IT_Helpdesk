package com.example.helpdesk.dto;

import com.example.helpdesk.enums.TicketCategory;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDto {

    private Long id;

    private String title;

    private String description;

    private TicketPriority priority;

    private TicketStatus status;

    private TicketCategory category;

    private Long createdById;

    private String createdByUsername;

    private Long assignedToId;

    private String assignedToUsername;

    private Long departmentId;

    private String departmentName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private LocalDateTime closedAt;

    private long commentCount;
    private long attachmentCount;


}
