package com.rv.support.service;

import com.rv.support.dto.SupportTicketRequestDto;
import com.rv.support.dto.SupportTicketResponseDto;
import com.rv.support.entity.SupportTicket;
import com.rv.support.entity.SupportTicket.Status;
import com.rv.support.exception.SupportTicketNotFoundException;
import com.rv.support.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportTicketServiceImpl implements SupportTicketService {

    private final SupportTicketRepository repository;
    private final WebClient webClient;

    @Value("${notification.service.url:http://localhost:8084}")
    private String notificationServiceUrl;

    @Override
    public SupportTicketResponseDto createTicket(SupportTicketRequestDto requestDto) {
        SupportTicket ticket = new SupportTicket();
        ticket.setCustomerEmail(requestDto.getCustomerEmail());
        ticket.setSubject(requestDto.getSubject());
        ticket.setDescription(requestDto.getDescription());
        ticket.setOrderTrackingNum(requestDto.getOrderTrackingNum());
        ticket.setPriority(requestDto.getPriority());
        ticket.setStatus(Status.OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket saved = repository.save(ticket);

        webClient.post()
                .uri(notificationServiceUrl + "/api/support/notify")
                .bodyValue(saved)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(ex -> {
                    return reactor.core.publisher.Mono.empty();
                })
                .subscribe();

        return mapToDto(saved);
    }

    @Override
    public SupportTicketResponseDto getTicket(Long id) {
        SupportTicket ticket = repository.findById(id)
                .orElseThrow(() -> new SupportTicketNotFoundException("Ticket not found"));
        return mapToDto(ticket);
    }

    @Override
    public List<SupportTicketResponseDto> getTicketsForCustomer(String customerEmail) {
        return repository.findByCustomerEmail(customerEmail)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SupportTicketResponseDto updateStatus(Long id, String status) {
        SupportTicket ticket = repository.findById(id)
                .orElseThrow(() -> new SupportTicketNotFoundException("Ticket not found"));
        ticket.setStatus(Status.valueOf(status));
        ticket.setUpdatedAt(LocalDateTime.now());
        SupportTicket saved = repository.save(ticket);
        return mapToDto(saved);
    }

    private SupportTicketResponseDto mapToDto(SupportTicket ticket) {
        SupportTicketResponseDto dto = new SupportTicketResponseDto();
        dto.setId(ticket.getId());
        dto.setCustomerEmail(ticket.getCustomerEmail());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setOrderTrackingNum(ticket.getOrderTrackingNum());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        return dto;
    }
}
