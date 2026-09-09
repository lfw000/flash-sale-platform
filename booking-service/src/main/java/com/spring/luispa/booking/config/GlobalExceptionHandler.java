package com.spring.luispa.booking.config;

import com.spring.luispa.booking.exception.InsufficientTicketsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InsufficientTicketsException.class)
    public ResponseEntity<Map<String, Object>> handleInsufficientTickets(InsufficientTicketsException ex) {
        log.warn("Booking failed: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "INSUFFICIENT_TICKETS", ex.getMessage());
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(ObjectOptimisticLockingFailureException ex) {
        log.warn("Concurrency conflict detected: {}", ex.getMessage());
        return buildResponse(HttpStatus.CONFLICT, "CONCURRENT_MODIFICATION",
                "The event was modified by another user. Please try again.");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String code, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "status", status.value(),
                "error", code,
                "message", message
        ));
    }
}
