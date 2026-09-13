package com.spring.luispa.booking.controller;

import com.spring.luispa.booking.dto.BookingRequest;
import com.spring.luispa.booking.dto.BookingResponse;
import com.spring.luispa.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody BookingRequest request,
            @RequestHeader(value = "X-User-Id", required = false) String userIdFromHeader,
            @RequestHeader(value = "X-User-Email", required = false) String userEmail,
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        // If userId came from the Gateway (JWT), use it; otherwise fall back to request body
        String effectiveUserId = (userIdFromHeader != null && !userIdFromHeader.isBlank())
                ? userIdFromHeader
                : request.userId();

        if (effectiveUserId == null || effectiveUserId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is required");
        }

        // Build a new request with the authenticated user's ID
        BookingRequest authenticatedRequest = new BookingRequest(
                request.eventId(),
                effectiveUserId,
                request.quantity()
        );

        BookingResponse response = bookingService.createBooking(authenticatedRequest);
        return ResponseEntity.ok(response);
    }
}
