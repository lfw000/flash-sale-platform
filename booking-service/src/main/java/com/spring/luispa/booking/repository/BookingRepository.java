package com.spring.luispa.booking.repository;

import com.spring.luispa.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {

    List<Booking> findByUserId(String userId);

    List<Booking> findByEventId(Long eventId);
}
