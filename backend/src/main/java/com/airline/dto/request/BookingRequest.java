package com.airline.dto.request;

import java.util.List;

public class BookingRequest {
    private Long passengerId;
    private Long flightId;
    private List<Long> seatIds;
    private String seatNumber;
    private Double totalFare;
    private List<SegmentRequest> segments;

    public BookingRequest() {}

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public List<Long> getSeatIds() { return seatIds; }
    public void setSeatIds(List<Long> seatIds) { this.seatIds = seatIds; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public Double getTotalFare() { return totalFare; }
    public void setTotalFare(Double totalFare) { this.totalFare = totalFare; }

    public List<SegmentRequest> getSegments() { return segments; }
    public void setSegments(List<SegmentRequest> segments) { this.segments = segments; }
}
