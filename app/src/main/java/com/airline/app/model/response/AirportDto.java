package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AirportDto implements Serializable {
    @SerializedName("id")
    private Long id;

    @SerializedName("iataCode")
    private String iataCode;

    @SerializedName("name")
    private String name;

    @SerializedName("city")
    private String city;

    @SerializedName("country")
    private String country;

    public AirportDto() {}

    public AirportDto(Long id, String iataCode, String name, String city, String country) {
        this.id = id;
        this.iataCode = iataCode;
        this.name = name;
        this.city = city;
        this.country = country;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIataCode() {
        return iataCode;
    }

    public void setIataCode(String iataCode) {
        this.iataCode = iataCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return (city != null ? city : "") + " (" + iataCode + ") - " + (name != null ? name : "");
    }
}
