package com.example.helpdesk.controller;

import com.example.helpdesk.entity.Role;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.UserStatus;
import com.example.helpdesk.repository.RoleRepository;
import com.example.helpdesk.repository.TicketRepository;
import com.example.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TicketRepository ticketRepository;

    @GetMapping("/users")
    public String listUsers(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<User> userPage = userRepository.findAll(pageable);
        List<Role> roles = roleRepository.findAll();

        model.addAttribute("users", userPage.getContent());
        model.addAttribute("roles", roles);
        model.addAttribute("statuses", UserStatus.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", userPage.getTotalPages());
        model.addAttribute("totalItems", userPage.getTotalElements());
        model.addAttribute("pageSize", size);
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String updateUserRole(@PathVariable Long id,
                                 @RequestParam String roleName,
                                 RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Role not found"));

            // Clear existing roles and set new one
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success",
                    "User " + user.getUsername() + " role updated to " + roleName);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update role: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/status")
    public String updateUserStatus(@PathVariable Long id,
                                   @RequestParam UserStatus status,
                                   RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            user.setStatus(status);
            userRepository.save(user);

            redirectAttributes.addFlashAttribute("success",
                    "User " + user.getUsername() + " status updated to " + status);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    @Transactional
    public String deleteUser(@PathVariable Long id,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Don't allow deleting yourself
            if (user.getUsername().equals(authentication.getName())) {
                redirectAttributes.addFlashAttribute("error", "You cannot delete your own account!");
                return "redirect:/admin/users";
            }

            // Unassign tickets that are assigned to this user (don't delete them)
            List<Ticket> assignedTickets = ticketRepository.findByAssignedTo_IdOrderByCreatedAtDesc(user.getId());
            for (Ticket ticket : assignedTickets) {
                ticket.setAssignedTo(null);
                ticketRepository.save(ticket);
            }

            // Delete user - this will cascade delete their created tickets and comments
            String username = user.getUsername();
            int ticketCount = user.getCreatedTickets().size();
            userRepository.delete(user);

            redirectAttributes.addFlashAttribute("success",
                    "User " + username + " and their " + ticketCount + " ticket(s) deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete user: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
