package com.eventhub.event_management_system.repository;

import com.eventhub.event_management_system.entity.Ticket;
import com.eventhub.event_management_system.entity.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository
        extends JpaRepository<Ticket, Long> {

    Optional<Ticket> findByTicketNumber(String ticketNumber);

    Optional<Ticket> findByQrCode(String qrCode);

    Optional<Ticket> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
    
    long countByStatus(TicketStatus status);
    
    long countByBookingEventOrganizerIdAndStatus(
            Long organizerId,
            TicketStatus status
    );
}