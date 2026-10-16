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

        // ====================================================================
        // 1. Seed 40 Major Indian Domestic Stations + 20 Global International Hubs
        // ====================================================================
        Map<String, Airport> ap = new LinkedHashMap<>();

        // --- Major Indian Domestic Hubs & Regional Centers ---
        ap.put("DEL", airportRepository.save(new Airport("DEL", "Indira Gandhi International Airport", "New Delhi", "India")));
        ap.put("BOM", airportRepository.save(new Airport("BOM", "Chhatrapati Shivaji Maharaj International Airport", "Mumbai", "India")));
        ap.put("BLR", airportRepository.save(new Airport("BLR", "Kempegowda International Airport", "Bengaluru", "India")));
        ap.put("MAA", airportRepository.save(new Airport("MAA", "Chennai International Airport", "Chennai", "India")));
        ap.put("HYD", airportRepository.save(new Airport("HYD", "Rajiv Gandhi International Airport", "Hyderabad", "India")));
        ap.put("CCU", airportRepository.save(new Airport("CCU", "Netaji Subhash Chandra Bose International Airport", "Kolkata", "India")));
        ap.put("IDR", airportRepository.save(new Airport("IDR", "Devi Ahilyabai Holkar Airport", "Indore", "India")));
        ap.put("BHO", airportRepository.save(new Airport("BHO", "Raja Bhoj Airport", "Bhopal", "India")));
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
        ap.put("NAG", airportRepository.save(new Airport("NAG", "Dr. Babasaheb Ambedkar International Airport", "Nagpur", "India")));
        ap.put("VTZ", airportRepository.save(new Airport("VTZ", "Visakhapatnam International Airport", "Visakhapatnam", "India")));
        ap.put("VNS", airportRepository.save(new Airport("VNS", "Lal Bahadur Shastri International Airport", "Varanasi", "India")));
        ap.put("ATQ", airportRepository.save(new Airport("ATQ", "Sri Guru Ram Dass Jee International Airport", "Amritsar", "India")));
        ap.put("CJB", airportRepository.save(new Airport("CJB", "Coimbatore International Airport", "Coimbatore", "India")));
        ap.put("UDR", airportRepository.save(new Airport("UDR", "Maharana Pratap Airport", "Udaipur", "India")));
        ap.put("IXE", airportRepository.save(new Airport("IXE", "Mangaluru International Airport", "Mangalore", "India")));
        ap.put("DED", airportRepository.save(new Airport("DED", "Jolly Grant Airport", "Dehradun", "India")));
        ap.put("RPR", airportRepository.save(new Airport("RPR", "Swami Vivekananda Airport", "Raipur", "India")));
        ap.put("IXB", airportRepository.save(new Airport("IXB", "Bagdogra International Airport", "Siliguri / Bagdogra", "India")));
        ap.put("IXJ", airportRepository.save(new Airport("IXJ", "Jammu Civil Enclave", "Jammu", "India")));
        ap.put("IXL", airportRepository.save(new Airport("IXL", "Kushok Bakula Rimpochee Airport", "Leh (Ladakh)", "India")));
        ap.put("VGA", airportRepository.save(new Airport("VGA", "Vijayawada International Airport", "Vijayawada", "India")));
        ap.put("IXU", airportRepository.save(new Airport("IXU", "Chhatrapati Sambhajinagar Airport", "Aurangabad", "India")));
        ap.put("TRZ", airportRepository.save(new Airport("TRZ", "Tiruchirappalli International Airport", "Tiruchirappalli", "India")));
        ap.put("IXZ", airportRepository.save(new Airport("IXZ", "Veer Savarkar International Airport", "Port Blair (Andaman)", "India")));
        ap.put("IXA", airportRepository.save(new Airport("IXA", "Maharaja Bir Bikram Airport", "Agartala", "India")));
        ap.put("GWL", airportRepository.save(new Airport("GWL", "Rajmata Vijaya Raje Scindia Airport", "Gwalior", "India")));
        ap.put("JDH", airportRepository.save(new Airport("JDH", "Jodhpur Airport", "Jodhpur", "India")));

        // --- Major International Destinations & Global Hubs ---
        ap.put("DXB", airportRepository.save(new Airport("DXB", "Dubai International Airport", "Dubai", "UAE")));
        ap.put("SIN", airportRepository.save(new Airport("SIN", "Singapore Changi Airport", "Singapore", "Singapore")));
        ap.put("BKK", airportRepository.save(new Airport("BKK", "Suvarnabhumi Airport", "Bangkok", "Thailand")));
        ap.put("LHR", airportRepository.save(new Airport("LHR", "Heathrow Airport", "London", "UK")));
        ap.put("JFK", airportRepository.save(new Airport("JFK", "John F. Kennedy International Airport", "New York", "USA")));
        ap.put("DOH", airportRepository.save(new Airport("DOH", "Hamad International Airport", "Doha", "Qatar")));
        ap.put("CDG", airportRepository.save(new Airport("CDG", "Charles de Gaulle Airport", "Paris", "France")));
        ap.put("FRA", airportRepository.save(new Airport("FRA", "Frankfurt Airport", "Frankfurt", "Germany")));
        ap.put("LAX", airportRepository.save(new Airport("LAX", "Los Angeles International Airport", "Los Angeles", "USA")));
        ap.put("HND", airportRepository.save(new Airport("HND", "Haneda Airport", "Tokyo", "Japan")));
        ap.put("SYD", airportRepository.save(new Airport("SYD", "Sydney Kingsford Smith Airport", "Sydney", "Australia")));
        ap.put("YYZ", airportRepository.save(new Airport("YYZ", "Toronto Pearson International Airport", "Toronto", "Canada")));
        ap.put("KUL", airportRepository.save(new Airport("KUL", "Kuala Lumpur International Airport", "Kuala Lumpur", "Malaysia")));
        ap.put("AMS", airportRepository.save(new Airport("AMS", "Amsterdam Airport Schiphol", "Amsterdam", "Netherlands")));
        ap.put("ZRH", airportRepository.save(new Airport("ZRH", "Zurich Airport", "Zurich", "Switzerland")));
        ap.put("FCO", airportRepository.save(new Airport("FCO", "Leonardo da Vinci–Fiumicino Airport", "Rome", "Italy")));
        ap.put("ICN", airportRepository.save(new Airport("ICN", "Incheon International Airport", "Seoul", "South Korea")));
        ap.put("IST", airportRepository.save(new Airport("IST", "Istanbul Airport", "Istanbul", "Turkey")));
        ap.put("KWI", airportRepository.save(new Airport("KWI", "Kuwait International Airport", "Kuwait City", "Kuwait")));
        ap.put("MUC", airportRepository.save(new Airport("MUC", "Munich Airport", "Munich", "Germany")));

        // ====================================================================
        // 2. Seed Airlines (Domestic & International)
        // ====================================================================
        Airline indigo = airlineRepository.save(new Airline("IndiGo", "6E", "India"));
        Airline airIndia = airlineRepository.save(new Airline("Air India", "AI", "India"));
        Airline vistara = airlineRepository.save(new Airline("Vistara", "UK", "India"));
        Airline spicejet = airlineRepository.save(new Airline("SpiceJet", "SG", "India"));
        Airline akasa = airlineRepository.save(new Airline("Akasa Air", "QP", "India"));
        Airline emirates = airlineRepository.save(new Airline("Emirates", "EK", "UAE"));
        Airline singaporeAir = airlineRepository.save(new Airline("Singapore Airlines", "SQ", "Singapore"));
        Airline qatarAir = airlineRepository.save(new Airline("Qatar Airways", "QR", "Qatar"));
        Airline britishAirways = airlineRepository.save(new Airline("British Airways", "BA", "UK"));
        Airline lufthansa = airlineRepository.save(new Airline("Lufthansa", "LH", "Germany"));
        Airline delta = airlineRepository.save(new Airline("Delta Air Lines", "DL", "USA"));

        // ====================================================================
        // 3. Seed Aircraft Models & Physical Fleet
        // ====================================================================
        AircraftModel a320 = aircraftModelRepository.save(new AircraftModel("Airbus", "A320neo", 180));
        AircraftModel a321 = aircraftModelRepository.save(new AircraftModel("Airbus", "A321neo", 220));
        AircraftModel b737 = aircraftModelRepository.save(new AircraftModel("Boeing", "737 MAX 8", 189));
        AircraftModel b777 = aircraftModelRepository.save(new AircraftModel("Boeing", "777-300ER", 300));
        AircraftModel b787 = aircraftModelRepository.save(new AircraftModel("Boeing", "787-9 Dreamliner", 296));
        AircraftModel a350 = aircraftModelRepository.save(new AircraftModel("Airbus", "A350-900", 314));

        Aircraft ac1 = aircraftRepository.save(new Aircraft("VT-IND01", a320, indigo));
        Aircraft ac2 = aircraftRepository.save(new Aircraft("VT-AIC01", a320, airIndia));
        Aircraft ac3 = aircraftRepository.save(new Aircraft("VT-VIS01", a321, vistara));
        Aircraft ac4 = aircraftRepository.save(new Aircraft("VT-SPJ01", b737, spicejet));
        Aircraft ac5 = aircraftRepository.save(new Aircraft("VT-AKA01", b737, akasa));
        Aircraft ac6 = aircraftRepository.save(new Aircraft("A6-EGB", b777, emirates));
        Aircraft ac7 = aircraftRepository.save(new Aircraft("9V-SMG", a350, singaporeAir));
        Aircraft ac8 = aircraftRepository.save(new Aircraft("A7-BHA", b787, qatarAir));
        Aircraft ac9 = aircraftRepository.save(new Aircraft("G-STBA", b787, britishAirways));
        Aircraft ac10 = aircraftRepository.save(new Aircraft("D-AIMA", a350, lufthansa));
        Aircraft ac11 = aircraftRepository.save(new Aircraft("N102DL", b777, delta));

        List<Aircraft> fleet = List.of(ac1, ac2, ac3, ac4, ac5, ac6, ac7, ac8, ac9, ac10, ac11);

        // ====================================================================
        // 4. Seed Seats for Aircraft (Rows 1-8, Columns A, B, C, D)
        // ====================================================================
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

        // ====================================================================
        // 5. Build Comprehensive Network Across Domestic & International Stations
        // ====================================================================
        LocalDateTime now = LocalDateTime.now();
        List<Flight> flights = new ArrayList<>();

        String[][] bidirectionalRoutes = {
            // ================================================================
            // INDORE (IDR) - Robust Nationwide & International Hub Connectivity
            // ================================================================
            {"IDR", "DEL"},  // Indore <-> Delhi (High Frequency)
            {"IDR", "BOM"},  // Indore <-> Mumbai (High Frequency)
            {"IDR", "BLR"},  // Indore <-> Bengaluru (Tech Corridor)
            {"IDR", "HYD"},  // Indore <-> Hyderabad
            {"IDR", "GOI"},  // Indore <-> Goa (Leisure)
            {"IDR", "PNQ"},  // Indore <-> Pune (Student & Business)
            {"IDR", "AMD"},  // Indore <-> Ahmedabad
            {"IDR", "JAI"},  // Indore <-> Jaipur
            {"IDR", "BHO"},  // Indore <-> Bhopal (MP Intercity)
            {"IDR", "NAG"},  // Indore <-> Nagpur
            {"IDR", "CCU"},  // Indore <-> Kolkata
            {"IDR", "LKO"},  // Indore <-> Lucknow
            {"IDR", "UDR"},  // Indore <-> Udaipur
            {"IDR", "RPR"},  // Indore <-> Raipur
            {"IDR", "IXC"},  // Indore <-> Chandigarh
            {"IDR", "COK"},  // Indore <-> Kochi
            {"IDR", "MAA"},  // Indore <-> Chennai
            {"IDR", "DXB"},  // Indore <-> Dubai (Direct International Route!)
            {"IDR", "SIN"},  // Indore <-> Singapore

            // ================================================================
            // DELHI (DEL) - Golden Triangle & Domestic Connectivity
            // ================================================================
            {"DEL", "BOM"}, {"DEL", "BLR"}, {"DEL", "MAA"}, {"DEL", "HYD"}, {"DEL", "CCU"},
            {"DEL", "GOI"}, {"DEL", "COK"}, {"DEL", "IXR"}, {"DEL", "AMD"}, {"DEL", "PNQ"},
            {"DEL", "JAI"}, {"DEL", "LKO"}, {"DEL", "GAU"}, {"DEL", "TRV"}, {"DEL", "IXC"},
            {"DEL", "PAT"}, {"DEL", "BBI"}, {"DEL", "SXR"}, {"DEL", "NAG"}, {"DEL", "VTZ"},
            {"DEL", "VNS"}, {"DEL", "ATQ"}, {"DEL", "CJB"}, {"DEL", "BHO"}, {"DEL", "UDR"},
            {"DEL", "DED"}, {"DEL", "RPR"}, {"DEL", "IXB"}, {"DEL", "IXJ"}, {"DEL", "IXL"},
            {"DEL", "IXZ"}, {"DEL", "GWL"}, {"DEL", "JDH"},

            // ================================================================
            // MUMBAI (BOM) - Financial Capital Domestic Connections
            // ================================================================
            {"BOM", "BLR"}, {"BOM", "MAA"}, {"BOM", "HYD"}, {"BOM", "CCU"}, {"BOM", "GOI"},
            {"BOM", "COK"}, {"BOM", "IXR"}, {"BOM", "AMD"}, {"BOM", "PNQ"}, {"BOM", "JAI"},
            {"BOM", "LKO"}, {"BOM", "GAU"}, {"BOM", "TRV"}, {"BOM", "IXC"}, {"BOM", "PAT"},
            {"BOM", "BBI"}, {"BOM", "SXR"}, {"BOM", "NAG"}, {"BOM", "VTZ"}, {"BOM", "VNS"},
            {"BOM", "ATQ"}, {"BOM", "CJB"}, {"BOM", "BHO"}, {"BOM", "UDR"}, {"BOM", "IXE"},
            {"BOM", "IXU"}, {"BOM", "RPR"}, {"BOM", "DED"}, {"BOM", "IXZ"}, {"BOM", "JDH"},

            // ================================================================
            // BENGALURU, HYDERABAD, CHENNAI, KOLKATA REGIONAL INTERCONNECTS
            // ================================================================
            {"BLR", "MAA"}, {"BLR", "HYD"}, {"BLR", "CCU"}, {"BLR", "GOI"}, {"BLR", "COK"},
            {"BLR", "IXR"}, {"BLR", "AMD"}, {"BLR", "PNQ"}, {"BLR", "TRV"}, {"BLR", "PAT"},
            {"BLR", "BBI"}, {"BLR", "NAG"}, {"BLR", "VTZ"}, {"BLR", "CJB"}, {"BLR", "IXE"},
            {"BLR", "VGA"}, {"BLR", "BHO"}, {"BLR", "IXZ"},
            {"HYD", "MAA"}, {"HYD", "GOI"}, {"HYD", "IXR"}, {"HYD", "PNQ"}, {"HYD", "BBI"},
            {"HYD", "NAG"}, {"HYD", "VTZ"}, {"HYD", "VGA"}, {"HYD", "BHO"}, {"HYD", "COK"},
            {"CCU", "MAA"}, {"CCU", "HYD"}, {"CCU", "GOI"}, {"CCU", "IXR"}, {"CCU", "GAU"},
            {"CCU", "PAT"}, {"CCU", "BBI"}, {"CCU", "VTZ"}, {"CCU", "VNS"}, {"CCU", "IXB"},
            {"CCU", "IXA"}, {"CCU", "IXZ"},
            {"MAA", "COK"}, {"MAA", "IXR"}, {"MAA", "TRV"}, {"MAA", "VTZ"}, {"MAA", "CJB"},
            {"MAA", "IXE"}, {"MAA", "TRZ"}, {"MAA", "IXZ"},

            // ================================================================
            // INTER-REGIONAL LEISURE & PILGRIMAGE CORRIDORS
            // ================================================================
            {"GOI", "PNQ"}, {"GOI", "AMD"}, {"GOI", "COK"},
            {"COK", "TRV"}, {"COK", "CJB"}, {"COK", "IXE"},
            {"AMD", "PNQ"}, {"AMD", "JAI"}, {"AMD", "UDR"},
            {"PNQ", "NAG"}, {"PNQ", "IXU"},
            {"JAI", "IXC"}, {"JAI", "ATQ"}, {"JAI", "UDR"}, {"JAI", "JDH"},
            {"LKO", "PAT"}, {"LKO", "VNS"}, {"LKO", "DED"},
            {"BHO", "DEL"}, {"BHO", "BOM"}, {"BHO", "BLR"}, {"BHO", "GWL"},
            {"GAU", "PAT"}, {"GAU", "IXB"}, {"GAU", "IXA"},
            {"IXC", "SXR"}, {"IXC", "ATQ"}, {"IXC", "IXL"}, {"IXJ", "SXR"}, {"IXJ", "DEL"},
            {"PAT", "BBI"}, {"PAT", "VNS"},
            {"DED", "BOM"}, {"DED", "BLR"},
            {"RPR", "NAG"}, {"RPR", "BLR"},
            {"VGA", "VTZ"}, {"VGA", "MAA"},

            // ================================================================
            // INTERNATIONAL FLIGHT ROUTES (Connecting India & World Hubs)
            // ================================================================
            // Dubai (DXB) Connections
            {"DEL", "DXB"}, {"BOM", "DXB"}, {"BLR", "DXB"}, {"HYD", "DXB"}, {"MAA", "DXB"},
            {"CCU", "DXB"}, {"COK", "DXB"}, {"AMD", "DXB"}, {"PNQ", "DXB"},

            // Singapore (SIN) Connections
            {"DEL", "SIN"}, {"BOM", "SIN"}, {"BLR", "SIN"}, {"MAA", "SIN"}, {"CCU", "SIN"},
            {"HYD", "SIN"}, {"COK", "SIN"}, {"TRZ", "SIN"},

            // Bangkok (BKK) Connections
            {"DEL", "BKK"}, {"BOM", "BKK"}, {"CCU", "BKK"}, {"BLR", "BKK"},

            // London Heathrow (LHR) Connections
            {"DEL", "LHR"}, {"BOM", "LHR"}, {"BLR", "LHR"}, {"HYD", "LHR"}, {"AMD", "LHR"},

            // New York (JFK) & North America Connections
            {"DEL", "JFK"}, {"BOM", "JFK"}, {"BLR", "JFK"},
            {"DEL", "YYZ"}, {"BOM", "YYZ"},
            {"DEL", "LAX"}, {"BOM", "LAX"},

            // Doha (DOH) Connections
            {"DEL", "DOH"}, {"BOM", "DOH"}, {"BLR", "DOH"}, {"HYD", "DOH"}, {"COK", "DOH"},

            // Paris (CDG) & Frankfurt (FRA) & European Hubs
            {"DEL", "CDG"}, {"BOM", "CDG"}, {"BLR", "CDG"},
            {"DEL", "FRA"}, {"BOM", "FRA"}, {"BLR", "FRA"},
            {"DEL", "AMS"}, {"BOM", "AMS"},
            {"DEL", "ZRH"}, {"BOM", "ZRH"},
            {"DEL", "FCO"}, {"DEL", "MUC"},

            // Asia-Pacific Connections
            {"DEL", "HND"}, {"BOM", "HND"},
            {"DEL", "SYD"}, {"BOM", "SYD"},
            {"DEL", "KUL"}, {"BOM", "KUL"}, {"MAA", "KUL"},
            {"DEL", "ICN"},
            {"DEL", "IST"}, {"BOM", "IST"},
            {"DEL", "KWI"}, {"BOM", "KWI"}, {"COK", "KWI"},

            // International-to-International Hub Pairs
            {"LHR", "JFK"}, {"LHR", "DXB"}, {"CDG", "JFK"}, {"SIN", "SYD"},
            {"HND", "LAX"}, {"FRA", "JFK"}, {"DXB", "LHR"}, {"DOH", "LHR"}
        };

        // ====================================================================
        // Generate Flights for Multiple Dates (Every day for next 14 days)
        // ====================================================================
        int flightCounter = 101;
        for (String[] route : bidirectionalRoutes) {
            String originCode = route[0];
            String destCode = route[1];

            Airport origin = ap.get(originCode);
            Airport dest = ap.get(destCode);
            if (origin == null || dest == null) continue;

            // Generate across next 14 days so searches on any chosen date succeed
            for (int day = 0; day <= 14; day += 2) {
                Aircraft acOut = fleet.get(flightCounter % fleet.size());
                Aircraft acRet = fleet.get((flightCounter + 3) % fleet.size());

                String outCarrier = acOut.getAirline().getCode();
                String retCarrier = acRet.getAirline().getCode();

                int outHour = 6 + (flightCounter % 14);
                int retHour = 13 + (flightCounter % 10);
                int durationMinutes = calculateDuration(originCode, destCode);

                BigDecimal fare = determineFare(originCode, destCode);

                // 1. OUTBOUND FLIGHT (Origin -> Destination)
                LocalDateTime depOut = now.plusDays(day).withHour(outHour).withMinute(15).withSecond(0);
                LocalDateTime arrOut = depOut.plusMinutes(durationMinutes);

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
                LocalDateTime depRet = now.plusDays(day + 1).withHour(retHour).withMinute(45).withSecond(0);
                LocalDateTime arrRet = depRet.plusMinutes(durationMinutes);

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
        }

        flightRepository.saveAll(flights);

        // ====================================================================
        // 6. Seed Demo Passengers
        // ====================================================================
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

    private int calculateDuration(String u, String v) {
        boolean uIntl = isInternational(u);
        boolean vIntl = isInternational(v);
        if (uIntl && vIntl) return 420; // 7h between intercontinental hubs
        if (uIntl || vIntl) return 270; // 4.5h international
        if ("IDR".equals(u) || "IDR".equals(v)) {
            if ("BHO".equals(u) || "BHO".equals(v)) return 55; // Indore <-> Bhopal short hop
            if ("BOM".equals(u) || "BOM".equals(v)) return 75; // Indore <-> Mumbai 1h 15m
            if ("DEL".equals(u) || "DEL".equals(v)) return 85; // Indore <-> Delhi 1h 25m
            return 110;
        }
        return 135; // Default 2h 15m
    }

    private boolean isInternational(String code) {
        return List.of("DXB", "SIN", "BKK", "LHR", "JFK", "DOH", "CDG", "FRA", "LAX", "HND", 
                       "SYD", "YYZ", "KUL", "AMS", "ZRH", "FCO", "ICN", "IST", "KWI", "MUC")
                   .contains(code);
    }

    private BigDecimal determineFare(String u, String v) {
        boolean uIntl = isInternational(u);
        boolean vIntl = isInternational(v);

        if (uIntl && vIntl) {
            return new BigDecimal("38500.00");
        }
        if (uIntl || vIntl) {
            if ("DXB".equals(u) || "DXB".equals(v) || "SIN".equals(u) || "SIN".equals(v) || "BKK".equals(u) || "BKK".equals(v)) {
                return new BigDecimal("14500.00"); // Mid-haul international
            }
            return new BigDecimal("32000.00"); // Long-haul international (US/Europe/Australia)
        }

        // Special Indore domestic pricing
        if ("IDR".equals(u) || "IDR".equals(v)) {
            if ("BHO".equals(u) || "BHO".equals(v)) return new BigDecimal("1999.00");
            if ("BOM".equals(u) || "BOM".equals(v)) return new BigDecimal("3250.00");
            if ("DEL".equals(u) || "DEL".equals(v)) return new BigDecimal("3450.00");
            if ("BLR".equals(u) || "BLR".equals(v)) return new BigDecimal("4100.00");
            if ("GOI".equals(u) || "GOI".equals(v)) return new BigDecimal("3850.00");
            return new BigDecimal("3600.00");
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
