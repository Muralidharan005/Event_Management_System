package com.eventhub.event_management_system.dto;

import com.eventhub.event_management_system.entity.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CheckInResponse {

    private String message;

    private String ticketNumber;

    private String bookingNumber;

    private String userName;

    private String eventName;

    private String ticketType;

    private TicketStatus status;

    private LocalDateTime checkedInAt;
}