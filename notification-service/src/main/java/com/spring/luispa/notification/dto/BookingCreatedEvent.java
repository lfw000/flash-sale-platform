package com.spring.luispa.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingCreatedEvent {
    private String bookingId;
    private String userId;
    private Long eventId;
    private Integer quantity;
}
