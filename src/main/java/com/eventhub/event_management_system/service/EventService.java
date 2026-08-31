package com.eventhub.event_management_system.service;


import org.springframework.stereotype.Service;

import com.eventhub.event_management_system.dto.CreateEventRequest;
import com.eventhub.event_management_system.dto.EventResponse;
import com.eventhub.event_management_system.dto.UpdateEventRequest;
import com.eventhub.event_management_system.entity.Event;
import com.eventhub.event_management_system.entity.EventStatus;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.repository.EventRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    public EventResponse createEvent(
            CreateEventRequest request,
            String organizerEmail) {

        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Organizer not found"));

        Event event = new Event();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setCity(request.getCity());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setImageUrl(request.getImageUrl());
        event.setTotalCapacity(request.getTotalCapacity());

        // Initially all seats are available
        event.setAvailableSeats(request.getTotalCapacity());

        event.setStatus(EventStatus.UPCOMING);

        // Set logged-in organizer
        event.setOrganizer(organizer);

        Event savedEvent = eventRepository.save(event);

        return convertToResponse(savedEvent);
    }

    public List<EventResponse> getAllEvents() {

        return eventRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public EventResponse getEventById(Long id) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found with id: " + id));

        return convertToResponse(event);
    }

    public EventResponse updateEvent(
            Long id,
            UpdateEventRequest request,
            String organizerEmail) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found with id: " + id));

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException(
                    "You are not authorized to update this event");
        }

        int oldCapacity = event.getTotalCapacity();
        int oldAvailableSeats = event.getAvailableSeats();

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setVenue(request.getVenue());
        event.setCity(request.getCity());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setImageUrl(request.getImageUrl());
        event.setTotalCapacity(request.getTotalCapacity());

        // Adjust available seats according to capacity change
        int seatsBooked = oldCapacity - oldAvailableSeats;

        if (request.getTotalCapacity() < seatsBooked) {
            throw new RuntimeException(
                    "New capacity cannot be less than already booked seats");
        }

        event.setAvailableSeats(
                request.getTotalCapacity() - seatsBooked
        );

        Event updatedEvent = eventRepository.save(event);

        return convertToResponse(updatedEvent);
    }

    public void deleteEvent(
            Long id,
            String organizerEmail) {

        Event event = eventRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Event not found with id: " + id));

        if (!event.getOrganizer().getEmail().equals(organizerEmail)) {
            throw new RuntimeException(
                    "You are not authorized to delete this event");
        }

        eventRepository.delete(event);
    }

    private EventResponse convertToResponse(Event event) {

        return new EventResponse(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getVenue(),
                event.getCity(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getImageUrl(),
                event.getTotalCapacity(),
                event.getAvailableSeats(),
                event.getStatus(),
                event.getOrganizer().getId(),
                event.getOrganizer().getName()
        );
    }
    
    public List<EventResponse> getEventsByOrganizer(
            Long organizerId) {

        return eventRepository
                .findByOrganizerId(organizerId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
}
