package com.example.helpdesk.service;

import com.example.helpdesk.dto.DashboardStatsDto;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final TicketRepository ticketRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public DashboardStatsDto getDashboardStats(String username, boolean isUserRole) {
        User user = userService.getUserByUsername(username);

        if (isUserRole) {
            // For regular users, show only their own ticket statistics
            return DashboardStatsDto.builder()
                    .totalTickets(ticketRepository.countByCreatedBy_Id(user.getId()))
                    .openTickets(ticketRepository.countByCreatedBy_IdAndStatus(user.getId(), TicketStatus.OPEN))
                    .inProgressTickets(ticketRepository.countByCreatedBy_IdAndStatus(user.getId(), TicketStatus.IN_PROGRESS))
                    .resolvedTickets(ticketRepository.countByCreatedBy_IdAndStatus(user.getId(), TicketStatus.RESOLVED))
                    .closedTickets(ticketRepository.countByCreatedBy_IdAndStatus(user.getId(), TicketStatus.CLOSED))
                    .urgentTickets(ticketRepository.countByCreatedBy_IdAndPriority(user.getId(), TicketPriority.URGENT))
                    .highPriorityTickets(ticketRepository.countByCreatedBy_IdAndPriority(user.getId(), TicketPriority.HIGH))
                    .myAssignedTickets(0L) // Users don't get tickets assigned to them
                    .build();
        } else {
            // For agents and admins, show all tickets statistics
            return DashboardStatsDto.builder()
                    .totalTickets(ticketRepository.count())
                    .openTickets(ticketRepository.countByStatus(TicketStatus.OPEN))
                    .inProgressTickets(ticketRepository.countByStatus(TicketStatus.IN_PROGRESS))
                    .resolvedTickets(ticketRepository.countByStatus(TicketStatus.RESOLVED))
                    .closedTickets(ticketRepository.countByStatus(TicketStatus.CLOSED))
                    .urgentTickets(ticketRepository.countByPriority(TicketPriority.URGENT))
                    .highPriorityTickets(ticketRepository.countByPriority(TicketPriority.HIGH))
                    .myAssignedTickets(ticketRepository.countActiveTicketsByAssignedTo(user.getId()))
                    .build();
        }
    }
}
