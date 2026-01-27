package com.example.helpdesk.controller;

import com.example.helpdesk.dto.TicketCreateDto;
import com.example.helpdesk.dto.TicketDto;
import com.example.helpdesk.dto.TicketUpdateDto;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.TicketCategory;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.service.TicketCommentService;
import com.example.helpdesk.service.TicketService;
import com.example.helpdesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;
    private final TicketCommentService commentService;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    @GetMapping
    public String listTickets(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "10") int size,
                             Model model,
                             Authentication authentication) {
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isUser = !isAdmin && !isAgent;

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<TicketDto> ticketPage;

        if (isUser) {
            User user = userService.getUserByUsername(username);
            ticketPage = ticketService.getTicketsByUserPaginated(user.getId(), pageable);
        } else {
            ticketPage = ticketService.getAllTicketsPaginated(pageable);
        }

        model.addAttribute("tickets", ticketPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", ticketPage.getTotalPages());
        model.addAttribute("totalItems", ticketPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("currentUsername", username);
        model.addAttribute("isAdmin", isAdmin);
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
    public String viewTicket(@PathVariable Long id, Model model, Authentication authentication) {
        var ticket = ticketService.getTicketById(id);
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isAgent = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
        boolean isOwner = ticket.getCreatedByUsername() != null &&
                         ticket.getCreatedByUsername().equals(username);

        model.addAttribute("ticket", ticket);
        model.addAttribute("comments", commentService.getCommentsByTicketId(id));
        model.addAttribute("statuses", TicketStatus.values());
        model.addAttribute("currentUsername", username);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isAgent", isAgent);
        model.addAttribute("canEdit", isAdmin || isOwner);
        model.addAttribute("canDelete", isAdmin || isOwner);
        return "ticket/view";
    }

    @GetMapping("/{id}/edit")
    public String editTicketForm(@PathVariable Long id, Model model, Authentication authentication) {
        var ticketDto = ticketService.getTicketById(id);
        String username = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = ticketDto.getCreatedByUsername() != null &&
                         ticketDto.getCreatedByUsername().equals(username);

        // Only admin or owner can edit
        if (!isAdmin && !isOwner) {
            return "redirect:/access-denied";
        }

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
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        // Check ownership
        var existingTicket = ticketService.getTicketById(id);
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = existingTicket.getCreatedByUsername() != null &&
                         existingTicket.getCreatedByUsername().equals(username);

        if (!isAdmin && !isOwner) {
            return "redirect:/access-denied";
        }

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
    public String deleteTicket(@PathVariable Long id,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        // Check ownership
        var ticket = ticketService.getTicketById(id);
        String username = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isOwner = ticket.getCreatedByUsername() != null &&
                         ticket.getCreatedByUsername().equals(username);

        if (!isAdmin && !isOwner) {
            redirectAttributes.addFlashAttribute("error", "You don't have permission to delete this ticket.");
            return "redirect:/tickets/" + id;
        }

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
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model,
                               Authentication authentication) {
        try {
            String username = authentication.getName();

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            boolean isAgent = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_AGENT"));
            boolean isUser = !isAdmin && !isAgent;

            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<TicketDto> ticketPage;

            if (isUser) {
                // Filter to show only user's own tickets
                User user = userService.getUserByUsername(username);
                ticketPage = ticketService.searchTicketsByUserPaginated(user.getId(), status, priority, keyword, pageable);
            } else {
                ticketPage = ticketService.searchTicketsPaginated(status, priority, keyword, pageable);
            }

            model.addAttribute("tickets", ticketPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", ticketPage.getTotalPages());
            model.addAttribute("totalItems", ticketPage.getTotalElements());
            model.addAttribute("pageSize", size);
        } catch (Exception e) {
            model.addAttribute("tickets", java.util.Collections.emptyList());
            model.addAttribute("error", "Error searching tickets: " + e.getMessage());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalItems", 0);
            model.addAttribute("pageSize", size);
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

    // Comment endpoints
    @PostMapping("/{id}/comments")
    public String addComment(@PathVariable Long id,
                            @RequestParam String content,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            commentService.addComment(id, username, content);
            redirectAttributes.addFlashAttribute("success", "Comment added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add comment: " + e.getMessage());
        }
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/{ticketId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable Long ticketId,
                               @PathVariable Long commentId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            commentService.deleteComment(commentId, username, isAdmin);
            redirectAttributes.addFlashAttribute("success", "Comment deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete comment: " + e.getMessage());
        }
        return "redirect:/tickets/" + ticketId;
    }
}
