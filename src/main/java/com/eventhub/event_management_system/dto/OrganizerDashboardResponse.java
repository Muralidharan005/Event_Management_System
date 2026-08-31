package com.eventhub.event_management_system.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrganizerDashboardResponse {

    private long totalEvents;

    private long totalBookings;

    private long totalTicketsSold;

    private double totalRevenue;

    private long totalCheckedIn;
}