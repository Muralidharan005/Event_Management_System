package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.CreateTicketTypeRequest;
import com.eventhub.event_management_system.dto.TicketTypeResponse;
import com.eventhub.event_management_system.service.TicketTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events/{eventId}/tickets")
public class TicketTypeController {

    private final TicketTypeService ticketTypeService;

    public TicketTypeController(
            TicketTypeService ticketTypeService) {

        this.ticketTypeService = ticketTypeService;
    }

    @PostMapping
    public ResponseEntity<TicketTypeResponse> createTicketType(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateTicketTypeRequest request) {

        TicketTypeResponse response =
                ticketTypeService.createTicketType(
                        eventId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<TicketTypeResponse>>
    getTicketsByEvent(
            @PathVariable Long eventId) {

        return ResponseEntity.ok(
                ticketTypeService.getTicketsByEvent(eventId)
        );
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketTypeResponse>
    getTicketTypeById(
            @PathVariable Long ticketId) {

        return ResponseEntity.ok(
                ticketTypeService.getTicketTypeById(ticketId)
        );
    }
}