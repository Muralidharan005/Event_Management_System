package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.CheckInRequest;
import com.eventhub.event_management_system.dto.CheckInResponse;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.CheckInService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/check-in")
public class CheckInController {

    private final CheckInService checkInService;

    public CheckInController(
            CheckInService checkInService) {

        this.checkInService = checkInService;
    }

    @PostMapping
    public ResponseEntity<CheckInResponse> checkIn(
            @Valid @RequestBody CheckInRequest request,
            Authentication authentication) {

        User organizer =
                (User) authentication.getPrincipal();

        CheckInResponse response =
                checkInService.checkIn(
                        request.getQrCode(),
                        organizer.getEmail()
                );

        return ResponseEntity.ok(response);
    }
}