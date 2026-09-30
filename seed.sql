USE airline_db;

-- Temporarily disable foreign key checks so tables can be truncated cleanly
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE baggage;
TRUNCATE TABLE payment;
TRUNCATE TABLE ticket;
TRUNCATE TABLE booking;
TRUNCATE TABLE passenger;
TRUNCATE TABLE employee;
TRUNCATE TABLE flight;
TRUNCATE TABLE seat;
TRUNCATE TABLE aircraft;
TRUNCATE TABLE aircraft_model;
TRUNCATE TABLE airport;
TRUNCATE TABLE airline;

SET FOREIGN_KEY_CHECKS = 1;

-- 1. Airlines
INSERT INTO airline (airline_id, name, iata_code, country) VALUES
(1, 'Air India', 'AI', 'India'),
(2, 'IndiGo', '6E', 'India'),
(3, 'Vistara', 'UK', 'India'),
(4, 'SpiceJet', 'SG', 'India'),
(5, 'Akasa Air', 'QP', 'India');

-- 2. Airports (IXR has no departing flights scheduled to test zero-flight queries)
INSERT INTO airport (airport_id, iata_code, name, city, country) VALUES
(1, 'DEL', 'Indira Gandhi International Airport', 'New Delhi', 'India'),
(2, 'BOM', 'Chhatrapati Shivaji Maharaj International Airport', 'Mumbai', 'India'),
(3, 'BLR', 'Kempegowda International Airport', 'Bengaluru', 'India'),
(4, 'MAA', 'Chennai International Airport', 'Chennai', 'India'),
(5, 'HYD', 'Rajiv Gandhi International Airport', 'Hyderabad', 'India'),
(6, 'CCU', 'Netaji Subhash Chandra Bose International Airport', 'Kolkata', 'India'),
(7, 'GOI', 'Dabolim Airport', 'Goa', 'India'),
(8, 'COK', 'Cochin International Airport', 'Kochi', 'India'),
(9, 'IXR', 'Birsa Munda Airport', 'Ranchi', 'India');

-- 3. Aircraft Models
INSERT INTO aircraft_model (model_id, manufacturer, model_name, range_km) VALUES
(1, 'Airbus', 'A320neo', 6500),
(2, 'Airbus', 'A321neo', 7400),
(3, 'Boeing', '737 MAX 8', 6570),
(4, 'Boeing', '777-300ER', 13650),
(5, 'Airbus', 'A350-900', 15000),
(6, 'Boeing', '787-9 Dreamliner', 14140);

-- 4. Aircraft Fleet
INSERT INTO aircraft (aircraft_id, airline_id, model_id, registration_number, manufacture_year, status) VALUES
(1, 1, 1, 'VT-EXA', 2021, 'ACTIVE'),       -- Air India A320neo
(2, 1, 4, 'VT-ALX', 2019, 'ACTIVE'),       -- Air India 777-300ER
(3, 2, 1, 'VT-IFP', 2022, 'ACTIVE'),       -- IndiGo A320neo
(4, 2, 2, 'VT-ILQ', 2023, 'ACTIVE'),       -- IndiGo A321neo
(5, 3, 1, 'VT-TVA', 2020, 'ACTIVE'),       -- Vistara A320neo
(6, 4, 3, 'VT-SZK', 2021, 'ACTIVE'),       -- SpiceJet 737 MAX 8
(7, 5, 3, 'VT-YAA', 2022, 'ACTIVE'),       -- Akasa Air 737 MAX 8
(8, 1, 5, 'VT-JRA', 2024, 'ACTIVE');       -- Air India A350-900

-- 5. Seat Maps
-- Aircraft 1 (Air India A320neo): Seats 1 to 16
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(1, 1, '1A', 'BUSINESS'), (2, 1, '1B', 'BUSINESS'), (3, 1, '1C', 'BUSINESS'), (4, 1, '1D', 'BUSINESS'),
(5, 1, '2A', 'BUSINESS'), (6, 1, '2B', 'BUSINESS'), (7, 1, '2C', 'BUSINESS'), (8, 1, '2D', 'BUSINESS'),
(9, 1, '3A', 'ECONOMY'),  (10, 1, '3B', 'ECONOMY'), (11, 1, '3C', 'ECONOMY'), (12, 1, '3D', 'ECONOMY'),
(13, 1, '4A', 'ECONOMY'), (14, 1, '4B', 'ECONOMY'), (15, 1, '4C', 'ECONOMY'), (16, 1, '4D', 'ECONOMY');

