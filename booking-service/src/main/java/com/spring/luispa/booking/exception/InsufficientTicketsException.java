package com.spring.luispa.booking.exception;

public class InsufficientTicketsException extends RuntimeException {

    public InsufficientTicketsException(String message) {
        super(message);
    }
}
