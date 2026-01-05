package com.example.helpdesk.dto;

import lombok.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDto {

    private long totalTickets;
    private long openTickets;
    private long closedTickets;
    private long resolvedTickets;
    private long inProgressTickets;
    private long totalUsers;
    private long activeUsers;

    private Map<String, Long> ticketsByCategory;
    private Map<String, Long> ticketsByPriority;
    private Map<String, Long> ticketsByDepartment;
    private Map<String, Long> ticketsByStatus;

}
