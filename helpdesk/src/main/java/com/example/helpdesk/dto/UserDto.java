package com.example.helpdesk.dto;

import com.example.helpdesk.validation.UniqueEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String confirmPassword;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @UniqueEmail
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;
    private Long departmentId;
    private String departmentName;
}
