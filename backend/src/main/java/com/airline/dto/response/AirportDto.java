package com.airline.dto.response;

public class AirportDto {
    private Long id;
    private Long airportId;
    private String iataCode;
    private String name;
    private String city;
    private String country;

    public AirportDto() {}

    public AirportDto(Long id, String iataCode, String name, String city, String country) {
        this.id = id;
        this.airportId = id;
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public Long getId() { return id != null ? id : airportId; }
    public void setId(Long id) { this.id = id; this.airportId = id; }

    public Long getAirportId() { return airportId != null ? airportId : id; }
    public void setAirportId(Long airportId) { this.airportId = airportId; this.id = airportId; }

    public String getIataCode() { return iataCode; }
    public void setIataCode(String iataCode) { this.iataCode = iataCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
