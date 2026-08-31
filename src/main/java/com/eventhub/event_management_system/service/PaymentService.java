package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.CreatePaymentRequest;
import com.eventhub.event_management_system.dto.PaymentResponse;
import com.eventhub.event_management_system.entity.Booking;
import com.eventhub.event_management_system.entity.BookingStatus;
import com.eventhub.event_management_system.entity.Payment;
import com.eventhub.event_management_system.entity.PaymentStatus;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.exception.UnauthorizedException;
import com.eventhub.event_management_system.repository.BookingRepository;
import com.eventhub.event_management_system.repository.PaymentRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TicketService ticketService;

    

    public PaymentService(PaymentRepository paymentRepository, BookingRepository bookingRepository,
			UserRepository userRepository, TicketService ticketService) {
		this.paymentRepository = paymentRepository;
		this.bookingRepository = bookingRepository;
		this.userRepository = userRepository;
		this.ticketService = ticketService;
	}

	@Transactional
    public PaymentResponse makePayment(
            CreatePaymentRequest request,
            String userEmail) {

        User user = userRepository
                .findByEmail(userEmail)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found"
                ));

        Booking booking = bookingRepository
                .findById(request.getBookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Booking not found"));

        // Check booking ownership
        if (!booking.getUser().getId()
                .equals(user.getId())) {

            throw new ResourceNotFoundException(
                    "You are not authorized to pay for this booking"
            );
        }

        // Check booking status
        if (booking.getStatus()
                != BookingStatus.PENDING) {

            throw new ResourceNotFoundException(
                    "Booking is not pending payment"
            );
        }

        // Prevent duplicate payment
        if (paymentRepository
                .existsByBookingId(booking.getId())) {

            throw new RuntimeException(
                    "Payment already exists for this booking"
            );
        }

        Payment payment = new Payment();

        payment.setTransactionId(
                "TXN-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        payment.setBooking(booking);

        payment.setAmount(
                booking.getTotalAmount()
        );

        payment.setPaymentMethod(
                request.getPaymentMethod()
        );

        payment.setPaymentDate(
                LocalDateTime.now()
        );

        // For development/demo
        payment.setStatus(
                PaymentStatus.SUCCESS
        );

        // Payment successful
        booking.setStatus(
                BookingStatus.CONFIRMED
        );

        bookingRepository.save(booking);

        Payment savedPayment =
                paymentRepository.save(payment);

        // Generate ticket automatically after successful payment
        ticketService.generateTicket(
                booking.getId(),
                userEmail
        );

        return convertToResponse(savedPayment);
    }

    public PaymentResponse getPaymentByBooking(
            Long bookingId,
            String userEmail) {

        Payment payment =
                paymentRepository
                        .findByBookingId(bookingId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Payment not found"));

        if (!payment.getBooking()
                .getUser()
                .getEmail()
                .equals(userEmail)) {

            throw new UnauthorizedException(
                    "You are not authorized"
            		);
        }

        return convertToResponse(payment);
    }

    private PaymentResponse convertToResponse(
            Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getTransactionId(),

                payment.getBooking().getId(),
                payment.getBooking().getBookingNumber(),

                payment.getAmount(),

                payment.getPaymentMethod(),

                payment.getStatus(),

                payment.getPaymentDate()
        );
    }
}