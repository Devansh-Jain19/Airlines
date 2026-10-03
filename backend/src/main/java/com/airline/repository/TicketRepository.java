package com.airline.repository;

import com.airline.entity.Ticket;
import com.airline.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    boolean existsByFlightFlightIdAndSeatSeatIdAndStatusNot(Long flightId, Long seatId, TicketStatus status);
    List<Ticket> findByBookingBookingId(Long bookingId);
    List<Ticket> findByFlightFlightId(Long flightId);
}
