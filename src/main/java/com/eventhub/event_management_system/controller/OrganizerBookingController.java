package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.BookingResponse;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/organizer/bookings")
public class OrganizerBookingController {

    private final BookingService bookingService;

    public OrganizerBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // Get all bookings for all events of this organizer
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.getOrganizerBookings(user.getEmail()));
    }

    // Get all bookings for a specific event
    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<BookingResponse>> getEventBookings(
            @PathVariable Long eventId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(bookingService.getOrganizerEventBookings(eventId, user.getEmail()));
    }

    // Delete a booking / ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        bookingService.deleteBookingByOrganizer(id, user.getEmail());
        return ResponseEntity.noContent().build();
    }
}
