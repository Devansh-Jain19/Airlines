package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FlightSummaryDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("departureAirportIata")
    private String departureAirportIata;

    @SerializedName("arrivalAirportIata")
    private String arrivalAirportIata;

    @SerializedName("departureAirportName")
    private String departureAirportName;

    @SerializedName("arrivalAirportName")
    private String arrivalAirportName;

    @SerializedName("departureCity")
    private String departureCity;

    @SerializedName("arrivalCity")
    private String arrivalCity;

    @SerializedName("departureTime")
    private String departureTime;

    @SerializedName("arrivalTime")
    private String arrivalTime;

    @SerializedName("duration")
    private String duration;

    @SerializedName("basePrice")
    private Double basePrice;

    @SerializedName("availableSeats")
    private Integer availableSeats;

    public FlightSummaryDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public void setAirlineName(String airlineName) {
        this.airlineName = airlineName;
    }

    public String getDepartureAirportIata() {
        return departureAirportIata;
    }

    public void setDepartureAirportIata(String departureAirportIata) {
        this.departureAirportIata = departureAirportIata;
    }

    public String getArrivalAirportIata() {
        return arrivalAirportIata;
    }

    public void setArrivalAirportIata(String arrivalAirportIata) {
        this.arrivalAirportIata = arrivalAirportIata;
    }

    public String getDepartureAirportName() {
        return departureAirportName;
    }

    public void setDepartureAirportName(String departureAirportName) {
        this.departureAirportName = departureAirportName;
    }

    public String getArrivalAirportName() {
        return arrivalAirportName;
    }

    public void setArrivalAirportName(String arrivalAirportName) {
        this.arrivalAirportName = arrivalAirportName;
    }

    public String getDepartureCity() {
        return departureCity != null ? departureCity : departureAirportIata;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getArrivalCity() {
        return arrivalCity != null ? arrivalCity : arrivalAirportIata;
    }

    public void setArrivalCity(String arrivalCity) {
        this.arrivalCity = arrivalCity;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getBasePrice() {
        return basePrice != null ? basePrice : 0.0;
    }

    public void setBasePrice(Double basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getAvailableSeats() {
        return availableSeats != null ? availableSeats : 0;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }
}
