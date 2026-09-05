package com.eventhub.event_management_system.config;

import com.eventhub.event_management_system.entity.*;
import com.eventhub.event_management_system.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

// Startup data seeding has been disabled
// @Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            UserRepository userRepository,
            EventRepository eventRepository,
            TicketTypeRepository ticketTypeRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.ticketTypeRepository = ticketTypeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Startup seeding disabled. No demo IDs or queries executed on server start.
    }

    /*
    // Previous demo seeding logic kept for reference:
    private void seedDemoData() {

        // 1. Seed demo organizer if none exists
        User organizer = userRepository.findByEmail("organizer@eventhub.com").orElse(null);
        if (organizer == null) {
            organizer = new User();
            organizer.setName("Apex Events Organizer");
            organizer.setEmail("organizer@eventhub.com");
            organizer.setPassword(passwordEncoder.encode("organizer123"));
            organizer.setPhone("9876543210");
            organizer.setRole(Role.ORGANIZER);
            organizer.setActive(true);
            organizer = userRepository.save(organizer);
        }

        // Demo Admin
        if (userRepository.findByEmail("admin@eventhub.com").isEmpty()) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail("admin@eventhub.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhone("9876543211");
            admin.setRole(Role.ADMIN);
            admin.setActive(true);
            userRepository.save(admin);
        }

        // Demo User
        if (userRepository.findByEmail("user@eventhub.com").isEmpty()) {
            User attendee = new User();
            attendee.setName("John Doe");
            attendee.setEmail("user@eventhub.com");
            attendee.setPassword(passwordEncoder.encode("user123"));
            attendee.setPhone("9876543212");
            attendee.setRole(Role.USER);
            attendee.setActive(true);
            userRepository.save(attendee);
        }

        // 2. Seed demo events if none exist in database
        if (eventRepository.count() == 0) {
            Event event1 = new Event();
            event1.setTitle("Global Tech & AI Summit 2026");
            event1.setDescription("Explore breakthrough advancements in AI, Cloud Architecture, and Web3 with world-class engineers and leaders.");
            event1.setCategory("Technology");
            event1.setVenue("Silicon Convention Center");
            event1.setCity("San Francisco");
            event1.setEventDate(LocalDate.now().plusDays(14));
            event1.setStartTime(LocalTime.of(9, 30));
            event1.setEndTime(LocalTime.of(17, 30));
            event1.setImageUrl("https://images.unsplash.com/photo-1540575467063-178a50c2df87?auto=format&fit=crop&w=1200&q=80");
            event1.setTotalCapacity(500);
            event1.setAvailableSeats(500);
            event1.setStatus(EventStatus.UPCOMING);
            event1.setOrganizer(organizer);
            event1 = eventRepository.save(event1);

            TicketType t1 = new TicketType(null, "General Admission", 49.0, 350, 350, event1);
            TicketType t2 = new TicketType(null, "VIP All-Access Pass", 149.0, 150, 150, event1);
            ticketTypeRepository.saveAll(List.of(t1, t2));

            Event event2 = new Event();
            event2.setTitle("Neon Pulse: Live Electronic Music Festival");
            event2.setDescription("A high-energy night of live synthwave, EDM, and acoustic sets with visual light installations and food trucks.");
            event2.setCategory("Music");
            event2.setVenue("Skyline Open Air Amphitheater");
            event2.setCity("Austin");
            event2.setEventDate(LocalDate.now().plusDays(21));
            event2.setStartTime(LocalTime.of(18, 0));
            event2.setEndTime(LocalTime.of(23, 30));
            event2.setImageUrl("https://images.unsplash.com/photo-1470225620780-dba8ba36b745?auto=format&fit=crop&w=1200&q=80");
            event2.setTotalCapacity(800);
            event2.setAvailableSeats(800);
            event2.setStatus(EventStatus.UPCOMING);
            event2.setOrganizer(organizer);
            event2 = eventRepository.save(event2);

            TicketType t3 = new TicketType(null, "Standard Pass", 35.0, 600, 600, event2);
            TicketType t4 = new TicketType(null, "Front Row VIP", 95.0, 200, 200, event2);
            ticketTypeRepository.saveAll(List.of(t3, t4));

            Event event3 = new Event();
            event3.setTitle("Modern Full-Stack Architecture Workshop");
            event3.setDescription("Intensive, code-along workshop covering Spring Boot, React, Docker, and PostgreSQL microservice patterns.");
            event3.setCategory("Workshop");
            event3.setVenue("DevSpace Innovation Hub");
            event3.setCity("Seattle");
            event3.setEventDate(LocalDate.now().plusDays(28));
            event3.setStartTime(LocalTime.of(10, 0));
            event3.setEndTime(LocalTime.of(16, 0));
            event3.setImageUrl("https://images.unsplash.com/photo-1517245386807-bb43f82c33c4?auto=format&fit=crop&w=1200&q=80");
            event3.setTotalCapacity(100);
            event3.setAvailableSeats(100);
            event3.setStatus(EventStatus.UPCOMING);
            event3.setOrganizer(organizer);
            event3 = eventRepository.save(event3);

            TicketType t5 = new TicketType(null, "Workshop Seat", 79.0, 100, 100, event3);
            ticketTypeRepository.save(t5);
        }
    }
    */
}

