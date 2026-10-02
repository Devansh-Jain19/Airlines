package com.airline.config;

import com.airline.entity.*;
import com.airline.entity.enums.FlightStatus;
import com.airline.entity.enums.SeatClass;
import com.airline.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private AirportRepository airportRepository;
    @Autowired private AirlineRepository airlineRepository;
    @Autowired private AircraftModelRepository aircraftModelRepository;
    @Autowired private AircraftRepository aircraftRepository;
    @Autowired private SeatRepository seatRepository;
    @Autowired private FlightRepository flightRepository;
    @Autowired private PassengerRepository passengerRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (airportRepository.count() > 0) return;

        // 1. Seed Airports (Domestic & International)
        Airport del = airportRepository.save(new Airport("DEL", "Indira Gandhi International Airport", "New Delhi", "India"));
        Airport bom = airportRepository.save(new Airport("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India"));
        Airport blr = airportRepository.save(new Airport("BLR", "Kempegowda International Airport", "Bengaluru", "India"));
        Airport hyd = airportRepository.save(new Airport("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India"));
        Airport maa = airportRepository.save(new Airport("MAA", "Chennai International Airport", "Chennai", "India"));
        Airport ccu = airportRepository.save(new Airport("CCU", "Netaji Subhash Chandra Bose International Airport", "Kolkata", "India"));
        Airport jfk = airportRepository.save(new Airport("JFK", "John F. Kennedy International Airport", "New York", "USA"));
        Airport lax = airportRepository.save(new Airport("LAX", "Los Angeles International Airport", "Los Angeles", "USA"));
        Airport lhr = airportRepository.save(new Airport("LHR", "Heathrow Airport", "London", "UK"));
        Airport dxb = airportRepository.save(new Airport("DXB", "Dubai International Airport", "Dubai", "UAE"));

        // 2. Seed Airlines
        Airline indigo = airlineRepository.save(new Airline("IndiGo", "6E", "India"));
        Airline airIndia = airlineRepository.save(new Airline("Air India", "AI", "India"));
        Airline delta = airlineRepository.save(new Airline("Delta Air Lines", "DL", "USA"));
        Airline britishAirways = airlineRepository.save(new Airline("British Airways", "BA", "UK"));

        // 3. Seed Aircraft Models & Aircraft
        AircraftModel a320 = aircraftModelRepository.save(new AircraftModel("Airbus", "A320neo", 180));
        AircraftModel b777 = aircraftModelRepository.save(new AircraftModel("Boeing", "777-300ER", 300));
        Aircraft aircraft1 = aircraftRepository.save(new Aircraft("VT-IND01", a320, indigo));
        Aircraft aircraft2 = aircraftRepository.save(new Aircraft("N102DL", a320, delta));
        Aircraft aircraft3 = aircraftRepository.save(new Aircraft("G-STBA", b777, britishAirways));

        // 4. Seed Seats (Columns A, B, C, D)
        List<Seat> seats = new ArrayList<>();
        String[] cols = {"A", "B", "C", "D"};
        for (Aircraft ac : List.of(aircraft1, aircraft2, aircraft3)) {
            for (int row = 1; row <= 12; row++) {
                SeatClass seatClass = (row <= 2) ? SeatClass.BUSINESS : SeatClass.ECONOMY;
                for (String col : cols) {
                    seats.add(new Seat(row + col, seatClass, ac));
                }
            }
        }
        seatRepository.saveAll(seats);

        // 5. Seed Flights
        LocalDateTime now = LocalDateTime.now();
        List<Flight> flights = new ArrayList<>();

        // DEL <-> BOM
        Flight f1 = new Flight();
        f1.setFlightNumber("6E201");
        f1.setAircraft(aircraft1);
        f1.setDepartureAirport(del);
        f1.setArrivalAirport(bom);
        f1.setDepartureTime(now.plusDays(1).withHour(6).withMinute(30).withSecond(0));
        f1.setArrivalTime(now.plusDays(1).withHour(8).withMinute(45).withSecond(0));
        f1.setBasePrice(new BigDecimal("5240.00"));
        f1.setStatus(FlightStatus.SCHEDULED);
        flights.add(f1);

        Flight f2 = new Flight();
        f2.setFlightNumber("6E205");
        f2.setAircraft(aircraft1);
        f2.setDepartureAirport(del);
        f2.setArrivalAirport(bom);
        f2.setDepartureTime(now.plusDays(1).withHour(17).withMinute(15).withSecond(0));
        f2.setArrivalTime(now.plusDays(1).withHour(19).withMinute(30).withSecond(0));
        f2.setBasePrice(new BigDecimal("5890.00"));
        f2.setStatus(FlightStatus.SCHEDULED);
        flights.add(f2);

        Flight f3 = new Flight();
        f3.setFlightNumber("AI502");
        f3.setAircraft(aircraft1);
        f3.setDepartureAirport(bom);
        f3.setArrivalAirport(del);
        f3.setDepartureTime(now.plusDays(1).withHour(10).withMinute(0).withSecond(0));
        f3.setArrivalTime(now.plusDays(1).withHour(12).withMinute(15).withSecond(0));
        f3.setBasePrice(new BigDecimal("5410.00"));
        f3.setStatus(FlightStatus.SCHEDULED);
        flights.add(f3);

        Flight f4 = new Flight();
        f4.setFlightNumber("6E309");
        f4.setAircraft(aircraft1);
        f4.setDepartureAirport(del);
        f4.setArrivalAirport(blr);
        f4.setDepartureTime(now.plusDays(1).withHour(14).withMinute(0).withSecond(0));
        f4.setArrivalTime(now.plusDays(1).withHour(16).withMinute(45).withSecond(0));
        f4.setBasePrice(new BigDecimal("6120.00"));
        f4.setStatus(FlightStatus.SCHEDULED);
        flights.add(f4);

        // JFK <-> LAX (Matching Frontend Default Search and Quick Destination)
        Flight f5 = new Flight();
        f5.setFlightNumber("DL404");
        f5.setAircraft(aircraft2);
        f5.setDepartureAirport(jfk);
        f5.setArrivalAirport(lax);
        f5.setDepartureTime(now.plusDays(1).withHour(8).withMinute(30).withSecond(0));
        f5.setArrivalTime(now.plusDays(1).withHour(11).withMinute(45).withSecond(0));
        f5.setBasePrice(new BigDecimal("289.00"));
        f5.setStatus(FlightStatus.SCHEDULED);
        flights.add(f5);

        Flight f6 = new Flight();
        f6.setFlightNumber("DL812");
        f6.setAircraft(aircraft2);
        f6.setDepartureAirport(jfk);
        f6.setArrivalAirport(lax);
        f6.setDepartureTime(now.plusDays(1).withHour(16).withMinute(0).withSecond(0));
        f6.setArrivalTime(now.plusDays(1).withHour(19).withMinute(15).withSecond(0));
        f6.setBasePrice(new BigDecimal("320.00"));
        f6.setStatus(FlightStatus.SCHEDULED);
        flights.add(f6);

        // LHR <-> DXB (Matching Frontend Quick Destination)
        Flight f7 = new Flight();
        f7.setFlightNumber("BA105");
        f7.setAircraft(aircraft3);
        f7.setDepartureAirport(lhr);
        f7.setArrivalAirport(dxb);
        f7.setDepartureTime(now.plusDays(1).withHour(13).withMinute(45).withSecond(0));
        f7.setArrivalTime(now.plusDays(1).withHour(23).withMinute(55).withSecond(0));
        f7.setBasePrice(new BigDecimal("450.00"));
        f7.setStatus(FlightStatus.SCHEDULED);
        flights.add(f7);

        flightRepository.saveAll(flights);

        // 6. Seed Test Passengers
        Passenger seedPassenger = new Passenger();
        seedPassenger.setFirstName("Aarav");
        seedPassenger.setLastName("Sharma");
        seedPassenger.setEmail("aarav@gmail.com");
        seedPassenger.setPassword(passwordEncoder.encode("Password@123"));
        seedPassenger.setPhone("+919876543210");
        seedPassenger.setPassportNumber("Z1234567");
        seedPassenger.setDateOfBirth(LocalDate.of(2001, 8, 20));
        seedPassenger.setGender("M");
        passengerRepository.save(seedPassenger);

        Passenger demoPassenger = new Passenger();
        demoPassenger.setFirstName("John");
        demoPassenger.setLastName("Doe");
        demoPassenger.setEmail("traveler@airline.com");
        demoPassenger.setPassword(passwordEncoder.encode("Password@123"));
        demoPassenger.setPhone("+1234567890");
        demoPassenger.setPassportNumber("A9876543");
        demoPassenger.setDateOfBirth(LocalDate.of(1995, 5, 12));
        demoPassenger.setGender("M");
        passengerRepository.save(demoPassenger);
    }
}
