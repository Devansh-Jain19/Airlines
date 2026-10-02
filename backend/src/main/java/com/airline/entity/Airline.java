package com.airline.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "airline")
public class Airline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "airline_id")
    private Long airlineId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", nullable = false, length = 3, unique = true)
    private String code;

    @Column(name = "country", length = 50)
    private String country;

    public Airline() {}

    public Airline(String name, String code, String country) {
        this.name = name;
        this.code = code;
        this.country = country;
    }

    public Long getAirlineId() { return airlineId; }
    public void setAirlineId(Long airlineId) { this.airlineId = airlineId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
