package com.airline.dto.request;

public class SegmentRequest {
    private Long flightId;
    private Long seatId;

    public SegmentRequest() {}

    public SegmentRequest(Long flightId, Long seatId) {
        this.flightId = flightId;
        this.seatId = seatId;
    }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }
}
