package com.support.controller;

import com.support.dto.CommentRequest;
import com.support.dto.TicketRequest;
import com.support.dto.TicketResponse;
import com.support.entity.Ticket;
import com.support.entity.Severity;
import com.support.mapper.DtoMapper;
import com.support.service.TicketService;

import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import com.support.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;



import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tickets")
@Validated
public class TicketController {

    private final TicketService svc;

    public TicketController(TicketService svc) {
        this.svc = svc;
    }

    // ===============================
    // CREATE TICKET (OLD VERSION — WILL UPDATE LATER)
    // ===============================
    @PostMapping
    public ResponseEntity<TicketResponse> create(@Valid @RequestBody TicketRequest req) {

        User employee = (User) SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal();

        Ticket t = svc.createTicket(req, employee);

        return ResponseEntity.ok(DtoMapper.toResponse(t));
    }


    // ===============================
    // LIST TICKETS (NEW SIGNATURE)
    // ===============================
    @GetMapping
    public ResponseEntity<Map<String,Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search
    ) {
        Page<Ticket> p = svc.listTickets(page, size, search);

        Map<String,Object> resp = Map.of(
                "items", p.getContent().stream().map(DtoMapper::toResponse).collect(Collectors.toList()),
                "page", p.getNumber(),
                "size", p.getSize(),
                "total", p.getTotalElements(),
                "totalPages", p.getTotalPages()
        );

        return ResponseEntity.ok(resp);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toResponse(svc.getTicket(id)));
    }

    @PostMapping("/{id}/comments")
    public ResponseEntity<TicketResponse> comment(@PathVariable Long id,
                                                  @Valid @RequestBody CommentRequest req) {
        return ResponseEntity.ok(DtoMapper.toResponse(svc.addComment(id, req)));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<TicketResponse> close(@PathVariable Long id) {
        return ResponseEntity.ok(DtoMapper.toResponse(svc.closeTicket(id)));
    }

    @PutMapping("/{id}/Severity")
    public ResponseEntity<TicketResponse> SetSeverity(@PathVariable Long id,
                                                      @RequestParam Severity p) {
        return ResponseEntity.ok(DtoMapper.toResponse(svc.changeSeverity(id, p)));
    }
    @GetMapping("/all")
    public ResponseEntity<?> allTickets() {
        return ResponseEntity.ok(
                svc.listAllTickets().stream()
                        .map(DtoMapper::toResponse)
                        .toList()
        );
    }
    @GetMapping("/my")
    public ResponseEntity<?> myTickets() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(401).body(Map.of("error","Not authenticated"));
            }

            Object principal = auth.getPrincipal();
            User user = null;

            // safe ways to get current user
            if (principal instanceof User) {
                user = (User) principal;
            } else if (principal instanceof org.springframework.security.core.userdetails.User) {
                // if you used Spring's UserDetails, get username (email) then lookup user
                String email = ((org.springframework.security.core.userdetails.User) principal).getUsername();
                user = svc.findUserByEmail(email); // implement findUserByEmail in service
            } else if (principal instanceof String) {
                // sometimes principal is the username string
                String email = (String) principal;
                user = svc.findUserByEmail(email);
            }

            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("error","Unable to locate authenticated user"));
            }

            var tickets = svc.getMyTickets(user.getId());
            var resp = tickets.stream().map(DtoMapper::toResponse).toList();
            return ResponseEntity.ok(resp);
        } catch (Exception ex) {
            ex.printStackTrace(); // prints stack trace in console for you
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

}
