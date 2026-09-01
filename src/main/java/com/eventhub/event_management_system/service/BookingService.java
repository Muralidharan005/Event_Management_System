package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.BookingResponse;
import com.eventhub.event_management_system.dto.CreateBookingRequest;
import com.eventhub.event_management_system.entity.Booking;
import com.eventhub.event_management_system.entity.BookingStatus;
import com.eventhub.event_management_system.entity.TicketType;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.BookingAlreadyCancelledException;
import com.eventhub.event_management_system.exception.InsufficientTicketException;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.exception.UnauthorizedException;
import com.eventhub.event_management_system.repository.BookingRepository;
import com.eventhub.event_management_system.repository.PaymentRepository;
import com.eventhub.event_management_system.repository.TicketRepository;
import com.eventhub.event_management_system.repository.TicketTypeRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;

    public BookingService(
            BookingRepository bookingRepository,
            TicketTypeRepository ticketTypeRepository,
            UserRepository userRepository,
            TicketRepository ticketRepository,
            PaymentRepository paymentRepository) {

        this.bookingRepository = bookingRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public BookingResponse createBooking(
            CreateBookingRequest request,
            String userEmail) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found"
                ));

        // 2. Find ticket type
        TicketType ticketType =
                ticketTypeRepository.findById(
                        request.getTicketTypeId()
                ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Ticket type not found"
                ));

        // 3. Check ticket availability
        if (ticketType.getAvailableQuantity()
                < request.getQuantity()) {

            throw new InsufficientTicketException(
                    "Not enough tickets available"
            		);
        }

        // 4. Calculate total amount
        Double totalAmount =
                ticketType.getPrice()
                        * request.getQuantity();

        // 5. Reduce available tickets
        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity()
                        - request.getQuantity()
        );

        ticketTypeRepository.save(ticketType);

        // 6. Create booking
        Booking booking = new Booking();

        booking.setBookingNumber(
                "BK-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase()
        );

        booking.setUser(user);
        booking.setEvent(ticketType.getEvent());
        booking.setTicketType(ticketType);

        booking.setQuantity(request.getQuantity());
        booking.setTotalAmount(totalAmount);

        booking.setBookingDate(LocalDateTime.now());

        booking.setStatus(
                BookingStatus.PENDING
        );

        Booking savedBooking =
                bookingRepository.save(booking);

        return convertToResponse(savedBooking);
    }

    public List<BookingResponse> getMyBookings(
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() ->
                new ResourceNotFoundException(
                        "User not found"
                ));

        return bookingRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public BookingResponse getBookingById(Long id) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        ));

        return convertToResponse(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(
            Long id,
            String userEmail) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found"
                        ));

        if (!booking.getUser()
                .getEmail()
                .equals(userEmail)) {

            throw new UnauthorizedException(
                    "You are not authorized to cancel this booking"
            		);
        }

        if (booking.getStatus()
                == BookingStatus.CANCELLED) {

        	throw new BookingAlreadyCancelledException(
        	        "Booking is already cancelled"
        	);
        }

        // Return tickets to inventory
        TicketType ticketType =
                booking.getTicketType();

        ticketType.setAvailableQuantity(
                ticketType.getAvailableQuantity()
                        + booking.getQuantity()
        );

        ticketTypeRepository.save(ticketType);

        // Cancel booking
        booking.setStatus(
                BookingStatus.CANCELLED
        );

        Booking updatedBooking =
                bookingRepository.save(booking);

        return convertToResponse(updatedBooking);
    }

    public List<BookingResponse> getOrganizerBookings(String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByEventOrganizerId(organizer.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    public List<BookingResponse> getOrganizerEventBookings(Long eventId, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return bookingRepository.findByEventIdAndEventOrganizerId(eventId, organizer.getId())
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Transactional
    public void deleteBookingByOrganizer(Long bookingId, String organizerEmail) {
        User organizer = userRepository.findByEmail(organizerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!booking.getEvent().getOrganizer().getId().equals(organizer.getId())) {
            throw new UnauthorizedException("You are not authorized to delete bookings for this event");
        }

        // Return tickets to inventory if not already cancelled
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            TicketType ticketType = booking.getTicketType();
            ticketType.setAvailableQuantity(ticketType.getAvailableQuantity() + booking.getQuantity());
            ticketTypeRepository.save(ticketType);
        }

        // Delete associated ticket
        ticketRepository.findByBookingId(bookingId).ifPresent(ticketRepository::delete);

        // Delete associated payment
        paymentRepository.findByBookingId(bookingId).ifPresent(paymentRepository::delete);

        // Delete booking record
        bookingRepository.delete(booking);
    }

    private BookingResponse convertToResponse(
            Booking booking) {

        return new BookingResponse(
                booking.getId(),
                booking.getBookingNumber(),

                booking.getUser().getId(),
                booking.getUser().getName(),

                booking.getEvent().getId(),
                booking.getEvent().getTitle(),

                booking.getTicketType().getId(),
                booking.getTicketType().getName(),

                booking.getQuantity(),
                booking.getTotalAmount(),

                booking.getBookingDate(),
                booking.getStatus()
        );
    }
}