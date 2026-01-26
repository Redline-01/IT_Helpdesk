package com.example.helpdesk.controller;

import com.example.helpdesk.dto.TicketCreateDto;
import com.example.helpdesk.dto.TicketUpdateDto;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.TicketCategory;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
@Slf4j
public class TicketController {

    private final TicketService ticketService;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public String listTickets(Model model, Authentication authentication) {
        String username = authentication.getName();

        log.info("=== Ticket List Access ===");
        log.info("Username: {}", username);
        log.info("Authorities: {}", authentication.getAuthorities());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isUser = !isAdmin && !isAgent;

        log.info("isAdmin: {}, isAgent: {}, isUser: {}", isAdmin, isAgent, isUser);

        if (isUser) {
            // Regular users see only their own tickets
            User user = userService.getUserByUsername(username);
            var tickets = ticketService.getTicketsByUser(user.getId());
            log.info("USER - Loading {} tickets for user {}", tickets.size(), username);
            model.addAttribute("tickets", tickets);
        } else {
            // Agents and Admins see all tickets
            var tickets = ticketService.getAllTickets();
            log.info("AGENT/ADMIN - Loading all {} tickets", tickets.size());
            model.addAttribute("tickets", tickets);
        }
        return "ticket/list";
    }

    @GetMapping("/create")
    public String createTicketForm(Model model) {
        model.addAttribute("ticketDto", new TicketCreateDto());
        model.addAttribute("priorities", TicketPriority.values());
        model.addAttribute("categories", TicketCategory.values());
        model.addAttribute("departments", departmentRepository.findAll());
        return "ticket/create";
    }

    @PostMapping("/create")
    public String createTicket(@Valid @ModelAttribute("ticketDto") TicketCreateDto ticketDto,
                              BindingResult result,
                              Authentication authentication,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("priorities", TicketPriority.values());
            model.addAttribute("categories", TicketCategory.values());
            model.addAttribute("departments", departmentRepository.findAll());
            return "ticket/create";
        }

        try {
            String username = authentication.getName();
            ticketService.createTicket(ticketDto, username);
            redirectAttributes.addFlashAttribute("success", "Ticket created successfully!");
            return "redirect:/tickets";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create ticket: " + e.getMessage());
            return "redirect:/tickets/create";
        }
    }

    @GetMapping("/{id}")
    public String viewTicket(@PathVariable Long id, Model model) {
        model.addAttribute("ticket", ticketService.getTicketById(id));
        return "ticket/view";
    }

    @GetMapping("/{id}/edit")
    public String editTicketForm(@PathVariable Long id, Model model) {
        var ticketDto = ticketService.getTicketById(id);

        // Create TicketUpdateDto from TicketDto
        TicketUpdateDto updateDto = new TicketUpdateDto();
        updateDto.setTitle(ticketDto.getTitle());
        updateDto.setDescription(ticketDto.getDescription());
        updateDto.setStatus(ticketDto.getStatus());
        updateDto.setPriority(ticketDto.getPriority());
        updateDto.setCategory(ticketDto.getCategory());
        updateDto.setAssignedToId(ticketDto.getAssignedToUsername() != null ?
            userService.getAllUsers().stream()
                .filter(u -> u.getUsername().equals(ticketDto.getAssignedToUsername()))
                .findFirst()
                .map(u -> u.getId())
                .orElse(null) : null);
        updateDto.setDepartmentId(ticketDto.getDepartmentName() != null ?
            departmentRepository.findAll().stream()
                .filter(d -> d.getName().equals(ticketDto.getDepartmentName()))
                .findFirst()
                .map(d -> d.getId())
                .orElse(null) : null);

        model.addAttribute("ticket", ticketDto); // For display purposes
        model.addAttribute("ticketDto", updateDto); // For form binding
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("priorities", TicketPriority.values());
        model.addAttribute("categories", TicketCategory.values());
        model.addAttribute("departments", departmentRepository.findAll());
        model.addAttribute("users", userService.getAllUsers());
        return "ticket/edit";
    }

    @PostMapping("/{id}/edit")
    public String updateTicket(@PathVariable Long id,
                              @Valid @ModelAttribute("ticketDto") TicketUpdateDto ticketDto,
                              BindingResult result,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("ticket", ticketService.getTicketById(id));
            model.addAttribute("statuses", TicketStatus.values());
            model.addAttribute("priorities", TicketPriority.values());
            model.addAttribute("categories", TicketCategory.values());
            model.addAttribute("departments", departmentRepository.findAll());
            model.addAttribute("users", userService.getAllUsers());
            return "ticket/edit";
        }

        try {
            ticketService.updateTicket(id, ticketDto);
            redirectAttributes.addFlashAttribute("success", "Ticket updated successfully!");
            return "redirect:/tickets/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update ticket: " + e.getMessage());
            return "redirect:/tickets/" + id + "/edit";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteTicket(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            ticketService.deleteTicket(id);
            redirectAttributes.addFlashAttribute("success", "Ticket deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete ticket: " + e.getMessage());
        }
        return "redirect:/tickets";
    }

    @GetMapping("/search")
    public String searchTickets(@RequestParam(required = false) TicketStatus status,
                               @RequestParam(required = false) TicketPriority priority,
                               @RequestParam(required = false) String keyword,
                               Model model,
                               Authentication authentication) {
        try {
            String username = authentication.getName();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isAgent = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
            boolean isUser = !isAdmin && !isAgent;

            var tickets = ticketService.searchTickets(status, priority, keyword);

            if (isUser) {
                // Filter to show only user's own tickets
                User user = userService.getUserByUsername(username);
                tickets = tickets.stream()
                        .filter(t -> t.getCreatedByUsername() != null &&
                                   t.getCreatedByUsername().equals(username))
                        .toList();
            }

            model.addAttribute("tickets", tickets != null ? tickets : java.util.Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("tickets", java.util.Collections.emptyList());
            model.addAttribute("error", "Error searching tickets: " + e.getMessage());
        }

        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("priorities", TicketPriority.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "ticket/search";
    }

    @PostMapping("/{id}/claim")
    public String claimTicket(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User agent = userService.getUserByUsername(username);
            ticketService.claimTicket(id, agent.getId());
            redirectAttributes.addFlashAttribute("success", "Ticket claimed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to claim ticket: " + e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{id}/status")
    public String updateTicketStatus(@PathVariable Long id,
                                     @RequestParam TicketStatus status,
                                     RedirectAttributes redirectAttributes) {
        try {
            ticketService.updateTicketStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Ticket status updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }
}
