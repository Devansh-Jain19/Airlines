package com.airline.repository;

import com.airline.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByPassengerPassengerIdOrderByBookingDateDesc(Long passengerId);
    Optional<Booking> findByBookingReference(String bookingReference);
}
