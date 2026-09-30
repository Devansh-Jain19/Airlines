CREATE DATABASE IF NOT EXISTS airline_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE airline_db;

-- 1. Airlines
CREATE TABLE airline (
    airline_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    iata_code CHAR(2) NOT NULL UNIQUE,
    country VARCHAR(60) NOT NULL
) ENGINE=InnoDB;

-- 2. Airports
CREATE TABLE airport (
    airport_id INT AUTO_INCREMENT PRIMARY KEY,
    iata_code CHAR(3) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    city VARCHAR(60) NOT NULL,
    country VARCHAR(60) NOT NULL
) ENGINE=InnoDB;

-- 3. Aircraft Models (normalized specs: range, manufacturer)
CREATE TABLE aircraft_model (
    model_id INT AUTO_INCREMENT PRIMARY KEY,
    manufacturer VARCHAR(50) NOT NULL,
    model_name VARCHAR(50) NOT NULL,
    range_km INT NOT NULL CHECK (range_km > 0),
    CONSTRAINT uk_manufacturer_model UNIQUE (manufacturer, model_name)
) ENGINE=InnoDB;

-- 4. Physical Aircraft fleet
CREATE TABLE aircraft (
    aircraft_id INT AUTO_INCREMENT PRIMARY KEY,
    airline_id INT NOT NULL,
    model_id INT NOT NULL,
    registration_number VARCHAR(15) NOT NULL UNIQUE,
    manufacture_year SMALLINT NOT NULL CHECK (manufacture_year BETWEEN 1990 AND 2035),
    status VARCHAR(15) NOT NULL DEFAULT 'ACTIVE',
    CONSTRAINT fk_aircraft_airline FOREIGN KEY (airline_id) REFERENCES airline(airline_id) ON DELETE RESTRICT,
    CONSTRAINT fk_aircraft_model FOREIGN KEY (model_id) REFERENCES aircraft_model(model_id) ON DELETE RESTRICT,
    CONSTRAINT chk_aircraft_status CHECK (status IN ('ACTIVE', 'MAINTENANCE', 'RETIRED'))
) ENGINE=InnoDB;

-- 5. Seat configurations per plane
CREATE TABLE seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    aircraft_id INT NOT NULL,
    seat_number VARCHAR(4) NOT NULL,
    seat_class VARCHAR(10) NOT NULL,
    CONSTRAINT fk_seat_aircraft FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id) ON DELETE RESTRICT,
    CONSTRAINT uk_aircraft_seat UNIQUE (aircraft_id, seat_number),
    CONSTRAINT chk_seat_class CHECK (seat_class IN ('ECONOMY', 'BUSINESS'))
) ENGINE=InnoDB;

-- 6. Scheduled Flights
CREATE TABLE flight (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(8) NOT NULL,
    aircraft_id INT NOT NULL,
    departure_airport_id INT NOT NULL,
    arrival_airport_id INT NOT NULL,
    departure_time DATETIME NOT NULL,
    arrival_time DATETIME NOT NULL,
    base_price DECIMAL(10,2) NOT NULL CHECK (base_price > 0),
    status VARCHAR(12) NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_flight_aircraft FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id) ON DELETE RESTRICT,
    CONSTRAINT fk_flight_dep_airport FOREIGN KEY (departure_airport_id) REFERENCES airport(airport_id) ON DELETE RESTRICT,
    CONSTRAINT fk_flight_arr_airport FOREIGN KEY (arrival_airport_id) REFERENCES airport(airport_id) ON DELETE RESTRICT,
    CONSTRAINT uk_flight_number_time UNIQUE (flight_number, departure_time),
    CONSTRAINT chk_different_airports CHECK (departure_airport_id <> arrival_airport_id),
    CONSTRAINT chk_arrival_after_departure CHECK (arrival_time > departure_time),
    CONSTRAINT chk_flight_status CHECK (status IN ('SCHEDULED', 'DELAYED', 'CANCELLED', 'DEPARTED', 'ARRIVED'))
) ENGINE=InnoDB;

