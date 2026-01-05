package com.example.helpdesk.dto;

import com.example.helpdesk.enums.TicketCategory;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.validation.ValidTicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketUpdateDto {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 5000, message = "Description must be between 10 and 5000 characters")
    private String description;

    private TicketPriority priority;

    @ValidTicketStatus
    private TicketStatus status;

    private TicketCategory category;
    private Long assignedToId;
    private Long departmentId;

}
