package com.airline.dto.response;

public class AuthResponseDto {
    private String token;
    private String tokenType = "Bearer";
    private Long passengerId;
    private String email;
    private String name;
    private PassengerDto passenger;

    public AuthResponseDto() {}

    public AuthResponseDto(String token, Long passengerId, String email, String name, PassengerDto passenger) {
        this.token = token;
        this.tokenType = "Bearer";
        this.passengerId = passengerId;
        this.email = email;
        this.name = name;
        this.passenger = passenger;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getPassengerId() { return passengerId; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public PassengerDto getPassenger() { return passenger; }
    public void setPassenger(PassengerDto passenger) { this.passenger = passenger; }
}
