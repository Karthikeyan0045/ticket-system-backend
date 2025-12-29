package com.support.service;

import com.support.dto.CommentRequest;
import com.support.dto.TicketRequest;
import com.support.entity.*;
import com.support.repository.CommentRepository;
import com.support.repository.TicketRepository;
import com.support.repository.UserRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepo;
    private final CommentRepository commentRepo;
    private final UserRepository userRepo;

    public TicketService(TicketRepository ticketRepo,
                         CommentRepository commentRepo,
                         UserRepository userRepo) {

        this.ticketRepo = ticketRepo;
        this.commentRepo = commentRepo;
        this.userRepo = userRepo;
    }

    // ==========================================
    // CREATE TICKET WITH AUTO-ASSIGN
    // ==========================================
    public Ticket createTicket(TicketRequest req, User employee) {

        Ticket t = new Ticket();
        t.setTitle(req.getTitle());
        t.setDescription(req.getDescription());
        t.setReporterId(employee.getId());
        t.setSeverity(req.getSeverity());
        t.setCreatedAt(LocalDateTime.now());
        t.setUpdatedAt(LocalDateTime.now());

        Long resolverId = autoAssignResolver();

        if (resolverId != null) {
            t.setResolverId(resolverId);
            t.setStatus(TicketStatus.OPEN);
        } else {
            t.setStatus(TicketStatus.UNASSIGNED);
        }

        return ticketRepo.save(t);
    }

    // ==========================================
    // AUTO ASSIGN RESOLVER
    // ==========================================
    public Long autoAssignResolver() {

        List<User> resolvers = userRepo.findAll()
                .stream()
                .filter(u -> u.getRole() == UserRole.RESOLVER)
                .toList();

        User best = null;
        int minLoad = Integer.MAX_VALUE;

        for (User r : resolvers) {
            int load = ticketRepo.countByResolverIdAndStatus(r.getId(), TicketStatus.OPEN);

            if (load < 5 && load < minLoad) {
                minLoad = load;
                best = r;
            }
        }

        return best != null ? best.getId() : null;
    }

    // ==========================================
    // LIST TICKETS
    // ==========================================
    public Page<Ticket> listTickets(int page, int size, String search) {
        Pageable p = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (search != null && !search.isBlank()) {
            return ticketRepo.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(search, search, p);
        }

        return ticketRepo.findAll(p);
    }

    // ==========================================
    // GET ONE TICKET
    // ==========================================
    public Ticket getTicket(Long id) {
        return ticketRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    // ==========================================
    // ADD COMMENT
    // ==========================================
    @Transactional
    public Ticket addComment(Long ticketId, CommentRequest req) {
        Ticket ticket = getTicket(ticketId);

        Comment c = new Comment();
        c.setText(req.getText());
        c.setAuthor(req.getAuthor());
        c.setTicket(ticket);

        commentRepo.save(c);

        ticket.getComments().add(c);
        ticket.setUpdatedAt(LocalDateTime.now());

        return ticketRepo.save(ticket);
    }

    // ==========================================
    // CLOSE TICKET
    // ==========================================
    public Ticket closeTicket(Long id) {
        Ticket t = getTicket(id);
        t.setStatus(TicketStatus.CLOSED);
        t.setUpdatedAt(LocalDateTime.now());
        return ticketRepo.save(t);
    }

    // ==========================================
    // CHANGE SEVERITY
    // ==========================================
    public Ticket changeSeverity(Long id, Severity severity) {
        Ticket t = getTicket(id);
        t.setSeverity(severity);
        t.setUpdatedAt(LocalDateTime.now());
        return ticketRepo.save(t);
    }
    // get my tickets
    public List<Ticket> getMyTickets(Long userId) {
        return ticketRepo.findByReporterId(userId);
    }
    public User findUserByEmail(String email) {
        return userRepo.findByEmail(email).orElse(null);
    }
    public List<Ticket> listAllTickets() {
        return ticketRepo.findAll();
    }


}