-- Aircraft 2 (Air India 777-300ER): Seats 21 to 36
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(21, 2, '1A', 'BUSINESS'), (22, 2, '1B', 'BUSINESS'), (23, 2, '1J', 'BUSINESS'), (24, 2, '1K', 'BUSINESS'),
(25, 2, '2A', 'BUSINESS'), (26, 2, '2B', 'BUSINESS'), (27, 2, '2J', 'BUSINESS'), (28, 2, '2K', 'BUSINESS'),
(29, 2, '10A', 'ECONOMY'), (30, 2, '10B', 'ECONOMY'),(31, 2, '10C', 'ECONOMY'),(32, 2, '10D', 'ECONOMY'),
(33, 2, '11A', 'ECONOMY'), (34, 2, '11B', 'ECONOMY'),(35, 2, '11C', 'ECONOMY'),(36, 2, '11D', 'ECONOMY');

-- Aircraft 3 (IndiGo A320neo): Seats 41 to 56
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(41, 3, '1A', 'ECONOMY'), (42, 3, '1B', 'ECONOMY'), (43, 3, '1C', 'ECONOMY'), (44, 3, '1D', 'ECONOMY'),
(45, 3, '2A', 'ECONOMY'), (46, 3, '2B', 'ECONOMY'), (47, 3, '2C', 'ECONOMY'), (48, 3, '2D', 'ECONOMY'),
(49, 3, '3A', 'ECONOMY'), (50, 3, '3B', 'ECONOMY'), (51, 3, '3C', 'ECONOMY'), (52, 3, '3D', 'ECONOMY'),
(53, 3, '4A', 'ECONOMY'), (54, 3, '4B', 'ECONOMY'), (55, 3, '4C', 'ECONOMY'), (56, 3, '4D', 'ECONOMY');

-- Aircraft 4 (IndiGo A321neo): Seats 61 to 76
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(61, 4, '1A', 'ECONOMY'), (62, 4, '1B', 'ECONOMY'), (63, 4, '1C', 'ECONOMY'), (64, 4, '1D', 'ECONOMY'),
(65, 4, '2A', 'ECONOMY'), (66, 4, '2B', 'ECONOMY'), (67, 4, '2C', 'ECONOMY'), (68, 4, '2D', 'ECONOMY'),
(69, 4, '3A', 'ECONOMY'), (70, 4, '3B', 'ECONOMY'), (71, 4, '3C', 'ECONOMY'), (72, 4, '3D', 'ECONOMY'),
(73, 4, '4A', 'ECONOMY'), (74, 4, '4B', 'ECONOMY'), (75, 4, '4C', 'ECONOMY'), (76, 4, '4D', 'ECONOMY');

-- Aircraft 5 (Vistara A320neo): Seats 81 to 96
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(81, 5, '1A', 'BUSINESS'), (82, 5, '1C', 'BUSINESS'), (83, 5, '1D', 'BUSINESS'), (84, 5, '1F', 'BUSINESS'),
(85, 5, '2A', 'BUSINESS'), (86, 5, '2C', 'BUSINESS'), (87, 5, '2D', 'BUSINESS'), (88, 5, '2F', 'BUSINESS'),
(89, 5, '5A', 'ECONOMY'),  (90, 5, '5B', 'ECONOMY'),  (91, 5, '5C', 'ECONOMY'),  (92, 5, '5D', 'ECONOMY'),
(93, 5, '6A', 'ECONOMY'),  (94, 5, '6B', 'ECONOMY'),  (95, 5, '6C', 'ECONOMY'),  (96, 5, '6D', 'ECONOMY');

-- Aircraft 6 (SpiceJet 737 MAX 8): Seats 101 to 112
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(101, 6, '1A', 'ECONOMY'), (102, 6, '1B', 'ECONOMY'), (103, 6, '1C', 'ECONOMY'),
(104, 6, '2A', 'ECONOMY'), (105, 6, '2B', 'ECONOMY'), (106, 6, '2C', 'ECONOMY'),
(107, 6, '3A', 'ECONOMY'), (108, 6, '3B', 'ECONOMY'), (109, 6, '3C', 'ECONOMY'),
(110, 6, '4A', 'ECONOMY'), (111, 6, '4B', 'ECONOMY'), (112, 6, '4C', 'ECONOMY');

