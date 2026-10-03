package com.airline.controller;

import com.airline.dto.response.BookingResponseDto;
import com.airline.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/passengers")
@CrossOrigin(origins = "*")
public class PassengerController {

    @Autowired
    private BookingService bookingService;

    @GetMapping("/{id}/bookings")
    public ResponseEntity<List<BookingResponseDto>> getPassengerBookings(@PathVariable("id") Long passengerId) {
        List<BookingResponseDto> bookings = bookingService.getPassengerBookings(passengerId);
        return ResponseEntity.ok(bookings);
    }
}
