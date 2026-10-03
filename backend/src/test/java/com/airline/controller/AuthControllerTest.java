package com.airline.controller;

import com.airline.dto.request.LoginRequest;
import com.airline.dto.request.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for test_cases.md Section 1: Auth (A-01 through A-11)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // A-05: Register with valid data → PassengerDto returned (201)
    @Test
    @Order(1)
    void A05_registerWithValidData_returns201() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Rohan");
        req.setLastName("Verma");
        req.setEmail("rohan@test.com");
        req.setPassword("StrongPass@1");
        req.setPhone("+919999999999");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("Rohan"))
            .andExpect(jsonPath("$.lastName").value("Verma"))
            .andExpect(jsonPath("$.email").value("rohan@test.com"));
    }

    // A-06: Register with duplicate email → error
    @Test
    @Order(2)
    void A06_registerDuplicateEmail_returnsError() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Rohan2");
        req.setLastName("Verma2");
        req.setEmail("rohan@test.com"); // same email
        req.setPassword("StrongPass@2");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    // A-01: Valid credentials → AuthResponseDto parsed with token
    @Test
    @Order(3)
    void A01_loginWithValidCredentials_returns200WithToken() throws Exception {
        // The DataInitializer seeds aarav@gmail.com / Password@123
        LoginRequest req = new LoginRequest();
        req.setEmail("aarav@gmail.com");
        req.setPassword("Password@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").exists())
            .andExpect(jsonPath("$.passengerId").exists())
            .andExpect(jsonPath("$.email").value("aarav@gmail.com"))
            .andExpect(jsonPath("$.name").exists());
    }

    // A-02: Wrong password → error
    @Test
    @Order(4)
    void A02_loginWithWrongPassword_returnsError() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("aarav@gmail.com");
        req.setPassword("WrongPass999");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    // A-03: Empty email → validation error, no 500
    @Test
    @Order(5)
    void A03_loginWithEmptyEmail_returnsBadRequest() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("");
        req.setPassword("Password@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    // A-04: Invalid email format
    @Test
    @Order(6)
    void A04_registerInvalidEmailFormat_returnsBadRequest() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("Bad");
        req.setLastName("Email");
        req.setEmail("abc@");
        req.setPassword("Pass@1234");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isBadRequest());
    }

    // A-08: Unicode name — no crash
    @Test
    @Order(7)
    void A08_registerWithUnicodeName_noCrash() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setFirstName("आरव");
        req.setLastName("शर्मा");
        req.setEmail("unicode@test.com");
        req.setPassword("Strong@123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.firstName").value("आरव"));
    }

    // A-11: Login with non-existent email → 404, no NPE
    @Test
    @Order(8)
    void A11_loginNonExistentEmail_returnsError() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("nobody@nowhere.com");
        req.setPassword("Any@123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isNotFound());
    }
}
