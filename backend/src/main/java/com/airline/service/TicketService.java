package com.airline.service;

import com.airline.dto.response.TicketDetailDto;
import com.airline.entity.Ticket;
import com.airline.exception.ResourceNotFoundException;
import com.airline.mapper.EntityDtoMapper;
import com.airline.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional(readOnly = true)
    public TicketDetailDto getTicketDetails(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new ResourceNotFoundException("Ticket not found with ID: " + ticketId));

        return EntityDtoMapper.toTicketDto(ticket);
    }
}
