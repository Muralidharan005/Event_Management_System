package com.eventhub.event_management_system.controller;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.eventhub.event_management_system.dto.CreateEventRequest;
import com.eventhub.event_management_system.dto.EventResponse;
import com.eventhub.event_management_system.dto.PageResponse;
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

    // Get All Events (Supports unpaginated list or paginated via ?page=0&size=6)
    @GetMapping
    public ResponseEntity<?> getAllEvents(
            @RequestParam(required = false) Integer page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        if (page != null) {
            return ResponseEntity.ok(
                    eventService.getPaginatedEvents(page, size, search, category, city, sortBy, sortDir)
            );
        }
        return ResponseEntity.ok(
                eventService.getAllEvents()
        );
    }

    // Get Paginated Events with filtering & sorting
    @GetMapping("/paginated")
    public ResponseEntity<PageResponse<EventResponse>> getPaginatedEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "eventDate") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        return ResponseEntity.ok(
                eventService.getPaginatedEvents(page, size, search, category, city, sortBy, sortDir)
        );
    }

    // Get Distinct Categories
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(eventService.getAllCategories());
    }

    // Get Distinct Cities
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(eventService.getAllCities());
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