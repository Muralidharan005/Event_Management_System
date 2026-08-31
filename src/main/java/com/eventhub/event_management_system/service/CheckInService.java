package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.CheckInResponse;
import com.eventhub.event_management_system.entity.Ticket;
import com.eventhub.event_management_system.entity.TicketStatus;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.exception.TicketAlreadyUsedException;
import com.eventhub.event_management_system.repository.TicketRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CheckInService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public CheckInService(
            TicketRepository ticketRepository,
            UserRepository userRepository) {

        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CheckInResponse checkIn(
            String qrCode,
            String organizerEmail) {

        // 1. Find organizer
        User organizer = userRepository
                .findByEmail(organizerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organizer not found"));

        // 2. Check organizer role
        if (organizer.getRole() == null ||
                !organizer.getRole()
                        .name()
                        .equals("ORGANIZER")) {

            throw new ResourceNotFoundException(
                    "Only organizer can perform check-in");
        }

        // 3. Find ticket using QR code
        Ticket ticket = ticketRepository
                .findByQrCode(qrCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Invalid QR code"));

        // 4. Check ticket status
        if (ticket.getStatus()
                == TicketStatus.CANCELLED) {

            throw new ResourceNotFoundException(
                    "Ticket has been cancelled");
        }

        // 5. Prevent duplicate check-in
        if (ticket.getStatus() == TicketStatus.USED) {

            throw new TicketAlreadyUsedException(
                    "Ticket has already been checked in"
            );
        }

        // 6. Mark ticket as USED
        ticket.setStatus(TicketStatus.USED);

        // 7. Store check-in time
        ticket.setCheckedInAt(
                LocalDateTime.now()
        );

        Ticket savedTicket =
                ticketRepository.save(ticket);

        return new CheckInResponse(
                "Check-in successful",

                savedTicket.getTicketNumber(),

                savedTicket.getBooking()
                        .getBookingNumber(),

                savedTicket.getBooking()
                        .getUser()
                        .getName(),

                savedTicket.getBooking()
                        .getEvent()
                        .getTitle(),

                savedTicket.getBooking()
                        .getTicketType()
                        .getName(),

                savedTicket.getStatus(),

                savedTicket.getCheckedInAt()
        );
    }
}