package com.spring.luispa.booking.service;

import com.spring.luispa.booking.domain.Booking;
import com.spring.luispa.booking.domain.BookingStatus;
import com.spring.luispa.booking.domain.Event;
import com.spring.luispa.booking.dto.BookingRequest;
import com.spring.luispa.booking.dto.BookingResponse;
import com.spring.luispa.booking.exception.InsufficientTicketsException;
import com.spring.luispa.booking.repository.BookingRepository;
import com.spring.luispa.booking.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routing-key}")
    private String routingKey;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Attempting to book {} tickets for event {} by user {}",
                request.quantity(), request.eventId(), request.userId());

        // 1. Fetch event with Pessimistic Lock (prevents concurrent reads of the same row)
        Event event = eventRepository.findByIdWithLock(request.eventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        // 2. Check availability
        if (event.getAvailableTickets() < request.quantity()) {
            throw new InsufficientTicketsException("Only " + event.getAvailableTickets() + " tickets available");
        }

        // 3. Decrement tickets
        event.setAvailableTickets(event.getAvailableTickets() - request.quantity());

        // 4. Save event (The @Version field will be checked here. If another transaction
        // modified this event, it will throw ObjectOptimisticLockingFailureException)
        eventRepository.save(event);

        // 5. Create Booking record
        Booking booking = Booking.builder()
                .eventId(event.getId())
                .userId(request.userId())
                .quantity(request.quantity())
                .status(BookingStatus.CONFIRMED)
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking confirmed: {}", booking.getId());

        // 6. Publish event to RabbitMQ for async notification
        Map<String, Object> message = Map.of(
                "bookingId", booking.getId(),
                "userId", booking.getUserId(),
                "eventId", booking.getEventId(),
                "quantity", booking.getQuantity()
        );

        rabbitTemplate.convertAndSend(exchange, routingKey, message);
        log.info("Booking event published to RabbitMQ");

        return new BookingResponse(
                booking.getId(),
                booking.getEventId(),
                booking.getQuantity(),
                booking.getStatus(),
                "Booking successful"
        );
    }
}