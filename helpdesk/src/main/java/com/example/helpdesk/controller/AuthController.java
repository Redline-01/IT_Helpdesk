package com.example.helpdesk.controller;

import com.example.helpdesk.dto.UserDto;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final DepartmentRepository departmentRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserDto());
        model.addAttribute("departments", departmentRepository.findAll());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("userDto") UserDto userDto,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // Server-side password confirmation validation
        if (userDto.getPassword() != null && userDto.getConfirmPassword() != null) {
            if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
                result.rejectValue("confirmPassword", "error.userDto", "Passwords do not match");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("departments", departmentRepository.findAll());
            return "auth/register";
        }

        try {
            userService.createUser(userDto, "ROLE_USER");
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/auth/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Registration failed: " + e.getMessage());
            return "redirect:/auth/register";
        }
    }
}
