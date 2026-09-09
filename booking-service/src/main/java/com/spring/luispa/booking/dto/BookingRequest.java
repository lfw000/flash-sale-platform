package com.spring.luispa.booking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BookingRequest(
        @NotNull(message = "Event ID is required")
        Long eventId,

        @NotNull(message = "User ID is required")
        String userId,

        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity
) {}
