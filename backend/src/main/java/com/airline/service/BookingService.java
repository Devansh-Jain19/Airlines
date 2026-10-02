package com.airline.service;

import com.airline.dto.request.BookingRequest;
import com.airline.dto.request.SegmentRequest;
import com.airline.dto.response.BookingResponseDto;
import com.airline.entity.*;
import com.airline.entity.enums.BookingStatus;
import com.airline.entity.enums.SeatClass;
import com.airline.entity.enums.TicketStatus;
import com.airline.exception.ResourceNotFoundException;
import com.airline.exception.SeatUnavailableException;
import com.airline.mapper.EntityDtoMapper;
import com.airline.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    @Autowired private BookingRepository bookingRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private FlightRepository flightRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private PassengerRepository passengerRepository;

    @Transactional(rollbackFor = Exception.class)
    public BookingResponseDto createBooking(BookingRequest request) {
        Passenger passenger = passengerRepository.findById(request.getPassengerId())
            .orElseThrow(() -> new ResourceNotFoundException("Passenger not found with ID: " + request.getPassengerId()));

        List<SegmentRequest> segments = new ArrayList<>();
        if (request.getSegments() != null && !request.getSegments().isEmpty()) {
            segments.addAll(request.getSegments());
        } else if (request.getFlightId() != null && request.getSeatIds() != null && !request.getSeatIds().isEmpty()) {
            for (Long seatId : request.getSeatIds()) {
                segments.add(new SegmentRequest(request.getFlightId(), seatId));
            }
        }

        if (segments.isEmpty()) {
            throw new IllegalArgumentException("Booking must contain at least one flight segment and seat.");
        }

        Flight primaryFlight = flightRepository.findById(segments.get(0).getFlightId())
            .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + segments.get(0).getFlightId()));

        Booking booking = new Booking();
        booking.setBookingReference(generatePnr());
        booking.setPassenger(passenger);
        booking.setFlight(primaryFlight);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING);
        booking = bookingRepository.save(booking);

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<Ticket> tickets = new ArrayList<>();

        for (SegmentRequest seg : segments) {
            Flight flight = flightRepository.findById(seg.getFlightId())
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + seg.getFlightId()));

            Seat seat = seatRepository.findById(seg.getSeatId())
                .orElseThrow(() -> new ResourceNotFoundException("Seat not found with ID: " + seg.getSeatId()));

            if (!seat.getAircraft().getAircraftId().equals(flight.getAircraft().getAircraftId())) {
                throw new IllegalArgumentException("Seat " + seat.getSeatNumber() + " does not belong to the flight's aircraft.");
            }

            if (ticketRepository.existsByFlightFlightIdAndSeatSeatIdAndStatusNot(
                    flight.getFlightId(), seat.getSeatId(), TicketStatus.CANCELLED)) {
                throw new SeatUnavailableException("Seat " + seat.getSeatNumber() + " is already booked for flight " + flight.getFlightNumber());
            }

            BigDecimal fare = flight.getBasePrice();
            if (seat.getSeatClass() == SeatClass.BUSINESS) {
                fare = fare.multiply(new BigDecimal("2.50"));
            } else if (seat.getSeatClass() == SeatClass.FIRST) {
                fare = fare.multiply(new BigDecimal("4.00"));
            }

            Ticket ticket = new Ticket();
            ticket.setTicketNumber("TK" + System.currentTimeMillis() + (tickets.size() + 1));
            ticket.setBooking(booking);
            ticket.setFlight(flight);
            ticket.setSeat(seat);
            ticket.setFare(fare);
            ticket.setStatus(TicketStatus.PENDING);

            try {
                ticket = ticketRepository.save(ticket);
            } catch (DataIntegrityViolationException ex) {
                throw new SeatUnavailableException("Concurrent booking collision: Seat " + seat.getSeatNumber() + " was just claimed by another user.");
            }

            totalAmount = totalAmount.add(fare);
            tickets.add(ticket);
        }

        booking.setTotalAmount(totalAmount);
        bookingRepository.save(booking);

        return EntityDtoMapper.toBookingResponseDto(booking, tickets, calculateAvailableSeats(primaryFlight));
    }

    @Transactional(readOnly = true)
    public List<BookingResponseDto> getPassengerBookings(Long passengerId) {
        List<Booking> bookings = bookingRepository.findByPassengerPassengerIdOrderByBookingDateDesc(passengerId);
        return bookings.stream().map(booking -> {
            List<Ticket> tickets = ticketRepository.findByBookingBookingId(booking.getBookingId());
            Flight flight = booking.getFlight();
            if (flight == null && !tickets.isEmpty()) {
                flight = tickets.get(0).getFlight();
            }
            int availableSeats = flight != null ? calculateAvailableSeats(flight) : 0;
            return EntityDtoMapper.toBookingResponseDto(booking, tickets, availableSeats);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + bookingId));

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        List<Ticket> tickets = ticketRepository.findByBookingBookingId(bookingId);
        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.CANCELLED);
            ticketRepository.save(ticket);
        }
    }

    private int calculateAvailableSeats(Flight flight) {
        if (flight == null || flight.getAircraft() == null) return 0;
        List<Seat> totalSeats = seatRepository.findByAircraftAircraftId(flight.getAircraft().getAircraftId());
        long bookedCount = totalSeats.stream()
            .filter(seat -> ticketRepository.existsByFlightFlightIdAndSeatSeatIdAndStatusNot(
                flight.getFlightId(), seat.getSeatId(), TicketStatus.CANCELLED))
            .count();
        return Math.max(0, totalSeats.size() - (int) bookedCount);
    }

    private String generatePnr() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(6);
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
