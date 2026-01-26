package com.example.helpdesk.service;

import com.example.helpdesk.dto.TicketCreateDto;
import com.example.helpdesk.dto.TicketDto;
import com.example.helpdesk.dto.TicketUpdateDto;
import com.example.helpdesk.entity.Department;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.enums.TicketPriority;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.mapper.TicketMapper;
import com.example.helpdesk.repository.DepartmentRepository;
import com.example.helpdesk.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketService {

    private final TicketRepository ticketRepository;
    private final DepartmentRepository departmentRepository;
    private final TicketMapper ticketMapper;
    private final UserService userService;

    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        try {
            return ticketMapper.toDtoList(ticketRepository.findAllByOrderByCreatedAtDesc());
        } catch (Exception e) {
            log.error("Error mapping tickets: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    public TicketDto getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
        return ticketMapper.toDto(ticket);
    }

    @Transactional(readOnly = true)
    public Ticket getTicketEntityById(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));
    }

    @Transactional
    public TicketDto createTicket(TicketCreateDto ticketDto, String username) {
        User user = userService.getUserByUsername(username);

        Ticket ticket = ticketMapper.toEntity(ticketDto);
        ticket.setCreatedBy(user);
        ticket.setStatus(TicketStatus.OPEN);

        if (ticketDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(ticketDto.getDepartmentId())
                    .orElse(null);
            ticket.setDepartment(department);
        }

        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(savedTicket);
    }

    @Transactional
    public TicketDto updateTicket(Long id, TicketUpdateDto ticketDto) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        ticketMapper.updateEntity(ticketDto, ticket);

        if (ticketDto.getAssignedToId() != null) {
            User assignedUser = userService.getUserEntityById(ticketDto.getAssignedToId());
            ticket.setAssignedTo(assignedUser);
        }

        if (ticketDto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(ticketDto.getDepartmentId())
                    .orElse(null);
            ticket.setDepartment(department);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Transactional
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByStatus(TicketStatus status) {
        return ticketMapper.toDtoList(ticketRepository.findByStatusOrderByCreatedAtDesc(status));
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByPriority(TicketPriority priority) {
        return ticketMapper.toDtoList(ticketRepository.findByPriorityOrderByCreatedAtDesc(priority));
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByUser(Long userId) {
        return ticketMapper.toDtoList(ticketRepository.findByCreatedBy_IdOrderByCreatedAtDesc(userId));
    }

    @Transactional(readOnly = true)
    public List<TicketDto> getAssignedTickets(Long userId) {
        return ticketMapper.toDtoList(ticketRepository.findByAssignedTo_IdOrderByCreatedAtDesc(userId));
    }

    @Transactional(readOnly = true)
    public List<TicketDto> searchTickets(TicketStatus status, TicketPriority priority, String keyword) {
        try {
            return ticketMapper.toDtoList(ticketRepository.searchTickets(status, priority, keyword));
        } catch (Exception e) {
            log.error("Error searching tickets: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    @Transactional
    public TicketDto assignTicket(Long ticketId, Long userId) {
        Ticket ticket = getTicketEntityById(ticketId);
        User user = userService.getUserEntityById(userId);

        ticket.setAssignedTo(user);
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Transactional
    public TicketDto updateTicketStatus(Long ticketId, TicketStatus status) {
        Ticket ticket = getTicketEntityById(ticketId);
        ticket.setStatus(status);

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }

    @Transactional
    public TicketDto claimTicket(Long ticketId, Long agentId) {
        Ticket ticket = getTicketEntityById(ticketId);

        // Check if ticket is already assigned
        if (ticket.getAssignedTo() != null) {
            throw new RuntimeException("Ticket is already assigned to " + ticket.getAssignedTo().getUsername());
        }

        User agent = userService.getUserEntityById(agentId);
        ticket.setAssignedTo(agent);

        // Change status to IN_PROGRESS if it's OPEN
        if (ticket.getStatus() == TicketStatus.OPEN) {
            ticket.setStatus(TicketStatus.IN_PROGRESS);
        }

        Ticket updatedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDto(updatedTicket);
    }
}
