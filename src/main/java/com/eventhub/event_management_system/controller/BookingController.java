package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.BookingResponse;
import com.eventhub.event_management_system.dto.CreateBookingRequest;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService) {

        this.bookingService = bookingService;
    }

    // Create Booking
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        BookingResponse response =
                bookingService.createBooking(
                        request,
                        user.getEmail()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get My Bookings
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>>
    getMyBookings(
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                bookingService.getMyBookings(
                        user.getEmail()
                )
        );
    }

    // Get Booking
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse>
    getBookingById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                bookingService.getBookingById(id)
        );
    }

    // Cancel Booking
    @PutMapping("/{id}/cancel")
    public ResponseEntity<BookingResponse>
    cancelBooking(
            @PathVariable Long id,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                bookingService.cancelBooking(
                        id,
                        user.getEmail()
                )
        );
    }
}