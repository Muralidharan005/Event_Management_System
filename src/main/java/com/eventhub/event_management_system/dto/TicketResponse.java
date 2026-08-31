package com.eventhub.event_management_system.dto;

import com.eventhub.event_management_system.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TicketResponse {

    private Long id;

    private String ticketNumber;

    private Long bookingId;

    private String bookingNumber;

    private String eventName;

    private String ticketType;

    private Integer quantity;

    private Double amount;

    private TicketStatus status;

    private LocalDateTime issuedAt;

    private LocalDateTime checkedInAt;
}