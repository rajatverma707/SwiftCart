package com.rv.support.controller;

import com.rv.support.dto.SupportTicketRequestDto;
import com.rv.support.dto.SupportTicketResponseDto;
import com.rv.support.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support/tickets")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping("/create")
    public ResponseEntity<SupportTicketResponseDto> create(@RequestBody SupportTicketRequestDto requestDto) {
        SupportTicketResponseDto created = supportTicketService.createTicket(requestDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public SupportTicketResponseDto get(@PathVariable Long id) {
        return supportTicketService.getTicket(id);
    }

    @GetMapping
    public List<SupportTicketResponseDto> getByCustomer(@RequestParam("email") String email) {
        return supportTicketService.getTicketsForCustomer(email);
    }

    @PatchMapping("/{id}/status")
    public SupportTicketResponseDto updateStatus(@PathVariable Long id,
                                                 @RequestParam("status") String status) {
        return supportTicketService.updateStatus(id, status);
    }
}
