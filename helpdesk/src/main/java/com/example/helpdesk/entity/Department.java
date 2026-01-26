package com.example.helpdesk.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Ticket> tickets = new ArrayList<>();
}