-- 7. Passengers
CREATE TABLE passenger (
    passenger_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    phone VARCHAR(15) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    gender CHAR(1) CHECK (gender IN ('M', 'F', 'O')),
    nationality VARCHAR(40) NOT NULL DEFAULT 'Indian',
    passport_number VARCHAR(15) UNIQUE NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- 8. Bookings
CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_reference CHAR(6) NOT NULL UNIQUE,
    passenger_id INT NOT NULL,
    booking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_booking_passenger FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id) ON DELETE RESTRICT,
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
) ENGINE=InnoDB;

-- 9. Tickets (links booking, flight, and seat; prevents double-booking via uk_flight_seat)
CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(14) NOT NULL UNIQUE,
    booking_id INT NOT NULL,
    flight_id INT NOT NULL,
    seat_id INT NULL, -- Set to NULL on cancellation so the seat can be booked again
    fare DECIMAL(10,2) NOT NULL CHECK (fare > 0),
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_flight FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id) ON DELETE RESTRICT,
    CONSTRAINT uk_flight_seat UNIQUE (flight_id, seat_id),
    CONSTRAINT uk_booking_flight UNIQUE (booking_id, flight_id),
    CONSTRAINT chk_ticket_status CHECK (status IN ('PENDING', 'ISSUED', 'CANCELLED')),
    CONSTRAINT chk_seat_required_unless_cancelled CHECK (status = 'CANCELLED' OR seat_id IS NOT NULL)
) ENGINE=InnoDB;

-- 10. Payments
CREATE TABLE payment (
    payment_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL CHECK (amount > 0),
    method VARCHAR(15) NOT NULL,
    status VARCHAR(10) NOT NULL,
    transaction_ref VARCHAR(30) NOT NULL UNIQUE,
    paid_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE RESTRICT,
    CONSTRAINT chk_payment_method CHECK (method IN ('CARD', 'UPI', 'NET_BANKING', 'WALLET')),
    CONSTRAINT chk_payment_status CHECK (status IN ('SUCCESS', 'FAILED', 'REFUNDED'))
) ENGINE=InnoDB;

-- 11. Baggage
CREATE TABLE baggage (
    baggage_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    baggage_type VARCHAR(8) NOT NULL,
    weight_kg DECIMAL(5,2) NOT NULL CHECK (weight_kg > 0 AND weight_kg <= 50),
    tag_number VARCHAR(12) NOT NULL UNIQUE,
    CONSTRAINT fk_baggage_ticket FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id) ON DELETE RESTRICT,
    CONSTRAINT chk_baggage_type CHECK (baggage_type IN ('CABIN', 'CHECKED'))
) ENGINE=InnoDB;

-- 12. Airline Employees
CREATE TABLE employee (
    employee_id INT AUTO_INCREMENT PRIMARY KEY,
    airline_id INT NOT NULL,
    base_airport_id INT NULL,
    manager_id INT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    designation VARCHAR(20) NOT NULL,
    hire_date DATE NOT NULL,
    salary DECIMAL(10,2) NOT NULL CHECK (salary > 0),
    CONSTRAINT fk_employee_airline FOREIGN KEY (airline_id) REFERENCES airline(airline_id) ON DELETE RESTRICT,
    CONSTRAINT fk_employee_airport FOREIGN KEY (base_airport_id) REFERENCES airport(airport_id) ON DELETE RESTRICT,
    CONSTRAINT fk_employee_manager FOREIGN KEY (manager_id) REFERENCES employee(employee_id) ON DELETE RESTRICT,
    CONSTRAINT chk_employee_designation CHECK (designation IN ('PILOT', 'CO_PILOT', 'CABIN_CREW', 'GROUND_STAFF', 'ENGINEER', 'MANAGER'))
) ENGINE=InnoDB;
