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
import java.util.*;

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

        // 1. Seed All 25 Major Indian Domestic Stations + 4 Key International Hubs
        Map<String, Airport> ap = new LinkedHashMap<>();
        ap.put("DEL", airportRepository.save(new Airport("DEL", "Indira Gandhi International Airport", "New Delhi", "India")));
        ap.put("BOM", airportRepository.save(new Airport("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India")));
        ap.put("BLR", airportRepository.save(new Airport("BLR", "Kempegowda International Airport", "Bengaluru", "India")));
        ap.put("MAA", airportRepository.save(new Airport("MAA", "Chennai International Airport", "Chennai", "India")));
        ap.put("HYD", airportRepository.save(new Airport("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India")));
        ap.put("CCU", airportRepository.save(new Airport("CCU", "Netaji Subhash Chandra Bose International Airport", "Kolkata", "India")));
        ap.put("GOI", airportRepository.save(new Airport("GOI", "Dabolim Airport", "Goa", "India")));
        ap.put("COK", airportRepository.save(new Airport("COK", "Cochin International Airport", "Kochi", "India")));
        ap.put("IXR", airportRepository.save(new Airport("IXR", "Birsa Munda Airport", "Ranchi", "India")));
        ap.put("AMD", airportRepository.save(new Airport("AMD", "Sardar Vallabhbhai Patel International Airport", "Ahmedabad", "India")));
        ap.put("PNQ", airportRepository.save(new Airport("PNQ", "Pune International Airport", "Pune", "India")));
        ap.put("JAI", airportRepository.save(new Airport("JAI", "Jaipur International Airport", "Jaipur", "India")));
        ap.put("LKO", airportRepository.save(new Airport("LKO", "Chaudhary Charan Singh International Airport", "Lucknow", "India")));
        ap.put("GAU", airportRepository.save(new Airport("GAU", "Lokpriya Gopinath Bordoloi International Airport", "Guwahati", "India")));
        ap.put("TRV", airportRepository.save(new Airport("TRV", "Thiruvananthapuram International Airport", "Thiruvananthapuram", "India")));
        ap.put("IXC", airportRepository.save(new Airport("IXC", "Shaheed Bhagat Singh International Airport", "Chandigarh", "India")));
        ap.put("PAT", airportRepository.save(new Airport("PAT", "Jay Prakash Narayan Airport", "Patna", "India")));
        ap.put("BBI", airportRepository.save(new Airport("BBI", "Biju Patnaik International Airport", "Bhubaneswar", "India")));
        ap.put("SXR", airportRepository.save(new Airport("SXR", "Sheikh ul-Alam International Airport", "Srinagar", "India")));
        ap.put("IDR", airportRepository.save(new Airport("IDR", "Devi Ahilyabai Holkar Airport", "Indore", "India")));
        ap.put("NAG", airportRepository.save(new Airport("NAG", "Dr. Babasaheb Ambedkar International Airport", "Nagpur", "India")));
        ap.put("VTZ", airportRepository.save(new Airport("VTZ", "Visakhapatnam International Airport", "Visakhapatnam", "India")));
        ap.put("VNS", airportRepository.save(new Airport("VNS", "Lal Bahadur Shastri International Airport", "Varanasi", "India")));
        ap.put("ATQ", airportRepository.save(new Airport("ATQ", "Sri Guru Ram Dass Jee International Airport", "Amritsar", "India")));
        ap.put("CJB", airportRepository.save(new Airport("CJB", "Coimbatore International Airport", "Coimbatore", "India")));

        // International Hubs
        ap.put("JFK", airportRepository.save(new Airport("JFK", "John F. Kennedy International Airport", "New York", "USA")));
        ap.put("LAX", airportRepository.save(new Airport("LAX", "Los Angeles International Airport", "Los Angeles", "USA")));
        ap.put("LHR", airportRepository.save(new Airport("LHR", "Heathrow Airport", "London", "UK")));
        ap.put("DXB", airportRepository.save(new Airport("DXB", "Dubai International Airport", "Dubai", "UAE")));

        // 2. Seed Airlines
        Airline indigo = airlineRepository.save(new Airline("IndiGo", "6E", "India"));
        Airline airIndia = airlineRepository.save(new Airline("Air India", "AI", "India"));
        Airline vistara = airlineRepository.save(new Airline("Vistara", "UK", "India"));
        Airline spicejet = airlineRepository.save(new Airline("SpiceJet", "SG", "India"));
        Airline akasa = airlineRepository.save(new Airline("Akasa Air", "QP", "India"));
        Airline delta = airlineRepository.save(new Airline("Delta Air Lines", "DL", "USA"));
        Airline britishAirways = airlineRepository.save(new Airline("British Airways", "BA", "UK"));

        // 3. Seed Aircraft Models & Physical Fleet
        AircraftModel a320 = aircraftModelRepository.save(new AircraftModel("Airbus", "A320neo", 180));
        AircraftModel a321 = aircraftModelRepository.save(new AircraftModel("Airbus", "A321neo", 220));
        AircraftModel b737 = aircraftModelRepository.save(new AircraftModel("Boeing", "737 MAX 8", 189));
        AircraftModel b777 = aircraftModelRepository.save(new AircraftModel("Boeing", "777-300ER", 300));
        AircraftModel b787 = aircraftModelRepository.save(new AircraftModel("Boeing", "787-9 Dreamliner", 296));

        Aircraft ac1 = aircraftRepository.save(new Aircraft("VT-IND01", a320, indigo));
        Aircraft ac2 = aircraftRepository.save(new Aircraft("VT-AIC01", a320, airIndia));
        Aircraft ac3 = aircraftRepository.save(new Aircraft("VT-VIS01", a321, vistara));
        Aircraft ac4 = aircraftRepository.save(new Aircraft("VT-SPJ01", b737, spicejet));
        Aircraft ac5 = aircraftRepository.save(new Aircraft("VT-AKA01", b737, akasa));
        Aircraft ac6 = aircraftRepository.save(new Aircraft("N102DL", b777, delta));
        Aircraft ac7 = aircraftRepository.save(new Aircraft("G-STBA", b787, britishAirways));

        List<Aircraft> fleet = List.of(ac1, ac2, ac3, ac4, ac5, ac6, ac7);

        // 4. Seed Seats for each aircraft (Rows 1-8, Columns A, B, C, D)
        List<Seat> seats = new ArrayList<>();
        String[] cols = {"A", "B", "C", "D"};
        for (Aircraft ac : fleet) {
            for (int row = 1; row <= 8; row++) {
                SeatClass seatClass = (row <= 2) ? SeatClass.BUSINESS : SeatClass.ECONOMY;
                for (String col : cols) {
                    seats.add(new Seat(row + col, seatClass, ac));
                }
            }
        }
        seatRepository.saveAll(seats);

        // 5. Build Comprehensive Bidirectional Route Network Across All Stations
        // Every single station has a rich set of destination stations for booking tickets,
        // with 100% reciprocal return trip flight coverage!
        LocalDateTime now = LocalDateTime.now();
        List<Flight> flights = new ArrayList<>();

        // Route definitions: each pair represents a bidirectional connection (outbound + return)
        String[][] bidirectionalRoutes = {
            // DEL connections (covers all regional & metro hubs)
            {"DEL", "BOM"}, {"DEL", "BLR"}, {"DEL", "MAA"}, {"DEL", "HYD"}, {"DEL", "CCU"},
            {"DEL", "GOI"}, {"DEL", "COK"}, {"DEL", "IXR"}, {"DEL", "AMD"}, {"DEL", "PNQ"},
            {"DEL", "JAI"}, {"DEL", "LKO"}, {"DEL", "GAU"}, {"DEL", "TRV"}, {"DEL", "IXC"},
            {"DEL", "PAT"}, {"DEL", "BBI"}, {"DEL", "SXR"}, {"DEL", "IDR"}, {"DEL", "NAG"},
            {"DEL", "VTZ"}, {"DEL", "VNS"}, {"DEL", "ATQ"}, {"DEL", "CJB"}, {"DEL", "DXB"},

            // BOM connections (covers West, South, Central & East hubs)
            {"BOM", "BLR"}, {"BOM", "MAA"}, {"BOM", "HYD"}, {"BOM", "CCU"}, {"BOM", "GOI"},
            {"BOM", "COK"}, {"BOM", "IXR"}, {"BOM", "AMD"}, {"BOM", "PNQ"}, {"BOM", "JAI"},
            {"BOM", "LKO"}, {"BOM", "GAU"}, {"BOM", "TRV"}, {"BOM", "IXC"}, {"BOM", "PAT"},
            {"BOM", "BBI"}, {"BOM", "SXR"}, {"BOM", "IDR"}, {"BOM", "NAG"}, {"BOM", "VTZ"},
            {"BOM", "VNS"}, {"BOM", "ATQ"}, {"BOM", "CJB"},

            // BLR connections (covers South, East & Central hubs)
            {"BLR", "MAA"}, {"BLR", "HYD"}, {"BLR", "CCU"}, {"BLR", "GOI"}, {"BLR", "COK"},
            {"BLR", "IXR"}, {"BLR", "AMD"}, {"BLR", "PNQ"}, {"BLR", "TRV"}, {"BLR", "PAT"},
            {"BLR", "BBI"}, {"BLR", "NAG"}, {"BLR", "VTZ"}, {"BLR", "CJB"},

            // CCU connections (covers East, Northeast & Central hubs)
            {"CCU", "MAA"}, {"CCU", "HYD"}, {"CCU", "GOI"}, {"CCU", "IXR"}, {"CCU", "GAU"},
            {"CCU", "PAT"}, {"CCU", "BBI"}, {"CCU", "VTZ"}, {"CCU", "VNS"},

            // HYD connections (covers South & Central hubs)
            {"HYD", "MAA"}, {"HYD", "GOI"}, {"HYD", "IXR"}, {"HYD", "PNQ"}, {"HYD", "BBI"},
            {"HYD", "NAG"}, {"HYD", "VTZ"},

            // MAA connections (covers South hubs)
            {"MAA", "COK"}, {"MAA", "IXR"}, {"MAA", "TRV"}, {"MAA", "VTZ"}, {"MAA", "CJB"},

            // IXR (Ranchi) connections (hub connectivity to multiple stations)
            {"IXR", "PAT"}, {"IXR", "BBI"},

            // Inter-regional connections
            {"GOI", "PNQ"}, {"GOI", "AMD"},
            {"COK", "TRV"}, {"COK", "CJB"},
            {"AMD", "PNQ"}, {"AMD", "JAI"}, {"AMD", "IDR"},
            {"PNQ", "NAG"},
            {"JAI", "IXC"}, {"JAI", "ATQ"},
            {"LKO", "PAT"}, {"LKO", "VNS"},
            {"GAU", "PAT"},
            {"TRV", "CJB"},
            {"IXC", "SXR"}, {"IXC", "ATQ"},
            {"PAT", "BBI"},
            {"SXR", "ATQ"},
            {"IDR", "NAG"},

            // International pairs
            {"JFK", "LAX"}, {"LHR", "DXB"}
        };

        int flightCounter = 101;
        for (String[] route : bidirectionalRoutes) {
            String originCode = route[0];
            String destCode = route[1];

            Airport origin = ap.get(originCode);
            Airport dest = ap.get(destCode);
            if (origin == null || dest == null) continue;

            Aircraft acOut = fleet.get(flightCounter % 5);
            Aircraft acRet = fleet.get((flightCounter + 2) % 5);

            String outCarrier = acOut.getAirline().getCode();
            String retCarrier = acRet.getAirline().getCode();

            int dayOffset = (flightCounter % 7) + 1;
            int outHour = 6 + (flightCounter % 12);
            int retHour = 14 + (flightCounter % 8);

            BigDecimal fare = determineFare(originCode, destCode);

            // 1. OUTBOUND FLIGHT (Origin -> Destination)
            LocalDateTime depOut = now.plusDays(dayOffset).withHour(outHour).withMinute(15).withSecond(0);
            LocalDateTime arrOut = depOut.plusHours(2).plusMinutes(15);

            Flight outFlight = new Flight();
            outFlight.setFlightNumber(outCarrier + flightCounter);
            outFlight.setAircraft(acOut);
            outFlight.setDepartureAirport(origin);
            outFlight.setArrivalAirport(dest);
            outFlight.setDepartureTime(depOut);
            outFlight.setArrivalTime(arrOut);
            outFlight.setBasePrice(fare);
            outFlight.setStatus(FlightStatus.SCHEDULED);
            flights.add(outFlight);
            flightCounter++;

            // 2. RETURN TRIP FLIGHT (Destination -> Origin)
            LocalDateTime depRet = now.plusDays(dayOffset + 2).withHour(retHour).withMinute(45).withSecond(0);
            LocalDateTime arrRet = depRet.plusHours(2).plusMinutes(10);

            Flight retFlight = new Flight();
            retFlight.setFlightNumber(retCarrier + flightCounter);
            retFlight.setAircraft(acRet);
            retFlight.setDepartureAirport(dest);
            retFlight.setArrivalAirport(origin);
            retFlight.setDepartureTime(depRet);
            retFlight.setArrivalTime(arrRet);
            retFlight.setBasePrice(fare.add(new BigDecimal("150.00")));
            retFlight.setStatus(FlightStatus.SCHEDULED);
            flights.add(retFlight);
            flightCounter++;
        }

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

    private BigDecimal determineFare(String u, String v) {
        if ("JFK".equals(u) || "LAX".equals(u) || "LHR".equals(u) || "DXB".equals(u)) {
            return new BigDecimal("450.00");
        }
        int hash = Math.abs(u.hashCode() + v.hashCode());
        int mod = hash % 4;
        switch (mod) {
            case 0: return new BigDecimal("2800.00");
            case 1: return new BigDecimal("3600.00");
            case 2: return new BigDecimal("4400.00");
            default: return new BigDecimal("5200.00");
        }
    }
}
