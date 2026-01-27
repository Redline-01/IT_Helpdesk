package com.example.helpdesk.repository;

import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.enums.TicketStatus;
import com.example.helpdesk.enums.TicketPriority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByStatusOrderByCreatedAtDesc(TicketStatus status);
    List<Ticket> findByPriorityOrderByCreatedAtDesc(TicketPriority priority);
    List<Ticket> findByCreatedBy_IdOrderByCreatedAtDesc(Long userId);
    List<Ticket> findByAssignedTo_IdOrderByCreatedAtDesc(Long userId);
    List<Ticket> findByDepartment_Id(Long departmentId);

    // Paginated methods
    Page<Ticket> findByCreatedBy_Id(Long userId, Pageable pageable);

    // Get all tickets ordered by creation date (newest first)
    List<Ticket> findAllByOrderByCreatedAtDesc();

    @Query("SELECT t FROM Ticket t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "ORDER BY t.createdAt DESC")
    List<Ticket> searchTickets(@Param("status") TicketStatus status,
                               @Param("priority") TicketPriority priority,
                               @Param("keyword") String keyword);

    long countByStatus(TicketStatus status);
    long countByPriority(TicketPriority priority);

    // User-specific counts
    long countByCreatedBy_Id(Long userId);
    long countByCreatedBy_IdAndStatus(Long userId, TicketStatus status);
    long countByCreatedBy_IdAndPriority(Long userId, TicketPriority priority);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedTo.id = :userId AND t.status != 'CLOSED'")
    long countActiveTicketsByAssignedTo(@Param("userId") Long userId);
}
