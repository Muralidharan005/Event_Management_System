package com.eventhub.event_management_system.dto;

import com.eventhub.event_management_system.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String transactionId;

    private Long bookingId;
    private String bookingNumber;

    private Double amount;

    private String paymentMethod;

    private PaymentStatus status;

    private LocalDateTime paymentDate;
}