-- Aircraft 7 (Akasa Air 737 MAX 8): Seats 121 to 132
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(121, 7, '1A', 'ECONOMY'), (122, 7, '1B', 'ECONOMY'), (123, 7, '1C', 'ECONOMY'),
(124, 7, '2A', 'ECONOMY'), (125, 7, '2B', 'ECONOMY'), (126, 7, '2C', 'ECONOMY'),
(127, 7, '3A', 'ECONOMY'), (128, 7, '3B', 'ECONOMY'), (129, 7, '3C', 'ECONOMY'),
(130, 7, '4A', 'ECONOMY'), (131, 7, '4B', 'ECONOMY'), (132, 7, '4C', 'ECONOMY');

-- Aircraft 8 (Air India A350-900): Seats 141 to 152
INSERT INTO seat (seat_id, aircraft_id, seat_number, seat_class) VALUES
(141, 8, '1A', 'BUSINESS'), (142, 8, '1D', 'BUSINESS'), (143, 8, '1K', 'BUSINESS'),
(144, 8, '2A', 'BUSINESS'), (145, 8, '2D', 'BUSINESS'), (146, 8, '2K', 'BUSINESS'),
(147, 8, '12A', 'ECONOMY'), (148, 8, '12B', 'ECONOMY'), (149, 8, '12C', 'ECONOMY'),
(150, 8, '14A', 'ECONOMY'), (151, 8, '14B', 'ECONOMY'), (152, 8, '14C', 'ECONOMY');

-- 6. Scheduled Flights
-- Flight 101: Air India DEL -> BOM on 2026-10-15
-- Flight 125: Long haul (> 3 hours) DEL -> COK
-- IndiGo operates 11 flights across routes to support high-frequency queries
-- Flight 124: Scheduled flight with zero bookings
INSERT INTO flight (flight_id, flight_number, aircraft_id, departure_airport_id, arrival_airport_id, departure_time, arrival_time, base_price, status) VALUES
-- Air India Flights
(101, 'AI-805', 1, 1, 2, '2026-10-15 08:00:00', '2026-10-15 10:15:00', 4800.00, 'SCHEDULED'), -- DEL -> BOM
(102, 'AI-806', 1, 2, 1, '2026-10-18 19:30:00', '2026-10-18 21:45:00', 4950.00, 'SCHEDULED'), -- BOM -> DEL
(103, 'AI-504', 2, 1, 3, '2026-10-15 06:15:00', '2026-10-15 09:05:00', 5600.00, 'SCHEDULED'), -- DEL -> BLR
(104, 'AI-505', 2, 3, 1, '2026-10-16 17:00:00', '2026-10-16 19:45:00', 5400.00, 'SCHEDULED'), -- BLR -> DEL
(125, 'AI-481', 8, 1, 8, '2026-10-15 05:30:00', '2026-10-15 08:50:00', 6800.00, 'SCHEDULED'), -- DEL -> COK (3h 20m)

-- IndiGo Flights (11 flights)
(105, '6E-201', 3, 1, 2, '2026-10-15 11:00:00', '2026-10-15 13:15:00', 4200.00, 'SCHEDULED'),
(106, '6E-202', 3, 2, 3, '2026-10-15 14:30:00', '2026-10-15 16:15:00', 3800.00, 'SCHEDULED'),
(107, '6E-203', 3, 3, 5, '2026-10-15 17:45:00', '2026-10-15 18:55:00', 2900.00, 'SCHEDULED'),
(108, '6E-204', 3, 5, 4, '2026-10-16 07:15:00', '2026-10-16 08:30:00', 3100.00, 'SCHEDULED'),
(109, '6E-205', 3, 4, 6, '2026-10-16 10:00:00', '2026-10-16 12:20:00', 4500.00, 'SCHEDULED'),
(110, '6E-206', 4, 6, 1, '2026-10-16 14:00:00', '2026-10-16 16:30:00', 4900.00, 'SCHEDULED'),
(111, '6E-301', 4, 1, 7, '2026-10-17 09:30:00', '2026-10-17 12:00:00', 5100.00, 'SCHEDULED'),
(112, '6E-302', 4, 7, 2, '2026-10-17 13:30:00', '2026-10-17 14:45:00', 2700.00, 'SCHEDULED'),
(113, '6E-303', 4, 2, 5, '2026-10-17 16:15:00', '2026-10-17 17:40:00', 3300.00, 'SCHEDULED'),
(114, '6E-304', 4, 5, 1, '2026-10-18 06:45:00', '2026-10-18 09:05:00', 4400.00, 'SCHEDULED'),
(115, '6E-305', 4, 1, 3, '2026-10-18 11:30:00', '2026-10-18 14:15:00', 5300.00, 'SCHEDULED'),

