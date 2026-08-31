package com.eventhub.event_management_system.exception;

public class InsufficientTicketException extends RuntimeException {

    public InsufficientTicketException(String message) {
        super(message);
    }
}