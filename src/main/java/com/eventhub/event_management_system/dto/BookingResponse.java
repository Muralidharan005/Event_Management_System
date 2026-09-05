package com.eventhub.event_management_system.dto;

import com.eventhub.event_management_system.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {

    private Long id;
    private String bookingNumber;

    private Long userId;
    private String userName;

    private Long eventId;
    private String eventName;
    private String eventCategory;

    private Long ticketTypeId;
    private String ticketTypeName;

    private Integer quantity;
    private Double totalAmount;

    private LocalDateTime bookingDate;

    private BookingStatus status;
}