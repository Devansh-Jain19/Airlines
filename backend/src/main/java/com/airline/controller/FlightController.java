package com.airline.controller;

import com.airline.dto.response.FlightSummaryDto;
import com.airline.dto.response.SeatDto;
import com.airline.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flights")
@CrossOrigin(origins = "*")
public class FlightController {

    @Autowired
    private FlightService flightService;

    @GetMapping("/search")
    public ResponseEntity<List<FlightSummaryDto>> searchFlights(
            @RequestParam("from") String departureIata,
            @RequestParam("to") String arrivalIata,
            @RequestParam("date") String flightDate) {

        List<FlightSummaryDto> flights = flightService.searchFlights(departureIata, arrivalIata, flightDate);
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<List<SeatDto>> getFlightSeats(@PathVariable("id") Long flightId) {
        List<SeatDto> seats = flightService.getFlightSeats(flightId);
        return ResponseEntity.ok(seats);
    }
}
