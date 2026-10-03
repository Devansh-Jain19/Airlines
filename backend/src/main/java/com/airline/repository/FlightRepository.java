package com.airline.repository;

import com.airline.entity.Flight;
import com.airline.entity.enums.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
    @Query("SELECT f FROM Flight f WHERE UPPER(f.departureAirport.iataCode) = UPPER(:depIata) " +
           "AND UPPER(f.arrivalAirport.iataCode) = UPPER(:arrIata) " +
           "AND f.departureTime >= :startTime AND f.departureTime <= :endTime " +
           "AND f.status <> :cancelledStatus")
    List<Flight> searchFlights(@Param("depIata") String depIata,
                               @Param("arrIata") String arrIata,
                               @Param("startTime") LocalDateTime startTime,
                               @Param("endTime") LocalDateTime endTime,
                               @Param("cancelledStatus") FlightStatus cancelledStatus);
}
