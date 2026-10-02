package com.airline.service;

import com.airline.dto.request.LoginRequest;
import com.airline.dto.request.RegisterRequest;
import com.airline.dto.response.AuthResponseDto;
import com.airline.dto.response.PassengerDto;
import com.airline.entity.Passenger;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.EntityDtoMapper;
import com.airline.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public PassengerDto register(RegisterRequest request) {
        if (passengerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email address is already registered.");
        }

        Passenger passenger = new Passenger();
        passenger.setFirstName(request.getFirstName());
        passenger.setLastName(request.getLastName());
        passenger.setEmail(request.getEmail());
        passenger.setPassword(passwordEncoder.encode(request.getPassword()));
        passenger.setPhone(request.getPhone());
        passenger.setPassportNumber(request.getPassportNumber());
        passenger.setDateOfBirth(request.getDateOfBirth());
        passenger.setGender(request.getGender());

        passenger = passengerRepository.save(passenger);
        return EntityDtoMapper.toPassengerDto(passenger);
    }

    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequest request) {
        Passenger passenger = passengerRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password."));

        if (!passwordEncoder.matches(request.getPassword(), passenger.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = "jwt_token_" + UUID.randomUUID().toString().replace("-", "");
        PassengerDto dto = EntityDtoMapper.toPassengerDto(passenger);

        return new AuthResponseDto(
            token,
            passenger.getPassengerId(),
            passenger.getEmail(),
            passenger.getFirstName() + " " + passenger.getLastName(),
            dto
        );
    }
}
