package com.spring.luispa.notification.service;

import com.spring.luispa.notification.dto.BookingCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendBookingConfirmation(BookingCreatedEvent event) {
        log.info(" ========================================");
        log.info(" SENDING BOOKING CONFIRMATION EMAIL");
        log.info(" ----------------------------------------");
        log.info(" To User ID:      {}", event.getUserId());
        log.info(" Booking ID:      {}", event.getBookingId());
        log.info(" Event ID:        {}", event.getEventId());
        log.info(" Tickets:         {}", event.getQuantity());
        log.info(" ========================================");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("Email sending was interrupted");
        }

        log.info("Email sent successfully for booking: {}", event.getBookingId());
    }
}
