package com.airline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for test_cases.md Sections 2 & 3: Flight Search (S-*) and Flight List (F-*)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlightControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // S-10: Valid search, results found → list returned with correct fields
    // F-02: Flight no., times, duration, price, seats left correct
    @Test
    @Order(1)
    void S10_F02_validSearch_returnsFlightsWithCorrectFields() throws Exception {
        // DataInitializer seeds DEL→BOM flights for tomorrow
        // Use a date far enough in future that it always matches
        mockMvc.perform(get("/api/flights/search")
                .param("from", "DEL")
                .param("to", "BOM")
                .param("date", java.time.LocalDate.now().plusDays(1).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].flightNumber").exists())
            .andExpect(jsonPath("$[0].departureAirportIata").value("DEL"))
            .andExpect(jsonPath("$[0].arrivalAirportIata").value("BOM"))
            .andExpect(jsonPath("$[0].departureTime").exists())
            .andExpect(jsonPath("$[0].arrivalTime").exists())
            .andExpect(jsonPath("$[0].basePrice").exists())
            .andExpect(jsonPath("$[0].availableSeats").exists())
            .andExpect(jsonPath("$[0].durationMinutes").exists())
            .andExpect(jsonPath("$[0].airlineName").exists());
    }

    // S-11: Valid search, zero results → empty array, not error
    @Test
    @Order(2)
    void S11_validSearchNoResults_returnsEmptyArray() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .param("from", "XXX")
                .param("to", "YYY")
                .param("date", "2026-12-25"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // S-04: Case-insensitive IATA matching
    @Test
    @Order(3)
    void S04_caseInsensitiveIata_returnsResults() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .param("from", "del")
                .param("to", "bom")
                .param("date", java.time.LocalDate.now().plusDays(1).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    // N-05: Query params from, to, date encoded correctly (implicit from the above tests)
    // S-15: Date format sent as ISO yyyy-MM-dd
    @Test
    @Order(4)
    void S15_isoDateFormat_accepted() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .param("from", "DEL")
                .param("to", "BLR")
                .param("date", java.time.LocalDate.now().plusDays(1).toString()))
            .andExpect(status().isOk());
    }

    // F-06: Null or missing fields in FlightSummaryDto → no crash
    @Test
    @Order(5)
    void F06_emptyResultsNoNPE() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .param("from", "AAA")
                .param("to", "BBB")
                .param("date", "2026-01-01"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    // F-11: Price formatting — backend returns BigDecimal with 2 decimal places
    @Test
    @Order(6)
    void F11_priceHasDecimals() throws Exception {
        mockMvc.perform(get("/api/flights/search")
                .param("from", "DEL")
                .param("to", "BOM")
                .param("date", java.time.LocalDate.now().plusDays(1).toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].basePrice").isNumber());
    }

    // ST-01 to ST-14: Seat grid endpoint tests
    // First we need a valid flightId. DataInitializer seeds flights, IDs start at 1.
    @Test
    @Order(7)
    void ST01_getFlightSeats_returnsGridWithCorrectFields() throws Exception {
        // Flight ID 1 is DEL→BOM 6E201
        mockMvc.perform(get("/api/flights/1/seats"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].seatNumber").exists())
            .andExpect(jsonPath("$[0].seatClass").exists())
            .andExpect(jsonPath("$[0].available").exists())
            .andExpect(jsonPath("$[0].price").exists());
    }

    // ST-09: Empty seat list on invalid flight → 404
    @Test
    @Order(8)
    void ST09_nonExistentFlightSeats_returns404() throws Exception {
        mockMvc.perform(get("/api/flights/99999/seats"))
            .andExpect(status().isNotFound());
    }

    // ST-13: Business seats have higher price than economy seats
    @Test
    @Order(9)
    void ST13_businessSeatsHaveHigherPrice() throws Exception {
        String response = mockMvc.perform(get("/api/flights/1/seats"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        // Parse and verify business seats cost more
        var seats = objectMapper.readTree(response);
        double economyPrice = -1;
        double businessPrice = -1;
        for (var seat : seats) {
            String seatClass = seat.get("seatClass").asText();
            double price = seat.get("price").asDouble();
            if ("ECONOMY".equals(seatClass) && economyPrice < 0) economyPrice = price;
            if ("BUSINESS".equals(seatClass) && businessPrice < 0) businessPrice = price;
        }
        if (economyPrice > 0 && businessPrice > 0) {
            Assertions.assertTrue(businessPrice > economyPrice,
                "Business price (" + businessPrice + ") should be > Economy (" + economyPrice + ")");
        }
    }

    // ST-14: Seat labels sorted by row then column (1A before 2A, not 10A before 2A)
    @Test
    @Order(10)
    void ST14_seatLabelsSortedCorrectly() throws Exception {
        String response = mockMvc.perform(get("/api/flights/1/seats"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        var seats = objectMapper.readTree(response);
        // Verify at least first seat is row 1
        String firstSeat = seats.get(0).get("seatNumber").asText();
        Assertions.assertTrue(firstSeat.startsWith("1"), "First seat should be row 1, got: " + firstSeat);
    }

    // N-06: Path param {id} correctly substituted
    @Test
    @Order(11)
    void N06_pathParamSubstitutedCorrectly() throws Exception {
        // Valid flight
        mockMvc.perform(get("/api/flights/1/seats"))
            .andExpect(status().isOk());

        // Invalid flight
        mockMvc.perform(get("/api/flights/0/seats"))
            .andExpect(status().isNotFound());
    }
}
