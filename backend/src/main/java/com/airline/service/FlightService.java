package com.airline.service;

import com.airline.dto.response.FlightSummaryDto;
import com.airline.dto.response.SeatDto;
import com.airline.entity.Flight;
import com.airline.entity.Seat;
import com.airline.entity.enums.FlightStatus;
import com.airline.entity.enums.SeatClass;
import com.airline.entity.enums.TicketStatus;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.EntityDtoMapper;
import com.airline.repository.FlightRepository;
import com.airline.repository.SeatRepository;
import com.airline.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FlightService {

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public List<FlightSummaryDto> searchFlights(String fromIata, String toIata, String dateStr) {
        LocalDate searchDate;
        try {
            searchDate = LocalDate.parse(dateStr);
        } catch (Exception e) {
            searchDate = LocalDate.now();
        }

        LocalDateTime startTime = searchDate.atStartOfDay();
        LocalDateTime endTime = searchDate.atTime(LocalTime.MAX);

        List<Flight> flights = flightRepository.searchFlights(fromIata, toIata, startTime, endTime, FlightStatus.CANCELLED);
        if (flights.isEmpty()) {
            // Fallback to fetch any flights matching route regardless of date for dev/demo flexibility
            flights = flightRepository.findAll().stream()
                .filter(f -> f.getDepartureAirport() != null && f.getDepartureAirport().getIataCode().equalsIgnoreCase(fromIata))
                .filter(f -> f.getArrivalAirport() != null && f.getArrivalAirport().getIataCode().equalsIgnoreCase(toIata))
                .collect(Collectors.toList());
        }

        List<FlightSummaryDto> result = new ArrayList<>();
        for (Flight flight : flights) {
            List<Seat> totalSeats = seatRepository.findByAircraftAircraftId(flight.getAircraft().getAircraftId());
            long bookedCount = totalSeats.stream()
                .filter(seat -> ticketRepository.existsByFlightFlightIdAndSeatSeatIdAndStatusNot(
                    flight.getFlightId(), seat.getSeatId(), TicketStatus.CANCELLED))
                .count();

            int availableSeats = Math.max(0, totalSeats.size() - (int) bookedCount);
            result.add(EntityDtoMapper.toFlightSummaryDto(flight, availableSeats));
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<SeatDto> getFlightSeats(Long flightId) {
        Flight flight = flightRepository.findById(flightId)
            .orElseThrow(() -> new ResourceNotFoundException("Flight not found with ID: " + flightId));

        List<Seat> seats = seatRepository.findByAircraftAircraftId(flight.getAircraft().getAircraftId());

        return seats.stream().map(seat -> {
            boolean isBooked = ticketRepository.existsByFlightFlightIdAndSeatSeatIdAndStatusNot(
                flight.getFlightId(), seat.getSeatId(), TicketStatus.CANCELLED);

            BigDecimal calculatedPrice = flight.getBasePrice();
            if (seat.getSeatClass() == SeatClass.BUSINESS) {
                calculatedPrice = calculatedPrice.multiply(new BigDecimal("2.50"));
            } else if (seat.getSeatClass() == SeatClass.FIRST) {
                calculatedPrice = calculatedPrice.multiply(new BigDecimal("4.00"));
            }

            SeatDto dto = EntityDtoMapper.toSeatDto(seat, !isBooked, flightId);
            dto.setPrice(calculatedPrice);
            dto.setCalculatedPrice(calculatedPrice);
            return dto;
        }).collect(Collectors.toList());
    }
}
