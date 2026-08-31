package com.eventhub.event_management_system.dto;

import com.eventhub.event_management_system.entity.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@AllArgsConstructor
public class EventResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String venue;
    private String city;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String imageUrl;
    private Integer totalCapacity;
    private Integer availableSeats;
    private EventStatus status;

    private Long organizerId;
    private String organizerName;
}