package com.example.helpdesk.dto;

import com.example.helpdesk.enums.UserStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    private UserStatus status;

    private Long departmentId;

    private String departmentName;

    private Set<String> roles;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    private int openTicketsCount;
    private int assignedTicketsCount;

}
