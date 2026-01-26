package com.example.helpdesk.controller.api;

import com.example.helpdesk.dto.DashboardStatsDto;
import com.example.helpdesk.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsRestController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardStatsDto> getStats(Authentication authentication) {
        String username = authentication.getName();

        // Determine if user is a regular user (not admin or agent)
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isUser = !isAdmin && !isAgent;

        DashboardStatsDto stats = dashboardService.getDashboardStats(username, isUser);
        return ResponseEntity.ok(stats);
    }
}
