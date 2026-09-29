package com.airline.app.model.response;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class AuthResponseDto implements Serializable {
    @SerializedName("token")
    private String token;

    @SerializedName("tokenType")
    private String tokenType;

    @SerializedName("passengerId")
    private Long passengerId;

    @SerializedName("email")
    private String email;

    @SerializedName("name")
    private String name;

    @SerializedName("passenger")
    private PassengerDto passenger;

    public AuthResponseDto() {}

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public Long getPassengerId() {
        if (passengerId != null) return passengerId;
        if (passenger != null) return passenger.getId();
        return null;
    }

    public void setPassengerId(Long passengerId) {
        this.passengerId = passengerId;
    }

    public String getEmail() {
        if (email != null) return email;
        if (passenger != null) return passenger.getEmail();
        return null;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        if (name != null) return name;
        if (passenger != null) return passenger.getFullName();
        return null;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PassengerDto getPassenger() {
        return passenger;
    }

    public void setPassenger(PassengerDto passenger) {
        this.passenger = passenger;
    }
}
