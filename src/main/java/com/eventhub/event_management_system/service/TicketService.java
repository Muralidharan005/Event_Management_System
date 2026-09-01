package com.eventhub.event_management_system.service;

import com.eventhub.event_management_system.dto.TicketResponse;
import com.eventhub.event_management_system.entity.Booking;
import com.eventhub.event_management_system.entity.BookingStatus;
import com.eventhub.event_management_system.entity.Ticket;
import com.eventhub.event_management_system.entity.TicketStatus;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.exception.ResourceNotFoundException;
import com.eventhub.event_management_system.exception.UnauthorizedException;
import com.eventhub.event_management_system.repository.BookingRepository;
import com.eventhub.event_management_system.repository.TicketRepository;
import com.eventhub.event_management_system.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class TicketService {

	private final TicketRepository ticketRepository;
	private final BookingRepository bookingRepository;
	private final UserRepository userRepository;
	private final QrCodeService qrCodeService;

	public TicketService(TicketRepository ticketRepository, BookingRepository bookingRepository,
			UserRepository userRepository, QrCodeService qrCodeService) {

		this.ticketRepository = ticketRepository;
		this.bookingRepository = bookingRepository;
		this.userRepository = userRepository;
		this.qrCodeService = qrCodeService;
	}

	public TicketResponse generateTicket(Long bookingId, String userEmail) {

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Booking booking = bookingRepository.findById(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

		// Check ownership
		if (!booking.getUser().getId().equals(user.getId())) {

			throw new UnauthorizedException("You are not authorized");
		}

		// Payment must be successful
		if (booking.getStatus() != BookingStatus.CONFIRMED) {

			throw new RuntimeException("Payment is not completed");
		}

		// If ticket already exists for this booking, return the existing ticket
		if (ticketRepository.existsByBookingId(bookingId)) {
			Ticket existing = ticketRepository.findByBookingId(bookingId)
					.orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));
			return convertToResponse(existing);
		}

		Ticket ticket = new Ticket();

		String ticketNumber = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

		String qrCode = "QR-" + UUID.randomUUID().toString().replace("-", "");

		ticket.setTicketNumber(ticketNumber);
		ticket.setQrCode(qrCode);
		ticket.setBooking(booking);

		ticket.setStatus(TicketStatus.ACTIVE);

		ticket.setIssuedAt(LocalDateTime.now());

		Ticket savedTicket = ticketRepository.save(ticket);

		return convertToResponse(savedTicket);
	}

	private TicketResponse convertToResponse(Ticket ticket) {

		Booking booking = ticket.getBooking();

		return new TicketResponse(ticket.getId(), ticket.getTicketNumber(),

				booking.getId(), booking.getBookingNumber(),

				booking.getEvent().getTitle(),

				booking.getTicketType().getName(),

				booking.getQuantity(),

				booking.getTotalAmount(),

				ticket.getStatus(),

				ticket.getIssuedAt(),

				ticket.getCheckedInAt());
	}

	public byte[] generateQrCode(Long ticketId, String userEmail) {

		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

		if (!ticket.getBooking().getUser().getId().equals(user.getId())) {

			throw new UnauthorizedException("You are not authorized");
		}

		return qrCodeService.generateQrCode(ticket.getQrCode(), 300, 300);
	}

	public TicketResponse getTicket(Long ticketId, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Ticket ticket = ticketRepository.findById(ticketId)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found"));

		if (!ticket.getBooking().getUser().getId().equals(user.getId())) {
			throw new UnauthorizedException("You are not authorized");
		}

		return convertToResponse(ticket);
	}

	public TicketResponse getTicketByBookingId(Long bookingId, String userEmail) {
		User user = userRepository.findByEmail(userEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Ticket ticket = ticketRepository.findByBookingId(bookingId)
				.orElseThrow(() -> new ResourceNotFoundException("Ticket not found for booking: " + bookingId));

		if (!ticket.getBooking().getUser().getId().equals(user.getId())) {
			throw new UnauthorizedException("You are not authorized");
		}

		return convertToResponse(ticket);
	}
}