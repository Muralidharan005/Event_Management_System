package com.eventhub.event_management_system.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.eventhub.event_management_system.dto.TicketResponse;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.TicketService;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Generate ticket for booking
    @PostMapping("/booking/{bookingId}")
    public ResponseEntity<TicketResponse> generateTicket(
            @PathVariable Long bookingId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        TicketResponse response =
                ticketService.generateTicket(
                        bookingId,
                        user.getEmail()
                );

        return ResponseEntity.ok(response);
    }

    // Get ticket by booking ID
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<TicketResponse> getTicketByBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        TicketResponse response =
                ticketService.getTicketByBookingId(
                        bookingId,
                        user.getEmail()
                );

        return ResponseEntity.ok(response);
    }

    // Get ticket by ticket ID
    @GetMapping("/{ticketId}")
    public ResponseEntity<TicketResponse> getTicket(
            @PathVariable Long ticketId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        TicketResponse response =
                ticketService.getTicket(
                        ticketId,
                        user.getEmail()
                );

        return ResponseEntity.ok(response);
    }

    // Generate QR code
    @GetMapping(
            value = "/{ticketId}/qr",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public ResponseEntity<byte[]> generateQrCode(
            @PathVariable Long ticketId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        byte[] qrCode =
                ticketService.generateQrCode(
                        ticketId,
                        user.getEmail()
                );

        return ResponseEntity.ok(qrCode);
    }
}