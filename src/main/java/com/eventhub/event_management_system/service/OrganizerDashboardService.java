package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.OrganizerDashboardResponse;
import com.eventhub.event_management_system.entity.BookingStatus;
import com.eventhub.event_management_system.entity.PaymentStatus;
import com.eventhub.event_management_system.entity.TicketStatus;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.repository.BookingRepository;
import com.eventhub.event_management_system.repository.EventRepository;
import com.eventhub.event_management_system.repository.PaymentRepository;
import com.eventhub.event_management_system.repository.TicketRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class OrganizerDashboardService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;

    public OrganizerDashboardService(
            UserRepository userRepository,
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            PaymentRepository paymentRepository,
            TicketRepository ticketRepository) {

        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
    }

    public OrganizerDashboardResponse getDashboard(
            String organizerEmail) {

        // Find logged-in organizer
        User organizer = userRepository
                .findByEmail(organizerEmail)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Organizer not found"
                        ));

        Long organizerId = organizer.getId();

        // Total events
        long totalEvents =
                eventRepository.countByOrganizerId(
                        organizerId
                );

        // Total confirmed bookings
        long totalBookings =
                bookingRepository
                        .countByEventOrganizerIdAndStatus(
                                organizerId,
                                BookingStatus.CONFIRMED
                        );

        // Total tickets sold
        Long ticketsSold =
                bookingRepository.sumTicketsSold(
                        organizerId,
                        BookingStatus.CONFIRMED
                );

        // Total revenue
        Double revenue =
                paymentRepository.calculateOrganizerRevenue(
                        organizerId,
                        PaymentStatus.SUCCESS
                );

        // Total checked-in
        long totalCheckedIn =
                ticketRepository
                        .countByBookingEventOrganizerIdAndStatus(
                                organizerId,
                                TicketStatus.USED
                        );

        return new OrganizerDashboardResponse(
                totalEvents,
                totalBookings,
                ticketsSold != null ? ticketsSold : 0,
                revenue != null ? revenue : 0.0,
                totalCheckedIn
        );
    }
}