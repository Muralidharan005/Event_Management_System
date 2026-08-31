package com.eventhub.event_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TicketTypeResponse {

    private Long id;
    private String name;
    private Double price;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Long eventId;
}