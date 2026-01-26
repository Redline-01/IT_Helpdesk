package com.example.helpdesk.repository;

import com.example.helpdesk.entity.TicketComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketCommentRepository extends JpaRepository<TicketComment, Long> {
    List<TicketComment> findByTicket_IdOrderByCreatedAtDesc(Long ticketId);
    List<TicketComment> findByUser_Id(Long userId);
    long countByTicket_Id(Long ticketId);
}
