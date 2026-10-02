package com.airline.controller;

import com.airline.dto.response.AirportDto;
import com.airline.service.AirportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/airports")
@CrossOrigin(origins = "*")
public class AirportController {

    @Autowired
    private AirportService airportService;

    @GetMapping
    public ResponseEntity<List<AirportDto>> getAirports() {
        List<AirportDto> airports = airportService.getAllAirports();
        return ResponseEntity.ok(airports);
    }
}
