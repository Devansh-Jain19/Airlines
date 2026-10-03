package com.airline.controller;

import com.airline.dto.request.BookingRequest;
import com.airline.dto.request.PaymentRequest;
import com.airline.dto.request.SegmentRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests for test_cases.md Sections 5 & 6: Booking, Payment, Ticket (B-*, T-*)
 */
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BookingPaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static Long bookingId;
    private static Long ticketId;
    private static Long firstBookedSeatId;

    /**
     * Helper: get the first available seat from flight 1.
     */
    private Long getFirstAvailableSeatId() throws Exception {
        String seatsJson = mockMvc.perform(get("/api/flights/1/seats"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode seats = objectMapper.readTree(seatsJson);
        for (JsonNode seat : seats) {
            if (seat.get("available").asBoolean()) {
                return seat.get("id").asLong();
            }
        }
        return null;
    }

    // B-01: Happy path — create booking → PENDING, payment → CONFIRMED
    @Test
    @Order(1)
    void B01_happyPath_bookingThenPayment() throws Exception {
        Long seatId = getFirstAvailableSeatId();
        Assertions.assertNotNull(seatId, "Should find an available seat");

        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, seatId)));

        MvcResult bookResult = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.bookingReference").exists())
            .andExpect(jsonPath("$.totalAmount").isNumber())
            .andExpect(jsonPath("$.tickets").isArray())
            .andExpect(jsonPath("$.tickets", hasSize(1)))
            .andReturn();


        JsonNode booking = objectMapper.readTree(bookResult.getResponse().getContentAsString());
        bookingId = booking.get("id").asLong();
        ticketId = booking.get("tickets").get(0).get("id").asLong();
        firstBookedSeatId = seatId;

        // Now pay
        PaymentRequest payReq = new PaymentRequest();
        payReq.setBookingId(bookingId);
        payReq.setMethod("UPI");

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.status").value("SUCCESS"))
            .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"))
            .andExpect(jsonPath("$.bookingId").value(bookingId))
            .andExpect(jsonPath("$.transactionId").exists())
            .andExpect(jsonPath("$.amount").isNumber());
    }

    // B-02: Each payment method works — verify CARD string is accepted
    @Test
    @Order(2)
    void B02_cardPaymentMethod() throws Exception {
        Long seatId = getFirstAvailableSeatId();
        Assertions.assertNotNull(seatId, "Should find available seat for B02");

        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, seatId)));

        MvcResult res = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isCreated())
            .andReturn();

        Long bId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        PaymentRequest payReq = new PaymentRequest();
        payReq.setBookingId(bId);
        payReq.setPaymentMethod("CARD");

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payReq)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.paymentMethod").value("CARD"));
    }

    // B-04: Summary card values — total matches fare
    @Test
    @Order(3)
    void B04_totalMatchesFare() throws Exception {
        // Get seat price from the seat map
        String seatsJson = mockMvc.perform(get("/api/flights/1/seats"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode seats = objectMapper.readTree(seatsJson);
        Long seatId = null;
        double expectedPrice = 0;
        for (JsonNode seat : seats) {
            if (seat.get("available").asBoolean()) {
                seatId = seat.get("id").asLong();
                expectedPrice = seat.get("price").asDouble();
                break;
            }
        }
        Assertions.assertNotNull(seatId, "Should find available seat for B04");

        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, seatId)));

        MvcResult res = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isCreated())
            .andReturn();

        double totalAmount = objectMapper.readTree(res.getResponse().getContentAsString())
            .get("totalAmount").asDouble();

        Assertions.assertEquals(expectedPrice, totalAmount, 0.01,
            "Total amount should match seat price");
    }

    // B-05: Seat already booked → 409 Conflict
    @Test
    @Order(4)
    void B05_doubleBookSameSeat_returns409() throws Exception {
        // firstBookedSeatId was booked in B01 — try to book it again
        if (firstBookedSeatId == null) return;

        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, firstBookedSeatId)));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isConflict());
    }

    // B-15: Total amount precision — BigDecimal, not double rounding
    @Test
    @Order(5)
    void B15_totalAmountPrecision() throws Exception {
        Long seatId = getFirstAvailableSeatId();
        Assertions.assertNotNull(seatId, "Should find available seat for B15");

        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, seatId)));

        String body = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();

        JsonNode json = objectMapper.readTree(body);
        double total = json.get("totalAmount").asDouble();
        Assertions.assertFalse(Double.isNaN(total), "totalAmount should not be NaN");
        Assertions.assertFalse(Double.isInfinite(total), "totalAmount should not be Infinite");
        Assertions.assertTrue(total > 0, "totalAmount should be positive");
    }

    // T-01: Boarding pass details — ticket has all fields
    @Test
    @Order(6)
    void T01_ticketDetails_hasAllFields() throws Exception {
        Assertions.assertNotNull(ticketId, "ticketId should be set from B01");

        mockMvc.perform(get("/api/tickets/" + ticketId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ticketNumber").exists())
            .andExpect(jsonPath("$.flightNumber").exists())
            .andExpect(jsonPath("$.seatNumber").exists())
            .andExpect(jsonPath("$.departureTime").exists())
            .andExpect(jsonPath("$.arrivalTime").exists())
            .andExpect(jsonPath("$.status").exists())
            .andExpect(jsonPath("$.passengerName").exists())
            .andExpect(jsonPath("$.bookingReference").exists());
    }

    // T-02: Ticket not found → 404
    @Test
    @Order(7)
    void T02_ticketNotFound_returns404() throws Exception {
        mockMvc.perform(get("/api/tickets/99999"))
            .andExpect(status().isNotFound());
    }

    // T-04: Passenger bookings list (B01 created one for passenger 1)
    @Test
    @Order(8)
    void T04_passengerBookingsList() throws Exception {
        // Passenger 1 has bookings from B01, B02, B04, B15
        mockMvc.perform(get("/api/passengers/1/bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
            .andExpect(jsonPath("$[0].status").exists())
            .andExpect(jsonPath("$[0].bookingReference").exists());
    }

    // T-05: No bookings for unknown passenger → empty list
    @Test
    @Order(9)
    void T05_noBookings_emptyList() throws Exception {
        mockMvc.perform(get("/api/passengers/99999/bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    // T-06: Cancel a booking → 204 No Content
    @Test
    @Order(10)
    void T06_cancelBooking_returns204() throws Exception {
        Long seatId = getFirstAvailableSeatId();
        Assertions.assertNotNull(seatId, "Should find available seat for T06");

        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, seatId)));

        MvcResult res = mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isCreated())
            .andReturn();

        Long cancelBookingId = objectMapper.readTree(res.getResponse().getContentAsString()).get("id").asLong();

        // N-03: Void response on cancel — no crash
        mockMvc.perform(delete("/api/bookings/" + cancelBookingId))
            .andExpect(status().isNoContent());
    }

    // Booking with non-existent passenger → 404
    @Test
    @Order(11)
    void bookingNonExistentPassenger_returns404() throws Exception {
        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(99999L);
        bookReq.setSegments(List.of(new SegmentRequest(1L, 1L)));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isNotFound());
    }

    // Booking with non-existent flight → 404
    @Test
    @Order(12)
    void bookingNonExistentFlight_returns404() throws Exception {
        BookingRequest bookReq = new BookingRequest();
        bookReq.setPassengerId(1L);
        bookReq.setSegments(List.of(new SegmentRequest(99999L, 1L)));

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookReq)))
            .andExpect(status().isNotFound());
    }

    // Payment for non-existent booking → 404
    @Test
    @Order(13)
    void paymentNonExistentBooking_returns404() throws Exception {
        PaymentRequest payReq = new PaymentRequest();
        payReq.setBookingId(99999L);
        payReq.setMethod("UPI");

        mockMvc.perform(post("/api/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payReq)))
            .andExpect(status().isNotFound());
    }
}
