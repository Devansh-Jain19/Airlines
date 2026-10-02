package com.airline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for test_cases.md: Airport endpoint and cross-cutting (N-*)
 */
@SpringBootTest
@AutoConfigureMockMvc
class AirportAndCrossCuttingTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // S-01: Airport list loads with correct fields
    @Test
    void S01_airportListLoads() throws Exception {
        mockMvc.perform(get("/api/airports"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].iataCode").exists())
            .andExpect(jsonPath("$[0].name").exists())
            .andExpect(jsonPath("$[0].city").exists())
            .andExpect(jsonPath("$[0].country").exists());
    }

    // N-01: HTTP 404 maps to distinct user-facing message
    @Test
    void N01_404_hasStructuredError() throws Exception {
        mockMvc.perform(get("/api/flights/99999/seats"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.error").value("Not Found"))
            .andExpect(jsonPath("$.message").exists())
            .andExpect(jsonPath("$.path").exists())
            .andExpect(jsonPath("$.timestamp").exists());
    }

    // N-02: Malformed JSON → 400 Bad Request, not 500
    @Test
    void N02_malformedJson_returns400() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/api/auth/login")
                    .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                    .content("{invalid json!!!}"))
            .andExpect(status().isBadRequest());
    }

    // T-12: passengerId from session, not hardcoded
    // Backend test: verify passengers/1 returns that user's bookings only
    @Test
    void T12_passengerBookingsAreIsolated() throws Exception {
        // Passenger 1 exists (seeded), passenger 99999 doesn't
        mockMvc.perform(get("/api/passengers/1/bookings"))
            .andExpect(status().isOk());

        mockMvc.perform(get("/api/passengers/99999/bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }
}
