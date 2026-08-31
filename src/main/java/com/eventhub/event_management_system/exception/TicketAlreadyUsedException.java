package com.eventhub.event_management_system.exception;

public class TicketAlreadyUsedException extends RuntimeException {

    public TicketAlreadyUsedException(String message) {
        super(message);
    }
}