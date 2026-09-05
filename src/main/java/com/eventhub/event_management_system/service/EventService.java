package com.eventhub.event_management_system.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eventhub.event_management_system.dto.CreateEventRequest;
import com.eventhub.event_management_system.dto.EventResponse;
import com.eventhub.event_management_system.dto.PageResponse;
import com.eventhub.event_management_system.dto.UpdateEventRequest;
import com.eventhub.event_management_system.entity.BookingStatus;
import com.eventhub.event_management_system.entity.Event;
import com.eventhub.event_management_system.entity.EventStatus;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.repository.BookingRepository;
import com.eventhub.event_management_system.repository.EventRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public EventService(EventRepository eventRepository,
                        UserRepository userRepository,
                        BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
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

    @Transactional(readOnly = true)
    public PageResponse<EventResponse> getPaginatedEvents(
            int page,
            int size,
            String search,
            String category,
            String city,
            String sortBy,
            String sortDir) {

        String safeSortBy = switch (sortBy != null ? sortBy.toLowerCase() : "date") {
            case "title" -> "title";
            case "availableseats", "seats" -> "availableSeats";
            case "totalcapacity", "capacity" -> "totalCapacity";
            case "id" -> "id";
            case "city" -> "city";
            case "category" -> "category";
            default -> "eventDate";
        };

        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, safeSortBy);
        if (!"id".equals(safeSortBy)) {
            sort = sort.and(Sort.by(direction, "id"));
        }

        int pageIndex = Math.max(0, page);
        int pageSize = Math.max(1, Math.min(size, 100));
        Pageable pageable = PageRequest.of(pageIndex, pageSize, sort);

        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanCategory = (category != null && !category.trim().equalsIgnoreCase("ALL") && !category.trim().isEmpty())
                ? category.trim()
                : null;
        String cleanCity = (city != null && !city.trim().equalsIgnoreCase("ALL") && !city.trim().isEmpty())
                ? city.trim()
                : null;

        Page<Event> eventPage = eventRepository.searchAndFilterEvents(cleanSearch, cleanCategory, cleanCity, pageable);

        List<EventResponse> content = eventPage.getContent().stream()
                .map(this::convertToResponse)
                .toList();

        return PageResponse.<EventResponse>builder()
                .content(content)
                .pageNumber(eventPage.getNumber())
                .pageSize(eventPage.getSize())
                .totalElements(eventPage.getTotalElements())
                .totalPages(eventPage.getTotalPages())
                .last(eventPage.isLast())
                .first(eventPage.isFirst())
                .hasNext(eventPage.hasNext())
                .hasPrevious(eventPage.hasPrevious())
                .build();
    }

    public List<String> getAllCategories() {
        return eventRepository.findDistinctCategories();
    }

    public List<String> getAllCities() {
        return eventRepository.findDistinctCities();
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
        Long bookedTickets = bookingRepository.sumActiveBookedTicketsByEvent(
                event.getId(),
                BookingStatus.CANCELLED
        );
        int totalCap = event.getTotalCapacity() != null ? event.getTotalCapacity() : 0;
        int remainingSeats = Math.max(0, totalCap - (bookedTickets != null ? bookedTickets.intValue() : 0));

        // Sync with DB if out of sync
        if (event.getAvailableSeats() == null || !event.getAvailableSeats().equals(remainingSeats)) {
            event.setAvailableSeats(remainingSeats);
            eventRepository.save(event);
        }

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
                remainingSeats,
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
