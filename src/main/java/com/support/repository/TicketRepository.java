package com.support.repository;

import com.support.entity.Ticket;
import com.support.entity.TicketStatus;
import java.util.List;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String title, String desc, Pageable pageable
    );

    int countByResolverIdAndStatus(Long resolverId, TicketStatus status);
    List<Ticket> findByReporterId(Long reporterId);

}
