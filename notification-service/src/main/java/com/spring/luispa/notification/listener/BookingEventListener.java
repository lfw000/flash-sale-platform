package com.spring.luispa.notification.listener;

import com.spring.luispa.notification.dto.BookingCreatedEvent;
import com.spring.luispa.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = "${app.rabbitmq.queue.name}")
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("Received booking event from RabbitMQ: bookingId={}", event.getBookingId());

        try {
            notificationService.sendBookingConfirmation(event);
        } catch (Exception e) {
            log.error("Failed to process booking event: {}", e.getMessage(), e);

            throw e;
        }
    }
}
