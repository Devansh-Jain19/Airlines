package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class SeatDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("seatClass")
    private String seatClass; // ECONOMY, BUSINESS, FIRST

    @SerializedName("price")
    private Double price;

    @SerializedName("available")
    private Boolean available;

    @SerializedName("flightId")
    private Long flightId;

    public SeatDto() {}

    public SeatDto(Long id, String seatNumber, String seatClass, Double price, Boolean available, Long flightId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.price = price;
        this.available = available;
        this.flightId = flightId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() {
        return seatClass;
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public Double getPrice() {
        return price != null ? price : 0.0;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean isAvailable() {
        return available != null && available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }
}
