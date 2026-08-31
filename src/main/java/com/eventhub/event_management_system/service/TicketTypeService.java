package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.CreateTicketTypeRequest;
import com.eventhub.event_management_system.dto.TicketTypeResponse;
import com.eventhub.event_management_system.entity.Event;
import com.eventhub.event_management_system.entity.TicketType;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.repository.EventRepository;
import com.eventhub.event_management_system.repository.TicketTypeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketTypeService {

    private final TicketTypeRepository ticketTypeRepository;
    private final EventRepository eventRepository;

    public TicketTypeService(
            TicketTypeRepository ticketTypeRepository,
            EventRepository eventRepository) {

        this.ticketTypeRepository = ticketTypeRepository;
        this.eventRepository = eventRepository;
    }

    public TicketTypeResponse createTicketType(
            Long eventId,
            CreateTicketTypeRequest request) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new RuntimeException("Event not found"));

        TicketType ticketType = new TicketType();

        ticketType.setName(request.getName());
        ticketType.setPrice(request.getPrice());
        ticketType.setTotalQuantity(request.getTotalQuantity());

        // Initially all tickets are available
        ticketType.setAvailableQuantity(
                request.getTotalQuantity()
        );

        ticketType.setEvent(event);

        TicketType savedTicket =
                ticketTypeRepository.save(ticketType);

        return convertToResponse(savedTicket);
    }

    public List<TicketTypeResponse> getTicketsByEvent(
            Long eventId) {

        return ticketTypeRepository
                .findByEventId(eventId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public TicketTypeResponse getTicketTypeById(Long id) {

        TicketType ticketType =
                ticketTypeRepository.findById(id)
                        .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ticket type not found"
                        ));

        return convertToResponse(ticketType);
    }

    private TicketTypeResponse convertToResponse(
            TicketType ticketType) {

        return new TicketTypeResponse(
                ticketType.getId(),
                ticketType.getName(),
                ticketType.getPrice(),
                ticketType.getTotalQuantity(),
                ticketType.getAvailableQuantity(),
                ticketType.getEvent().getId()
        );
    }
}