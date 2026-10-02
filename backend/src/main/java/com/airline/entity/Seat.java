package com.airline.entity;

import com.airline.entity.enums.SeatClass;
import jakarta.persistence.*;

@Entity
@Table(name = "seat", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"aircraft_id", "seat_number"})
})
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @Column(name = "seat_number", nullable = false, length = 5)
    private String seatNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_class", nullable = false)
    private SeatClass seatClass = SeatClass.ECONOMY;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aircraft_id", nullable = false)
    private Aircraft aircraft;

    public Seat() {}

    public Seat(String seatNumber, SeatClass seatClass, Aircraft aircraft) {
        this.seatNumber = seatNumber;
        this.seatClass = seatClass;
        this.aircraft = aircraft;
    }

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }

    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }

    public SeatClass getSeatClass() { return seatClass; }
    public void setSeatClass(SeatClass seatClass) { this.seatClass = seatClass; }

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }
}
