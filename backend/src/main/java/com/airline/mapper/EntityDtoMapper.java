package com.airline.mapper;

import com.airline.dto.response.*;
import com.airline.entity.*;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoMapper {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static PassengerDto toPassengerDto(Passenger passenger) {
        if (passenger == null) return null;
        return new PassengerDto(
            passenger.getPassengerId(),
            passenger.getFirstName(),
            passenger.getLastName(),
            passenger.getEmail(),
            passenger.getPhone(),
            passenger.getPassportNumber()
        );
    }

    public static AirportDto toAirportDto(Airport airport) {
        if (airport == null) return null;
        return new AirportDto(
            airport.getAirportId(),
            airport.getIataCode(),
            airport.getName(),
            airport.getCity(),
            airport.getCountry()
        );
    }

    public static FlightSummaryDto toFlightSummaryDto(Flight flight, int availableSeats) {
        if (flight == null) return null;
        FlightSummaryDto dto = new FlightSummaryDto();
        dto.setId(flight.getFlightId());
        dto.setFlightNumber(flight.getFlightNumber());
        if (flight.getAircraft() != null && flight.getAircraft().getAirline() != null) {
            dto.setAirlineName(flight.getAircraft().getAirline().getName());
        } else {
            dto.setAirlineName("IndiGo");
        }
        if (flight.getDepartureAirport() != null) {
            dto.setDepartureAirportIata(flight.getDepartureAirport().getIataCode());
            dto.setDepartureAirportName(flight.getDepartureAirport().getName());
            dto.setDepartureCity(flight.getDepartureAirport().getCity());
        }
        if (flight.getArrivalAirport() != null) {
            dto.setArrivalAirportIata(flight.getArrivalAirport().getIataCode());
            dto.setArrivalAirportName(flight.getArrivalAirport().getName());
            dto.setArrivalCity(flight.getArrivalAirport().getCity());
        }
        if (flight.getDepartureTime() != null) {
            dto.setDepartureTime(flight.getDepartureTime().format(ISO_FORMATTER));
        }
        if (flight.getArrivalTime() != null) {
            dto.setArrivalTime(flight.getArrivalTime().format(ISO_FORMATTER));
        }
        if (flight.getDepartureTime() != null && flight.getArrivalTime() != null) {
            long minutes = Duration.between(flight.getDepartureTime(), flight.getArrivalTime()).toMinutes();
            dto.setDurationMinutes((int) minutes);
            long hours = minutes / 60;
            long mins = minutes % 60;
            dto.setDuration(hours + "h " + mins + "m");
        }
        dto.setBasePrice(flight.getBasePrice());
        dto.setAvailableSeats(availableSeats);
        return dto;
    }

    public static SeatDto toSeatDto(Seat seat, boolean isAvailable, Long flightId) {
        if (seat == null) return null;
        SeatDto dto = new SeatDto();
        dto.setId(seat.getSeatId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setSeatClass(seat.getSeatClass() != null ? seat.getSeatClass().name() : "ECONOMY");
        dto.setAvailable(isAvailable);
        dto.setFlightId(flightId);
        return dto;
    }

    public static TicketDetailDto toTicketDto(Ticket ticket) {
        if (ticket == null) return null;
        TicketDetailDto dto = new TicketDetailDto();
        dto.setId(ticket.getTicketId());
        dto.setTicketNumber(ticket.getTicketNumber());
        if (ticket.getBooking() != null) {
            dto.setBookingReference(ticket.getBooking().getBookingReference());
            if (ticket.getBooking().getPassenger() != null) {
                Passenger p = ticket.getBooking().getPassenger();
                dto.setPassengerName(p.getFirstName() + " " + p.getLastName());
            }
        }
        if (ticket.getFlight() != null) {
            Flight f = ticket.getFlight();
            dto.setFlightNumber(f.getFlightNumber());
            if (f.getAircraft() != null && f.getAircraft().getAirline() != null) {
                dto.setAirlineName(f.getAircraft().getAirline().getName());
            }
            if (f.getDepartureAirport() != null) {
                dto.setOriginAirport(f.getDepartureAirport().getIataCode());
                dto.setOriginCity(f.getDepartureAirport().getCity());
            }
            if (f.getArrivalAirport() != null) {
                dto.setDestinationAirport(f.getArrivalAirport().getIataCode());
                dto.setDestinationCity(f.getArrivalAirport().getCity());
            }
            if (f.getDepartureTime() != null) {
                dto.setDepartureTime(f.getDepartureTime().format(ISO_FORMATTER));
            }
            if (f.getArrivalTime() != null) {
                dto.setArrivalTime(f.getArrivalTime().format(ISO_FORMATTER));
            }
        }
        if (ticket.getSeat() != null) {
            dto.setSeatNumber(ticket.getSeat().getSeatNumber());
            dto.setSeatClass(ticket.getSeat().getSeatClass() != null ? ticket.getSeat().getSeatClass().name() : "ECONOMY");
        }
        dto.setBarcode("AIR-" + (ticket.getTicketNumber() != null ? ticket.getTicketNumber() : "12345678"));
        dto.setFare(ticket.getFare());
        dto.setStatus(ticket.getStatus() != null ? ticket.getStatus().name() : "PENDING");
        return dto;
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking, List<Ticket> tickets, int availableSeats) {
        if (booking == null) return null;
        BookingResponseDto dto = new BookingResponseDto();
        dto.setId(booking.getBookingId());
        dto.setBookingReference(booking.getBookingReference());
        if (booking.getBookingDate() != null) {
            dto.setBookingTime(booking.getBookingDate().format(ISO_FORMATTER));
        }
        dto.setStatus(booking.getStatus() != null ? booking.getStatus().name() : "PENDING");
        if (booking.getPassenger() != null) {
            dto.setPassengerId(booking.getPassenger().getPassengerId());
            dto.setPassengerName(booking.getPassenger().getFirstName() + " " + booking.getPassenger().getLastName());
        }

        if (booking.getFlight() != null) {
            dto.setFlightSummary(toFlightSummaryDto(booking.getFlight(), availableSeats));
        } else if (tickets != null && !tickets.isEmpty() && tickets.get(0).getFlight() != null) {
            dto.setFlightSummary(toFlightSummaryDto(tickets.get(0).getFlight(), availableSeats));
        }

        if (tickets != null && !tickets.isEmpty()) {
            List<String> seatNums = tickets.stream()
                .filter(t -> t.getSeat() != null)
                .map(t -> t.getSeat().getSeatNumber())
                .collect(Collectors.toList());
            dto.setSeatNumbers(seatNums);
            dto.setSeatNumber(String.join(", ", seatNums));
            dto.setTicketId(tickets.get(0).getTicketId());
            dto.setTickets(tickets.stream().map(EntityDtoMapper::toTicketDto).collect(Collectors.toList()));
        }

        dto.setTotalPrice(booking.getTotalAmount());
        return dto;
    }

    public static PaymentResponseDto toPaymentDto(Payment payment) {
        if (payment == null) return null;
        PaymentResponseDto dto = new PaymentResponseDto();
        dto.setId(payment.getPaymentId());
        if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getBookingId());
            dto.setBookingStatus(payment.getBooking().getStatus() != null ? payment.getBooking().getStatus().name() : "CONFIRMED");
        }
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setAmount(payment.getAmount());
        dto.setStatus(payment.getStatus() != null ? payment.getStatus().name() : "SUCCESS");
        dto.setTransactionId(payment.getTransactionRef());
        if (payment.getPaymentTime() != null) {
            dto.setPaymentTime(payment.getPaymentTime().format(ISO_FORMATTER));
        }
        return dto;
    }
}