-- Vistara Flights
(116, 'UK-945', 5, 1, 2, '2026-10-15 16:00:00', '2026-10-15 18:15:00', 5900.00, 'SCHEDULED'),
(117, 'UK-946', 5, 2, 1, '2026-10-16 09:00:00', '2026-10-16 11:15:00', 6100.00, 'SCHEDULED'),
(118, 'UK-812', 5, 1, 3, '2026-10-17 07:45:00', '2026-10-17 10:30:00', 6400.00, 'SCHEDULED'),

-- SpiceJet Flights
(119, 'SG-112', 6, 1, 7, '2026-10-15 13:00:00', '2026-10-15 15:35:00', 3900.00, 'SCHEDULED'),
(120, 'SG-113', 6, 7, 1, '2026-10-16 11:30:00', '2026-10-16 14:10:00', 4100.00, 'SCHEDULED'),

-- Akasa Air Flights
(121, 'QP-1301', 7, 3, 2, '2026-10-15 08:30:00', '2026-10-15 10:10:00', 3400.00, 'SCHEDULED'),
(122, 'QP-1302', 7, 2, 3, '2026-10-15 18:00:00', '2026-10-15 19:40:00', 3600.00, 'SCHEDULED'),

-- Unbooked Flight (tests NOT EXISTS query)
(124, 'AI-999', 1, 1, 6, '2026-10-25 22:00:00', '2026-10-26 00:15:00', 4100.00, 'SCHEDULED');

-- 7. Passengers
-- Passenger 12 (Aarav Sharma) has multiple bookings across airlines (tests frequent flyer and multi-airline queries)
-- Passenger 18 (Rohit Verma) has no bookings (tests outer join query)
INSERT INTO passenger (passenger_id, first_name, last_name, email, password_hash, phone, date_of_birth, gender, nationality, passport_number) VALUES
(1, 'Aditya', 'Verma', 'aditya.verma@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100001', '1988-04-12', 'M', 'Indian', 'P1029384'),
(2, 'Priya', 'Nair', 'priya.nair@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100002', '1992-09-25', 'F', 'Indian', 'P2938475'),
(3, 'Rohan', 'Mehta', 'rohan.mehta@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100003', '1995-01-18', 'M', 'Indian', 'P3847561'),
(4, 'Ananya', 'Iyer', 'ananya.iyer@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100004', '1990-11-05', 'F', 'Indian', 'P4758692'),
(5, 'Vikram', 'Singh', 'vikram.singh@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100005', '1985-07-30', 'M', 'Indian', 'P5869703'),
(6, 'Sneha', 'Patel', 'sneha.patel@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100006', '1994-03-14', 'F', 'Indian', 'P6970814'),
(7, 'Karan', 'Kapoor', 'karan.kapoor@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100007', '1991-12-08', 'M', 'Indian', NULL),
(8, 'Diya', 'Mukherjee', 'diya.m@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100008', '1996-06-21', 'F', 'Indian', NULL),
(9, 'Rahul', 'Deshmukh', 'rahul.d@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100009', '1989-10-19', 'M', 'Indian', 'P7081925'),
(10, 'Pooja', 'Reddy', 'pooja.reddy@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100010', '1993-08-11', 'F', 'Indian', 'P8192036'),
(11, 'Manish', 'Gupta', 'manish.g@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100011', '1987-02-15', 'M', 'Indian', 'P9203147'),
(12, 'Aarav', 'Sharma', 'aarav.sharma@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100012', '1990-05-15', 'M', 'Indian', 'P0314258'),
(13, 'Ishaan', 'Bhatia', 'ishaan.b@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100013', '1998-11-22', 'M', 'Indian', NULL),
(14, 'Tanvi', 'Chopra', 'tanvi.c@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100014', '1995-04-03', 'F', 'Indian', 'P1425369'),
(15, 'Siddharth', 'Joshi', 'sid.joshi@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100015', '1986-09-17', 'M', 'Indian', 'P2536470'),
(18, 'Rohit', 'Verma', 'rohit.verma@example.com', '$2a$12$e8Y4J2a7V.YfQ0w2G4uH8OGxU0v8m7Fz1Zp8d7Kq9L6m4N2b3C1e2', '+919811100018', '1997-03-29', 'M', 'Indian', NULL);

