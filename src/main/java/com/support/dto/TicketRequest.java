package com.support.dto;

import com.support.entity.Severity;
import com.support.entity.Severity;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketRequest {
    @NotBlank
    private String title;
    private String description;
    private String reporterId;
    private Severity severity;
}
