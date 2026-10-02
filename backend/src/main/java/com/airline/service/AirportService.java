package com.airline.service;

import com.airline.dto.response.AirportDto;
import com.airline.mapper.EntityDtoMapper;
import com.airline.repository.AirportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AirportService {

    @Autowired
    private AirportRepository airportRepository;

    @Transactional(readOnly = true)
    public List<AirportDto> getAllAirports() {
        return airportRepository.findAll().stream()
            .map(EntityDtoMapper::toAirportDto)
            .collect(Collectors.toList());
    }
}