-- 8. Bookings
-- Booking 10 is a round trip (2 tickets)
-- Booking 55 is pending payment confirmation
-- Booking 60 has no successful payment
INSERT INTO booking (booking_id, booking_reference, passenger_id, booking_date, status) VALUES
(1, 'BK1001', 1, '2026-09-01 10:14:00', 'CONFIRMED'),
(2, 'BK1002', 2, '2026-09-02 11:30:00', 'CONFIRMED'),
(3, 'BK1003', 3, '2026-09-05 09:20:00', 'CONFIRMED'),
(4, 'BK1004', 4, '2026-09-08 14:45:00', 'CONFIRMED'),
(5, 'BK1005', 5, '2026-09-10 16:10:00', 'CONFIRMED'),
(6, 'BK1006', 6, '2026-09-12 18:25:00', 'CONFIRMED'),
(7, 'BK1007', 7, '2026-09-14 20:05:00', 'CONFIRMED'),
(8, 'BK1008', 8, '2026-09-15 08:40:00', 'CANCELLED'),
(9, 'BK1009', 9, '2026-09-16 12:15:00', 'CONFIRMED'),
(10, 'BK1010', 10, '2026-09-18 15:50:00', 'CONFIRMED'),
(11, 'BK1011', 11, '2026-09-20 17:35:00', 'CONFIRMED'),
(12, 'BK1012', 12, '2026-09-21 09:10:00', 'CONFIRMED'),
(13, 'BK1013', 12, '2026-09-22 13:40:00', 'CONFIRMED'),
(14, 'BK1014', 12, '2026-09-25 19:00:00', 'CONFIRMED'),
(15, 'BK1015', 13, '2026-09-26 11:20:00', 'CONFIRMED'),
(16, 'BK1016', 14, '2026-09-27 14:05:00', 'CONFIRMED'),
(17, 'BK1017', 15, '2026-09-28 16:55:00', 'CONFIRMED'),
(55, 'BK1055', 11, '2026-09-29 18:30:00', 'PENDING'),
(60, 'BK1060', 13, '2026-09-29 21:00:00', 'PENDING');

-- 9. Tickets
-- Seats strictly match the aircraft assigned to each flight
-- Ticket 71 is an issued business class ticket on flight 101
-- Cancelled ticket 8 has seat_id set to NULL
INSERT INTO ticket (ticket_id, ticket_number, booking_id, flight_id, seat_id, fare, status) VALUES
-- Flight 101 (Aircraft 1: Seats 1..16)
(1, 'TK-0987654321', 1, 101, 1, 8500.00, 'ISSUED'),   -- Seat 1A (Business)
(2, 'TK-0987654322', 2, 101, 2, 8500.00, 'ISSUED'),   -- Seat 1B (Business)
(3, 'TK-0987654323', 3, 101, 9, 4800.00, 'ISSUED'),   -- Seat 3A (Economy)
(4, 'TK-0987654324', 4, 101, 10, 4800.00, 'ISSUED'),  -- Seat 3B (Economy)
(71, 'TK-0987654371', 5, 101, 3, 8500.00, 'ISSUED'),  -- Seat 1C (Business)

-- Flight 102 (Return flight for round-trip booking 10)
(10, 'TK-0987654310', 10, 101, 11, 4800.00, 'ISSUED'), -- Outbound DEL->BOM
(11, 'TK-0987654311', 10, 102, 11, 4950.00, 'ISSUED'), -- Inbound BOM->DEL

-- Flight 103 (Aircraft 2: Seats 21..36)
(5, 'TK-0987654325', 6, 103, 21, 9500.00, 'ISSUED'),
(6, 'TK-0987654326', 7, 103, 29, 5600.00, 'ISSUED'),
(8, 'TK-0987654328', 8, 103, NULL, 5600.00, 'CANCELLED'),

-- Flight 105 (IndiGo Aircraft 3: Seats 41..56)
(9, 'TK-0987654329', 9, 105, 41, 4200.00, 'ISSUED'),

