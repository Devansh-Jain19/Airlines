package com.airline.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "aircraft")
public class Aircraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aircraft_id")
    private Long aircraftId;

    @Column(name = "registration_number", nullable = false, length = 20, unique = true)
    private String registrationNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private AircraftModel aircraftModel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "airline_id")
    private Airline airline;

    public Aircraft() {}

    public Aircraft(String registrationNumber, AircraftModel aircraftModel, Airline airline) {
        this.registrationNumber = registrationNumber;
        this.aircraftModel = aircraftModel;
        this.airline = airline;
    }

    public Long getAircraftId() { return aircraftId; }
    public void setAircraftId(Long aircraftId) { this.aircraftId = aircraftId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public AircraftModel getAircraftModel() { return aircraftModel; }
    public void setAircraftModel(AircraftModel aircraftModel) { this.aircraftModel = aircraftModel; }

    public Airline getAirline() { return airline; }
    public void setAirline(Airline airline) { this.airline = airline; }
}
