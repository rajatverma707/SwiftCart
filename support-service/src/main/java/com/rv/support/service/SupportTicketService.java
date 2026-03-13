package com.rv.support.service;

import com.rv.support.dto.SupportTicketRequestDto;
import com.rv.support.dto.SupportTicketResponseDto;

import java.util.List;

public interface SupportTicketService {

    SupportTicketResponseDto createTicket(SupportTicketRequestDto requestDto);

    SupportTicketResponseDto getTicket(Long id);

    List<SupportTicketResponseDto> getTicketsForCustomer(String customerEmail);

    SupportTicketResponseDto updateStatus(Long id, String status);
}