-- Passenger 12's tickets across Air India, IndiGo, and Vistara
(12, 'TK-0987654312', 12, 101, 4, 8500.00, 'ISSUED'),  -- Air India (Seat 1D)
(13, 'TK-0987654313', 13, 105, 42, 4200.00, 'ISSUED'), -- IndiGo (Seat 1B)
(14, 'TK-0987654314', 14, 116, 81, 9200.00, 'ISSUED'), -- Vistara (Seat 1A)

-- Flight 106 (Aircraft 3)
(15, 'TK-0987654315', 15, 106, 45, 3800.00, 'ISSUED'),

-- Flight 119 (SpiceJet Aircraft 6)
(16, 'TK-0987654316', 16, 119, 101, 3900.00, 'ISSUED'),

-- Flight 121 (Akasa Air Aircraft 7)
(17, 'TK-0987654317', 17, 121, 121, 3400.00, 'ISSUED'),

-- Pending ticket under Booking 55
(55, 'TK-0987654355', 55, 101, 12, 4800.00, 'PENDING');

-- 10. Payments
-- Includes successful payments, a refunded payment (booking 8), a failed payment (booking 60), and an old failed record (payment 99)
INSERT INTO payment (payment_id, booking_id, amount, method, status, transaction_ref, paid_at) VALUES
(1, 1, 8500.00, 'UPI', 'SUCCESS', 'TXN_UPI_20260901_001', '2026-09-01 10:15:30'),
(2, 2, 8500.00, 'CARD', 'SUCCESS', 'TXN_CRD_20260902_002', '2026-09-02 11:31:45'),
(3, 3, 4800.00, 'NET_BANKING', 'SUCCESS', 'TXN_NB_20260905_003', '2026-09-05 09:22:10'),
(4, 4, 4800.00, 'UPI', 'SUCCESS', 'TXN_UPI_20260908_004', '2026-09-08 14:46:12'),
(5, 5, 8500.00, 'CARD', 'SUCCESS', 'TXN_CRD_20260910_005', '2026-09-10 16:12:00'),
(6, 6, 9500.00, 'CARD', 'SUCCESS', 'TXN_CRD_20260912_006', '2026-09-12 18:26:40'),
(7, 7, 5600.00, 'WALLET', 'SUCCESS', 'TXN_WAL_20260914_007', '2026-09-14 20:06:50'),
(8, 8, 5600.00, 'UPI', 'REFUNDED', 'TXN_UPI_20260915_008', '2026-09-15 08:42:00'),
(9, 9, 4200.00, 'CARD', 'SUCCESS', 'TXN_CRD_20260916_009', '2026-09-16 12:17:15'),
(10, 10, 9750.00, 'NET_BANKING', 'SUCCESS', 'TXN_NB_20260918_010', '2026-09-18 15:52:30'),
(11, 11, 4800.00, 'UPI', 'SUCCESS', 'TXN_UPI_20260920_011', '2026-09-20 17:36:20'),
(12, 12, 8500.00, 'CARD', 'SUCCESS', 'TXN_CRD_20260921_012', '2026-09-21 09:12:05'),
(13, 13, 4200.00, 'UPI', 'SUCCESS', 'TXN_UPI_20260922_013', '2026-09-22 13:41:40'),
(14, 14, 9200.00, 'CARD', 'SUCCESS', 'TXN_CRD_20260925_014', '2026-09-25 19:02:15'),
(15, 15, 3800.00, 'UPI', 'SUCCESS', 'TXN_UPI_20260926_015', '2026-09-26 11:21:55'),
(16, 16, 3900.00, 'WALLET', 'SUCCESS', 'TXN_WAL_20260927_016', '2026-09-27 14:06:30'),
(17, 17, 3400.00, 'UPI', 'SUCCESS', 'TXN_UPI_20260928_017', '2026-09-28 16:56:45'),
(60, 60, 4800.00, 'CARD', 'FAILED', 'TXN_CRD_20260929_060', '2026-09-29 21:02:00'),
(99, 1, 8500.00, 'CARD', 'FAILED', 'TXN_CRD_20260501_099', '2026-05-01 10:14:15');

