package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class BookingResponseDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("bookingReference")
    private String bookingReference;

    @SerializedName("bookingTime")
    private String bookingTime;

    @SerializedName("status")
    private String status; // PENDING, CONFIRMED, CANCELLED

    @SerializedName("passengerId")
    private Long passengerId;

    @SerializedName("passengerName")
    private String passengerName;

    @SerializedName("flightSummary")
    private FlightSummaryDto flightSummary;

    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("seatNumbers")
    private List<String> seatNumbers;

    @SerializedName("totalPrice")
    private Double totalPrice;

    @SerializedName("ticketId")
    private Long ticketId;

    public BookingResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status != null ? status : "PENDING";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public FlightSummaryDto getFlightSummary() {
        return flightSummary;
    }

    public void setFlightSummary(FlightSummaryDto flightSummary) {
        this.flightSummary = flightSummary;
    }

    public String getSeatNumber() {
        if (seatNumber != null) return seatNumber;
        if (seatNumbers != null && !seatNumbers.isEmpty()) {
            return String.join(", ", seatNumbers);
        }
        return "";
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public Double getTotalPrice() {
        return totalPrice != null ? totalPrice : 0.0;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Long getTicketId() {
        return ticketId;
    }

    public void setTicketId(Long ticketId) {
        this.ticketId = ticketId;
    }
}
