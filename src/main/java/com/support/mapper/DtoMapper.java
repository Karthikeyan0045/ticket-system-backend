package com.support.mapper;

import com.support.dto.*;
import com.support.entity.Comment;
import com.support.entity.Ticket;
import com.support.entity.User;
import java.util.List;



import java.util.stream.Collectors;

public class DtoMapper {

    public static TicketResponse toResponse(Ticket t) {
        TicketResponse r = new TicketResponse();
        r.setId(t.getId());
        r.setTitle(t.getTitle());
        r.setDescription(t.getDescription());
        r.setSeverity(t.getSeverity());
        r.setStatus(t.getStatus());
        r.setReporterId(t.getReporterId());
        r.setCreatedAt(t.getCreatedAt());
        r.setUpdatedAt(t.getUpdatedAt());
        r.setComments(t.getComments().stream().map(DtoMapper::toCommentResponse).collect(Collectors.toList()));
        return r;
    }

    public static CommentResponse toCommentResponse(Comment c) {
        CommentResponse cr = new CommentResponse();
        cr.setId(c.getId());
        cr.setText(c.getText());
        cr.setAuthor(c.getAuthor());
        cr.setCreatedAt(c.getCreatedAt());
        return cr;
    }
}
