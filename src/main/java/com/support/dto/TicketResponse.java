package com.support.dto;

import com.support.entity.TicketStatus;
import lombok.Data;
import com.support.entity.Severity;


import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketResponse {
    private Long id;
    private String title;
    private String description;
    private Severity severity;
    private TicketStatus status;
    private Long reporterId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponse> comments;
}
