package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.OrganizerDashboardResponse;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.OrganizerDashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/organizer/dashboard")
public class OrganizerDashboardController {

    private final OrganizerDashboardService organizerDashboardService;

    public OrganizerDashboardController(
            OrganizerDashboardService organizerDashboardService) {

        this.organizerDashboardService =
                organizerDashboardService;
    }

    @GetMapping
    public ResponseEntity<OrganizerDashboardResponse>
    getDashboard(Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        String organizerEmail = user.getEmail();

        return ResponseEntity.ok(
                organizerDashboardService.getDashboard(
                        organizerEmail
                )
        );
    }
}