-- 11. Baggage (Ticket 1 has 2 checked bags totaling 33.5 kg to test weight threshold queries)
INSERT INTO baggage (baggage_id, ticket_id, baggage_type, weight_kg, tag_number) VALUES
(1, 1, 'CABIN', 7.00, 'AI-DEL-0001'),
(2, 1, 'CHECKED', 18.50, 'AI-DEL-0002'),
(3, 1, 'CHECKED', 15.00, 'AI-DEL-0003'),
(4, 2, 'CABIN', 6.50, 'AI-DEL-0004'),
(5, 2, 'CHECKED', 22.00, 'AI-DEL-0005'),
(6, 3, 'CABIN', 7.00, 'AI-DEL-0006'),
(7, 3, 'CHECKED', 14.50, 'AI-DEL-0007'),
(8, 4, 'CABIN', 5.50, 'AI-DEL-0008'),
(9, 71, 'CABIN', 7.00, 'AI-DEL-0071'),
(10, 71, 'CHECKED', 20.00, 'AI-DEL-0072'),
(11, 12, 'CABIN', 7.00, 'AI-DEL-0012'),
(12, 12, 'CHECKED', 23.50, 'AI-DEL-0013'),
(13, 9, 'CABIN', 6.80, '6E-DEL-0009'),
(14, 9, 'CHECKED', 15.00, '6E-DEL-0010');

-- 12. Employees & Manager Hierarchy
-- Managers have manager_id set to NULL; staff report to their airline's manager
INSERT INTO employee (employee_id, airline_id, base_airport_id, manager_id, first_name, last_name, email, designation, hire_date, salary) VALUES
-- Air India (Airline 1)
(1, 1, 1, NULL, 'Rajesh', 'Sharma', 'rajesh.sharma@airindia.in', 'MANAGER', '2015-03-01', 220000.00),
(2, 1, 1, 1, 'Sameer', 'Khan', 'sameer.khan@airindia.in', 'PILOT', '2017-06-15', 310000.00),
(3, 1, 1, 1, 'Neha', 'Verma', 'neha.verma@airindia.in', 'CO_PILOT', '2019-11-20', 160000.00),
(4, 1, 2, 1, 'Ankit', 'Saxena', 'ankit.saxena@airindia.in', 'ENGINEER', '2018-08-10', 125000.00),
(5, 1, 1, 1, 'Kavita', 'Rao', 'kavita.rao@airindia.in', 'CABIN_CREW', '2020-02-01', 75000.00),

-- IndiGo (Airline 2)
(6, 2, 1, NULL, 'Priya', 'Malhotra', 'priya.m@goindigo.in', 'MANAGER', '2016-01-10', 210000.00),
(7, 2, 1, 6, 'Vikram', 'Rathore', 'vikram.r@goindigo.in', 'PILOT', '2018-04-22', 290000.00),
(8, 2, 3, 6, 'Amit', 'Trivedi', 'amit.t@goindigo.in', 'CO_PILOT', '2021-07-19', 155000.00),
(9, 2, 1, 6, 'Pooja', 'Patel', 'pooja.p@goindigo.in', 'GROUND_STAFF', '2022-09-05', 48000.00),
(10, 2, 2, 6, 'Suresh', 'Babu', 'suresh.b@goindigo.in', 'ENGINEER', '2019-12-01', 118000.00),

-- Vistara (Airline 3)
(11, 3, 1, NULL, 'Sanjay', 'Dutt', 'sanjay.dutt@airvistara.com', 'MANAGER', '2016-05-18', 230000.00),
(12, 3, 1, 11, 'Deepak', 'Kaushik', 'deepak.k@airvistara.com', 'PILOT', '2018-10-12', 320000.00),
(13, 3, 1, 11, 'Simran', 'Kaur', 'simran.k@airvistara.com', 'CABIN_CREW', '2021-03-25', 80000.00),

-- SpiceJet (Airline 4)
(14, 4, 1, NULL, 'Sunil', 'Gavaskar', 'sunil.g@spicejet.com', 'MANAGER', '2015-08-01', 195000.00),
(15, 4, 7, 14, 'Manoj', 'Tiwari', 'manoj.t@spicejet.com', 'GROUND_STAFF', '2020-06-11', 45000.00),

-- Akasa Air (Airline 5)
(16, 5, 3, NULL, 'Bhavin', 'Turakhia', 'bhavin.t@akasaair.com', 'MANAGER', '2022-04-01', 240000.00),
(17, 5, 3, 16, 'Ritesh', 'Desai', 'ritesh.d@akasaair.com', 'PILOT', '2022-07-15', 285000.00);
