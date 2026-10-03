package com.airline.dto.response;

import java.math.BigDecimal;

public class FlightSummaryDto {
    private Long id;
    private Long flightId;
    private String flightNumber;
    private String airlineName;
    private String departureAirportIata;
    private String departureAirportCode;
    private String arrivalAirportIata;
    private String arrivalAirportCode;
    private String departureAirportName;
    private String arrivalAirportName;
    private String departureCity;
    private String arrivalCity;
    private String departureTime;
    private String arrivalTime;
    private String duration;
    private Integer durationMinutes;
    private BigDecimal basePrice;
    private Integer availableSeats;
    private Integer seatsAvailable;

    public FlightSummaryDto() {}

    public Long getId() { return id != null ? id : flightId; }
    public void setId(Long id) { this.id = id; this.flightId = id; }

    public Long getFlightId() { return flightId != null ? flightId : id; }
    public void setFlightId(Long flightId) { this.flightId = flightId; this.id = flightId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getDepartureAirportIata() { return departureAirportIata != null ? departureAirportIata : departureAirportCode; }
    public void setDepartureAirportIata(String departureAirportIata) {
        this.departureAirportIata = departureAirportIata;
        this.departureAirportCode = departureAirportIata;
    }

    public String getDepartureAirportCode() { return departureAirportCode != null ? departureAirportCode : departureAirportIata; }
    public void setDepartureAirportCode(String departureAirportCode) {
        this.departureAirportCode = departureAirportCode;
        this.departureAirportIata = departureAirportCode;
    }

    public String getArrivalAirportIata() { return arrivalAirportIata != null ? arrivalAirportIata : arrivalAirportCode; }
    public void setArrivalAirportIata(String arrivalAirportIata) {
        this.arrivalAirportIata = arrivalAirportIata;
        this.arrivalAirportCode = arrivalAirportIata;
    }

    public String getArrivalAirportCode() { return arrivalAirportCode != null ? arrivalAirportCode : arrivalAirportIata; }
    public void setArrivalAirportCode(String arrivalAirportCode) {
        this.arrivalAirportCode = arrivalAirportCode;
        this.arrivalAirportIata = arrivalAirportCode;
    }

    public String getDepartureAirportName() { return departureAirportName; }
    public void setDepartureAirportName(String departureAirportName) { this.departureAirportName = departureAirportName; }

    public String getArrivalAirportName() { return arrivalAirportName; }
    public void setArrivalAirportName(String arrivalAirportName) { this.arrivalAirportName = arrivalAirportName; }

    public String getDepartureCity() { return departureCity; }
    public void setDepartureCity(String departureCity) { this.departureCity = departureCity; }

    public String getArrivalCity() { return arrivalCity; }
    public void setArrivalCity(String arrivalCity) { this.arrivalCity = arrivalCity; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getAvailableSeats() { return availableSeats != null ? availableSeats : seatsAvailable; }
    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
        this.seatsAvailable = availableSeats;
    }

    public Integer getSeatsAvailable() { return seatsAvailable != null ? seatsAvailable : availableSeats; }
    public void setSeatsAvailable(Integer seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
        this.availableSeats = seatsAvailable;
    }
}
