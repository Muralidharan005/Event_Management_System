package com.eventhub.event_management_system.repository;

import com.eventhub.event_management_system.entity.Payment;
import com.eventhub.event_management_system.entity.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    Optional<Payment> findByBookingId(Long bookingId);

    boolean existsByBookingId(Long bookingId);
    
    @Query("""
    	       SELECT COALESCE(SUM(p.amount), 0)
    	       FROM Payment p
    	       WHERE p.status = :status
    	       """)
    	Double calculateRevenue(
    	        @Param("status") PaymentStatus status);
    
    @Query("""
    	       SELECT COALESCE(SUM(p.amount), 0)
    	       FROM Payment p
    	       WHERE p.booking.event.organizer.id = :organizerId
    	       AND p.status = :status
    	       """)
    	Double calculateOrganizerRevenue(
    	        @Param("organizerId") Long organizerId,
    	        @Param("status") PaymentStatus status
    	);
}