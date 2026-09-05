package com.eventhub.event_management_system.repository;

import com.eventhub.event_management_system.entity.Booking;
import com.eventhub.event_management_system.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository
        extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByUserIdOrderByBookingDateDesc(Long userId);

    List<Booking> findByUserIdOrderByIdDesc(Long userId);

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByEventOrganizerId(Long organizerId);

    List<Booking> findByEventIdAndEventOrganizerId(Long eventId, Long organizerId);

    List<Booking> findByStatus(BookingStatus status);
    
    long countByStatus(BookingStatus status);
    
    long countByEventOrganizerIdAndStatus(
            Long organizerId,
            BookingStatus status
    );
    
    @Query("""
    	       SELECT COALESCE(SUM(b.quantity), 0)
    	       FROM Booking b
    	       WHERE b.event.organizer.id = :organizerId
    	       AND b.status = :status
    	       """)
    	Long sumTicketsSold(
    	        @Param("organizerId") Long organizerId,
    	        @Param("status") BookingStatus status
    	);

    @Query("""
    	       SELECT COALESCE(SUM(b.quantity), 0)
    	       FROM Booking b
    	       WHERE b.event.id = :eventId
    	       AND b.status != :cancelledStatus
    	       """)
    	Long sumActiveBookedTicketsByEvent(
    	        @Param("eventId") Long eventId,
    	        @Param("cancelledStatus") BookingStatus cancelledStatus
    	);
}