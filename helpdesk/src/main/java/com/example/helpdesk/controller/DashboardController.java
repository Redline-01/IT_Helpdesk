package com.example.helpdesk.controller;

import com.example.helpdesk.dto.DashboardStatsDto;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.service.DashboardService;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final TicketService ticketService;
    private final UserService userService;

    @GetMapping
    public String dashboard(Model model, Authentication authentication) {
        try {
            String username = authentication.getName();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isAgent = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
            boolean isUser = !isAdmin && !isAgent;

            DashboardStatsDto stats = dashboardService.getDashboardStats(username, isUser);
            model.addAttribute("stats", stats);

            try {
                if (isAdmin || isAgent) {
                    model.addAttribute("recentTickets", ticketService.getAllTickets());
                } else {
                    User user = userService.getUserByUsername(username);
                    model.addAttribute("recentTickets", ticketService.getTicketsByUser(user.getId()));
                }
            } catch (Exception e) {
                model.addAttribute("recentTickets", Collections.emptyList());
            }

            if (isAdmin) {
                return "dashboard/admin";
            }

            return "dashboard/index";
        } catch (Exception e) {
            model.addAttribute("error", "Error loading dashboard: " + e.getMessage());
            return "error/500";
        }
    }
}
