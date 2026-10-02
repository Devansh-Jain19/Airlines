package com.airline.dto.response;

import java.math.BigDecimal;

public class SeatDto {
    private Long id;
    private Long seatId;
    private String seatNumber;
    private String seatClass;
    private BigDecimal price;
    private BigDecimal calculatedPrice;
    private Boolean available;
    private Long flightId;

    public SeatDto() {}

    public Long getId() { return id != null ? id : seatId; }
    public void setId(Long id) { this.id = id; this.seatId = id; }

    public Long getSeatId() { return seatId != null ? seatId : id; }
    public void setSeatId(Long seatId) { this.seatId = seatId; this.id = seatId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public String getSeatClass() { return seatClass; }
    public void setSeatClass(String seatClass) { this.seatClass = seatClass; }

    public BigDecimal getPrice() { return price != null ? price : calculatedPrice; }
    public void setPrice(BigDecimal price) {
        this.price = price;
        this.calculatedPrice = price;
    }

    public BigDecimal getCalculatedPrice() { return calculatedPrice != null ? calculatedPrice : price; }
    public void setCalculatedPrice(BigDecimal calculatedPrice) {
        this.calculatedPrice = calculatedPrice;
        this.price = calculatedPrice;
    }

    public Boolean getAvailable() { return available; }
    public Boolean isAvailable() { return available; }
    public void setAvailable(Boolean available) { this.available = available; }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }
}
