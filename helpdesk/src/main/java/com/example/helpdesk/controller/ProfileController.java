package com.example.helpdesk.controller;

import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("departments", departmentRepository.findAll());
        return "profile/view";
    }

    @PostMapping("/update")
    public String updateProfile(@RequestParam String firstName,
                               @RequestParam String lastName,
                               @RequestParam String email,
                               @RequestParam(required = false) String phoneNumber,
                               @RequestParam(required = false) Long departmentId,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Check if email is already used by another user
            if (!user.getEmail().equals(email)) {
                if (userRepository.findByEmail(email).isPresent()) {
                    redirectAttributes.addFlashAttribute("error", "Email is already in use by another account");
                    return "redirect:/profile";
                }
            }

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setPhoneNumber(phoneNumber);

            if (departmentId != null) {
                departmentRepository.findById(departmentId)
                        .ifPresent(user::setDepartment);
            }

            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update profile: " + e.getMessage());
        }
        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                @RequestParam String newPassword,
                                @RequestParam String confirmPassword,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            String username = authentication.getName();
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify current password
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                redirectAttributes.addFlashAttribute("error", "Current password is incorrect");
                return "redirect:/profile";
            }

            // Check new passwords match
            if (!newPassword.equals(confirmPassword)) {
                redirectAttributes.addFlashAttribute("error", "New passwords do not match");
                return "redirect:/profile";
            }

            // Check password length
            if (newPassword.length() < 6) {
                redirectAttributes.addFlashAttribute("error", "New password must be at least 6 characters");
                return "redirect:/profile";
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to change password: " + e.getMessage());
        }
        return "redirect:/profile";
    }
}
