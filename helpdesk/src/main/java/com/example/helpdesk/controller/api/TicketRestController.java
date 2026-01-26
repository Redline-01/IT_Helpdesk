package com.example.helpdesk.controller.api;

import com.example.helpdesk.dto.TicketDto;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketRestController {

    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<TicketDto>> getAllTickets(Authentication authentication) {
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isUser = !isAdmin && !isAgent;

        if (isUser) {
            // Regular users see only their own tickets
            User user = userService.getUserByUsername(username);
            return ResponseEntity.ok(ticketService.getTicketsByUser(user.getId()));
        } else {
            // Agents and admins see all tickets
            return ResponseEntity.ok(ticketService.getAllTickets());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketDto> getTicket(@PathVariable Long id, Authentication authentication) {
        TicketDto ticket = ticketService.getTicketById(id);

        // Check if user has permission to view this ticket
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isUser = !isAdmin && !isAgent;

        if (isUser && !ticket.getCreatedByUsername().equals(username)) {
            // Regular user trying to access someone else's ticket
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(ticket);
    }

    @GetMapping("/search")
    public ResponseEntity<List<TicketDto>> searchTickets(
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) TicketPriority priority,
            @RequestParam(required = false) String keyword,
            Authentication authentication) {

        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isUser = !isAdmin && !isAgent;

        List<TicketDto> tickets = ticketService.searchTickets(status, priority, keyword);

        if (isUser) {
            // Filter to show only user's own tickets
            tickets = tickets.stream()
                    .filter(t -> t.getCreatedByUsername() != null &&
                               t.getCreatedByUsername().equals(username))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<TicketDto> assignTicket(@PathVariable Long id, @RequestParam Long userId) {
        return ResponseEntity.ok(ticketService.assignTicket(id, userId));
    }

    @PostMapping("/{id}/status")
    public ResponseEntity<TicketDto> updateStatus(@PathVariable Long id, @RequestParam TicketStatus status) {
        return ResponseEntity.ok(ticketService.updateTicketStatus(id, status));
    }
}
