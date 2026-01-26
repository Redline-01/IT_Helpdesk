package com.example.helpdesk.config;

import com.example.helpdesk.entity.Department;
import com.example.helpdesk.entity.Role;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.*;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.repository.RoleRepository;
import com.example.helpdesk.repository.TicketRepository;
import com.example.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final TicketRepository ticketRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Initializing application data...");

        // Create Roles
        Role adminRole = createRoleIfNotExists("ROLE_ADMIN", "Administrator role with full access");
        Role agentRole = createRoleIfNotExists("ROLE_AGENT", "Agent role - can view all tickets and claim them");
        Role userRole = createRoleIfNotExists("ROLE_USER", "User role - can create and view own tickets");

        // Create Departments
        Department itDept = createDepartmentIfNotExists("IT Support", "Information Technology Support");
        Department hrDept = createDepartmentIfNotExists("HR", "Human Resources");
        Department financeDept = createDepartmentIfNotExists("Finance", "Finance Department");

        // Create Admin User
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .email("admin@helpdesk.com")
                    .firstName("Admin")
                    .lastName("User")
                    .phoneNumber("+1234567890")
                    .status(UserStatus.ACTIVE)
                    .department(itDept)
                    .roles(new HashSet<>(Set.of(adminRole)))
                    .build();
            userRepository.save(admin);
            log.info("Admin user created: username=admin, password=admin123");
        }

        // Create Regular User
        if (userRepository.findByUsername("user").isEmpty()) {
            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .email("user@helpdesk.com")
                    .firstName("John")
                    .lastName("Doe")
                    .phoneNumber("+1234567891")
                    .status(UserStatus.ACTIVE)
                    .department(hrDept)
                    .roles(new HashSet<>(Set.of(userRole)))
                    .build();
            userRepository.save(user);
            log.info("Regular user created: username=user, password=user123");
        }

        // Create Agent User
        if (userRepository.findByUsername("agent").isEmpty()) {
            User agent = User.builder()
                    .username("agent")
                    .password(passwordEncoder.encode("agent123"))
                    .email("agent@helpdesk.com")
                    .firstName("Jane")
                    .lastName("Smith")
                    .phoneNumber("+1234567892")
                    .status(UserStatus.ACTIVE)
                    .department(itDept)
                    .roles(new HashSet<>(Set.of(agentRole)))
                    .build();
            userRepository.save(agent);
            log.info("Agent user created: username=agent, password=agent123");
        }

        // Create Sample Tickets
        if (ticketRepository.count() == 0) {
            User creator = userRepository.findByUsername("user").orElseThrow();
            User assignee = userRepository.findByUsername("agent").orElseThrow();

            Ticket ticket1 = Ticket.builder()
                    .title("Computer won't start")
                    .description("My desktop computer is not turning on. I've tried pressing the power button multiple times but nothing happens.")
                    .status(TicketStatus.OPEN)
                    .priority(TicketPriority.HIGH)
                    .category(TicketCategory.HARDWARE)
                    .createdBy(creator)
                    .department(itDept)
                    .build();
            ticketRepository.save(ticket1);

            Ticket ticket2 = Ticket.builder()
                    .title("Cannot access email account")
                    .description("I forgot my email password and cannot reset it. Please help me regain access.")
                    .status(TicketStatus.IN_PROGRESS)
                    .priority(TicketPriority.MEDIUM)
                    .category(TicketCategory.EMAIL)
                    .createdBy(creator)
                    .assignedTo(assignee)
                    .department(itDept)
                    .build();
            ticketRepository.save(ticket2);

            Ticket ticket3 = Ticket.builder()
                    .title("Software installation request")
                    .description("Need Adobe Photoshop installed on my workstation for design work.")
                    .status(TicketStatus.OPEN)
                    .priority(TicketPriority.LOW)
                    .category(TicketCategory.SOFTWARE)
                    .createdBy(creator)
                    .department(itDept)
                    .build();
            ticketRepository.save(ticket3);

            Ticket ticket4 = Ticket.builder()
                    .title("Network connectivity issues")
                    .description("Internet connection keeps dropping every few minutes. Very frustrating!")
                    .status(TicketStatus.OPEN)
                    .priority(TicketPriority.URGENT)
                    .category(TicketCategory.NETWORK)
                    .createdBy(creator)
                    .department(itDept)
                    .build();
            ticketRepository.save(ticket4);

            Ticket ticket5 = Ticket.builder()
                    .title("Printer not working")
                    .description("The office printer is showing an error message and won't print anything.")
                    .status(TicketStatus.RESOLVED)
                    .priority(TicketPriority.MEDIUM)
                    .category(TicketCategory.PRINTER)
                    .createdBy(creator)
                    .assignedTo(assignee)
                    .department(itDept)
                    .build();
            ticketRepository.save(ticket5);

            log.info("Sample tickets created");
        }

        log.info("Data initialization completed!");
        log.info("===========================================");
        log.info("Test Accounts:");
        log.info("Admin - username: admin, password: admin123");
        log.info("User  - username: user, password: user123");
        log.info("Agent - username: agent, password: agent123");
        log.info("===========================================");
    }

    private Role createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name).orElseGet(() -> {
            Role role = Role.builder()
                    .name(name)
                    .description(description)
                    .build();
            return roleRepository.save(role);
        });
    }

    private Department createDepartmentIfNotExists(String name, String description) {
        return departmentRepository.findByName(name).orElseGet(() -> {
            Department department = Department.builder()
                    .name(name)
                    .description(description)
                    .build();
            return departmentRepository.save(department);
        });
    }
}
