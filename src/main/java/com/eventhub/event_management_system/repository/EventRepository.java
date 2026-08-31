package com.eventhub.event_management_system.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.eventhub.event_management_system.entity.Event;
import com.eventhub.event_management_system.entity.EventStatus;

import java.time.LocalDate;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCityIgnoreCase(String city);

    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByEventDate(LocalDate eventDate);

    List<Event> findByTitleContainingIgnoreCase(String title);

    List<Event> findByOrganizerId(Long organizerId);
    
    long countByOrganizerId(Long organizerId);
}
