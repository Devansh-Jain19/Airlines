package com.airline.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class BookingResponseDto {
    private Long id;
    private Long bookingId;
    private String bookingReference;
    private String bookingTime;
    private String bookingDate;
    private String status;
    private Long passengerId;
    private String passengerName;
    private FlightSummaryDto flightSummary;
    private String seatNumber;
    private List<String> seatNumbers;
    private BigDecimal totalPrice;
    private BigDecimal totalAmount;
    private Long ticketId;
    private List<TicketDetailDto> tickets;

    public BookingResponseDto() {}

    public Long getId() { return id != null ? id : bookingId; }
    public void setId(Long id) { this.id = id; this.bookingId = id; }

    public Long getBookingId() { return bookingId != null ? bookingId : id; }
    public void setBookingId(Long bookingId) { this.bookingId = bookingId; this.id = bookingId; }

    public String getBookingReference() { return bookingReference; }
    public void setBookingReference(String bookingReference) { this.bookingReference = bookingReference; }

    public String getBookingTime() { return bookingTime != null ? bookingTime : bookingDate; }
    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
        this.bookingDate = bookingTime;
    }

    public String getBookingDate() { return bookingDate != null ? bookingDate : bookingTime; }
    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
        this.bookingTime = bookingDate;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }

    public FlightSummaryDto getFlightSummary() { return flightSummary; }
    public void setFlightSummary(FlightSummaryDto flightSummary) { this.flightSummary = flightSummary; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public List<String> getSeatNumbers() { return seatNumbers; }
    public void setSeatNumbers(List<String> seatNumbers) { this.seatNumbers = seatNumbers; }

    public BigDecimal getTotalPrice() { return totalPrice != null ? totalPrice : totalAmount; }
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
        this.totalAmount = totalPrice;
    }

    public BigDecimal getTotalAmount() { return totalAmount != null ? totalAmount : totalPrice; }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
        this.totalPrice = totalAmount;
    }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public List<TicketDetailDto> getTickets() { return tickets; }
    public void setTickets(List<TicketDetailDto> tickets) { this.tickets = tickets; }
}
