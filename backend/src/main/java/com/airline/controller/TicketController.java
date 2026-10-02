package com.airline.controller;

import com.airline.dto.response.TicketDetailDto;
import com.airline.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDto> getTicketDetails(@PathVariable("id") Long ticketId) {
        TicketDetailDto ticket = ticketService.getTicketDetails(ticketId);
        return ResponseEntity.ok(ticket);
    }
}
