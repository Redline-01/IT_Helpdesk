package com.example.helpdesk.controller;

import com.example.helpdesk.dto.DashboardStatsDto;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.service.DashboardService;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;
    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();
            log.info("Dashboard accessed by user: {}", username);

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isAgent = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
            boolean isUser = !isAdmin && !isAgent;

            // Get statistics based on role
            DashboardStatsDto stats = dashboardService.getDashboardStats(username, isUser);
            model.addAttribute("stats", stats);

            try {
                if (isAdmin || isAgent) {
                    // Admins and agents see all tickets
                    model.addAttribute("recentTickets", ticketService.getAllTickets());
                } else {
                    // Users see only their own tickets
                    User user = userService.getUserByUsername(username);
                    model.addAttribute("recentTickets", ticketService.getTicketsByUser(user.getId()));
                }
            } catch (Exception e) {
                log.error("Error loading tickets: {}", e.getMessage());
                model.addAttribute("recentTickets", Collections.emptyList());
            }

            log.info("User {} is admin: {}, is agent: {}, is user: {}", username, isAdmin, isAgent, isUser);

            if (isAdmin) {
                return "dashboard/admin";
            }

            return "dashboard/index";
        } catch (Exception e) {
            log.error("Error in dashboard controller: ", e);
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error/500";
        }
    }
}
