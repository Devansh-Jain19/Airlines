package com.airline.dto.response;

public class PassengerDto {
    private Long id;
    private Long passengerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passportNumber;

    public PassengerDto() {}

    public PassengerDto(Long id, String firstName, String lastName, String email, String phone, String passportNumber) {
        this.id = id;
        this.passengerId = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.passportNumber = passportNumber;
    }

    public Long getId() { return id != null ? id : passengerId; }
    public void setId(Long id) { this.id = id; this.passengerId = id; }

    public Long getPassengerId() { return passengerId != null ? passengerId : id; }
    public void setPassengerId(Long passengerId) { this.passengerId = passengerId; this.id = passengerId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }
}
