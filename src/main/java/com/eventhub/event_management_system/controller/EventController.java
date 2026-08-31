package com.eventhub.event_management_system.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.eventhub.event_management_system.dto.CreateEventRequest;
import com.eventhub.event_management_system.dto.EventResponse;
import com.eventhub.event_management_system.dto.UpdateEventRequest;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.EventService;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    // Create Event - Organizer
    @PostMapping
    public ResponseEntity<EventResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            Authentication authentication) {

    	User user = (User) authentication.getPrincipal();

    	String organizerEmail = user.getEmail();

        EventResponse response =
                eventService.createEvent(request, organizerEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get All Events
    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {

        return ResponseEntity.ok(
                eventService.getAllEvents()
        );
    }

    // Get Event By ID
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                eventService.getEventById(id)
        );
    }

    // Update Event - Organizer
    @PutMapping("/{id}")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody UpdateEventRequest request,
            Authentication authentication) {

    	User user = (User) authentication.getPrincipal();

    	String organizerEmail = user.getEmail();

        EventResponse response =
                eventService.updateEvent(
                        id,
                        request,
                        organizerEmail
                );

        return ResponseEntity.ok(response);
    }

    // Delete Event - Organizer
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEvent(
            @PathVariable Long id,
            Authentication authentication) {

    	User user = (User) authentication.getPrincipal();

    	String organizerEmail = user.getEmail();

        eventService.deleteEvent(id, organizerEmail);

        return ResponseEntity.ok(
                "Event deleted successfully"
        );
    }
    
    @GetMapping("/my-events")
    public ResponseEntity<List<EventResponse>> getMyEvents(
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                eventService.getEventsByOrganizer(
                        user.getId()
                )
        );
    }
}