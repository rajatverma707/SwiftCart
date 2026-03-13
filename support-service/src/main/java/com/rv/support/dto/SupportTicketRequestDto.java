package com.rv.support.dto;

import lombok.Data;

@Data
public class SupportTicketRequestDto {

    private String customerEmail;
    private String subject;
    private String description;
    private String orderTrackingNum;
    private String priority;
}
