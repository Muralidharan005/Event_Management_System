package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.DashboardResponse;
import com.eventhub.event_management_system.entity.BookingStatus;
import com.eventhub.event_management_system.entity.PaymentStatus;
import com.eventhub.event_management_system.entity.Role;
import com.eventhub.event_management_system.entity.TicketStatus;
import com.eventhub.event_management_system.repository.BookingRepository;
import com.eventhub.event_management_system.repository.EventRepository;
import com.eventhub.event_management_system.repository.PaymentRepository;
import com.eventhub.event_management_system.repository.TicketRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;

    public DashboardService(
            UserRepository userRepository,
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            TicketRepository ticketRepository,
            PaymentRepository paymentRepository) {

        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
    }

    public DashboardResponse getDashboard() {

        long totalUsers =
                userRepository.count();

        long totalOrganizers =
                userRepository.countByRole(
                        Role.ORGANIZER
                );

        long totalNormalUsers =
                userRepository.countByRole(
                        Role.USER
                );

        long totalEvents =
                eventRepository.count();

        long totalBookings =
                bookingRepository.countByStatus(
                        BookingStatus.CONFIRMED
                );

        long totalTicketsSold =
                ticketRepository.countByStatus(
                        TicketStatus.ACTIVE
                )
                + ticketRepository.countByStatus(
                        TicketStatus.USED
                );

        Double revenue =
                paymentRepository.calculateRevenue(
                        PaymentStatus.SUCCESS
                );

        long totalCheckedIn =
                ticketRepository.countByStatus(
                        TicketStatus.USED
                );

        return new DashboardResponse(
                totalUsers,
                totalOrganizers,
                totalNormalUsers,
                totalEvents,
                totalBookings,
                totalTicketsSold,
                revenue != null ? revenue : 0.0,
                totalCheckedIn
        );
    }
}