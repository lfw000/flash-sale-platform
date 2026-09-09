package com.spring.luispa.booking.dto;

import com.spring.luispa.booking.domain.BookingStatus;

public record BookingResponse(
        String bookingId,
        Long eventId,
        Integer quantity,
        BookingStatus status,
        String message
    ) {}
