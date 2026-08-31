package com.eventhub.event_management_system.exception;

public class BookingAlreadyCancelledException
        extends RuntimeException {

    public BookingAlreadyCancelledException(String message) {
        super(message);
    }
}