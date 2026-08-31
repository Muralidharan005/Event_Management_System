package com.eventhub.event_management_system.controller;

import com.eventhub.event_management_system.dto.CreatePaymentRequest;
import com.eventhub.event_management_system.dto.PaymentResponse;
import com.eventhub.event_management_system.entity.User;
import com.eventhub.event_management_system.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService) {

        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> makePayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        PaymentResponse response =
                paymentService.makePayment(
                        request,
                        user.getEmail()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentResponse>
    getPaymentByBooking(
            @PathVariable Long bookingId,
            Authentication authentication) {

        User user =
                (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                paymentService.getPaymentByBooking(
                        bookingId,
                        user.getEmail()
                )
        );
    }
}