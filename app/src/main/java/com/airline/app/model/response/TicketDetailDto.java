package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class TicketDetailDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("ticketNumber")
    private String ticketNumber;

    @SerializedName("bookingReference")
    private String bookingReference;

    @SerializedName("passengerName")
    private String passengerName;

    @SerializedName("flightNumber")
    private String flightNumber;

    @SerializedName("airlineName")
    private String airlineName;

    @SerializedName("originAirport")
    private String originAirport;

    @SerializedName("originCity")
    private String originCity;

    @SerializedName("destinationAirport")
    private String destinationAirport;

    @SerializedName("destinationCity")
    private String destinationCity;

    @SerializedName("departureTime")
    private String departureTime;

    @SerializedName("arrivalTime")
    private String arrivalTime;

    @SerializedName("seatNumber")
    private String seatNumber;

    @SerializedName("seatClass")
    private String seatClass;

    @SerializedName("gate")
    private String gate;

    @SerializedName("terminal")
    private String terminal;

    @SerializedName("barcode")
    private String barcode;

    @SerializedName("status")
    private String status;

    public TicketDetailDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
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

    public String getOriginAirport() {
        return originAirport;
    }

    public void setOriginAirport(String originAirport) {
        this.originAirport = originAirport;
    }

    public String getOriginCity() {
        return originCity != null ? originCity : originAirport;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getDestinationAirport() {
        return destinationAirport;
    }

    public void setDestinationAirport(String destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public String getDestinationCity() {
        return destinationCity != null ? destinationCity : destinationAirport;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
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

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getSeatClass() {
        return seatClass != null ? seatClass : "ECONOMY";
    }

    public void setSeatClass(String seatClass) {
        this.seatClass = seatClass;
    }

    public String getGate() {
        return gate != null ? gate : "G12";
    }

    public void setGate(String gate) {
        this.gate = gate;
    }

    public String getTerminal() {
        return terminal != null ? terminal : "T2";
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public String getBarcode() {
        return barcode != null ? barcode : "AIR-" + (ticketNumber != null ? ticketNumber : "12345");
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getStatus() {
        return status != null ? status : "CONFIRMED";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
