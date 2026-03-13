package com.rv.support.dto;

import com.rv.support.entity.SupportTicket.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupportTicketResponseDto {

    private Long id;
    private String customerEmail;
    private String subject;
    private String description;
    private String orderTrackingNum;
    private Status status;
    private String priority;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
