package com.airline.controller;

import com.airline.dto.request.LoginRequest;
import com.airline.dto.request.RegisterRequest;
import com.airline.dto.response.AuthResponseDto;
import com.airline.dto.response.PassengerDto;
import com.airline.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<PassengerDto> register(@Valid @RequestBody RegisterRequest request) {
        PassengerDto passenger = authService.register(request);
        return new ResponseEntity<>(passenger, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginRequest request) {
        AuthResponseDto response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
