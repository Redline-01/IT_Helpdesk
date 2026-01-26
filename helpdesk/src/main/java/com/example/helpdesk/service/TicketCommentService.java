package com.example.helpdesk.service;

import com.example.helpdesk.dto.TicketCommentDto;
import com.example.helpdesk.entity.Ticket;
import com.example.helpdesk.entity.TicketComment;
import com.example.helpdesk.entity.User;
import com.example.helpdesk.repository.TicketCommentRepository;
import com.example.helpdesk.repository.TicketRepository;
import com.example.helpdesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketCommentService {

    private final TicketCommentRepository commentRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TicketCommentDto> getCommentsByTicketId(Long ticketId) {
        return commentRepository.findByTicket_IdOrderByCreatedAtDesc(ticketId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TicketCommentDto addComment(Long ticketId, String username, String content) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        TicketComment comment = TicketComment.builder()
                .content(content)
                .ticket(ticket)
                .user(user)
                .build();

        TicketComment savedComment = commentRepository.save(comment);
        return toDto(savedComment);
    }

    @Transactional
    public void deleteComment(Long commentId, String username, boolean isAdmin) {
        TicketComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        // Only allow deletion if user is admin or comment owner
        if (!isAdmin && !comment.getUser().getUsername().equals(username)) {
            throw new RuntimeException("You don't have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional(readOnly = true)
    public long getCommentCountByTicketId(Long ticketId) {
        return commentRepository.countByTicket_Id(ticketId);
    }

    private TicketCommentDto toDto(TicketComment comment) {
        return TicketCommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .ticketId(comment.getTicket().getId())
                .userId(comment.getUser().getId())
                .username(comment.getUser().getUsername())
                .userFullName(comment.getUser().getFirstName() + " " + comment.getUser().getLastName())
                .build();
    }
}
