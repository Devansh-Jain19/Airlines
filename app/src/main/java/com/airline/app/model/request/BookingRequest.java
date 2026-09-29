package com.airline.app.model.request;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class BookingRequest {
    @SerializedName("passengerId")
    private Long passengerId;

    @SerializedName("flightId")
    private Long flightId;

    @SerializedName("seatIds")
    private List<Long> seatIds;

    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("totalFare")
    private Double totalFare;

    public BookingRequest() {}

    public BookingRequest(Long passengerId, Long flightId, List<Long> seatIds, String seatNumber, Double totalFare) {
        this.passengerId = passengerId;
        this.flightId = flightId;
        this.seatIds = seatIds;
        this.seatNumber = seatNumber;
        this.totalFare = totalFare;
    }

    public Long getPassengerId() {
        return passengerId;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public List<Long> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Long> seatIds) {
        this.seatIds = seatIds;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }
}
