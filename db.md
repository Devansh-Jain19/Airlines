# Database Architecture, Schema, Queries & Normalization (`db.md`)

## 1. Relational Database Overview & ER Schema

This database is designed in **Third Normal Form (3NF)** for MySQL 8.0+. It features **12 interconnected relational tables** with strict primary keys, foreign keys, surrogate IDs, composite uniqueness, and CHECK constraints.

```
┌─────────────┐       ┌─────────────┐       ┌─────────────────┐
│   airline   │◄──────┤  aircraft   ├──────►│  aircraft_model │
└──────┬──────┘       └──────┬──────┘       └─────────────────┘
       │                     │
       │                     ▼
       │              ┌─────────────┐
       │              │    seat     │
       │              └──────┬──────┘
       │                     │
       ▼                     ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────────┐
│  employee   │       │   flight    │◄──────┤     airport     │
└─────────────┘       └──────┬──────┘       └─────────────────┘
                             │
                             ▼
┌─────────────┐       ┌─────────────┐       ┌─────────────────┐
│  passenger  ├──────►│   booking   ├──────►│     payment     │
└─────────────┘       └──────┬──────┘       └─────────────────┘
                             │
                             ▼
                      ┌─────────────┐       ┌─────────────────┐
                      │   ticket    ├──────►│     baggage     │
                      └─────────────┘       └─────────────────┘
```

---

## 2. Complete MySQL DDL Schema (`schema.sql`)

```sql
CREATE DATABASE IF NOT EXISTS airline_db;
USE airline_db;

-- 1. Airline Table
CREATE TABLE airline (
    airline_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    iata_code CHAR(2) NOT NULL UNIQUE,
    country VARCHAR(60) NOT NULL
);

-- 2. Airport Table
CREATE TABLE airport (
    airport_id INT AUTO_INCREMENT PRIMARY KEY,
    iata_code CHAR(3) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    city VARCHAR(60) NOT NULL,
    country VARCHAR(60) NOT NULL
);

-- 3. Aircraft Model Table
CREATE TABLE aircraft_model (
    model_id INT AUTO_INCREMENT PRIMARY KEY,
    manufacturer VARCHAR(50) NOT NULL,
    model_name VARCHAR(50) NOT NULL,
    range_km INT NOT NULL CHECK (range_km > 0),
    CONSTRAINT uk_manufacturer_model UNIQUE (manufacturer, model_name)
);

-- 4. Aircraft Table
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
);

-- 5. Seat Table
CREATE TABLE seat (
    seat_id INT AUTO_INCREMENT PRIMARY KEY,
    aircraft_id INT NOT NULL,
    seat_number VARCHAR(4) NOT NULL,
    seat_class VARCHAR(10) NOT NULL,
    CONSTRAINT fk_seat_aircraft FOREIGN KEY (aircraft_id) REFERENCES aircraft(aircraft_id) ON DELETE RESTRICT,
    CONSTRAINT uk_aircraft_seat UNIQUE (aircraft_id, seat_number),
    CONSTRAINT chk_seat_class CHECK (seat_class IN ('ECONOMY', 'BUSINESS'))
);

-- 6. Flight Table
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
);

-- 7. Passenger Table
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
);

-- 8. Booking Table
CREATE TABLE booking (
    booking_id INT AUTO_INCREMENT PRIMARY KEY,
    booking_reference CHAR(6) NOT NULL UNIQUE,
    passenger_id INT NOT NULL,
    booking_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_booking_passenger FOREIGN KEY (passenger_id) REFERENCES passenger(passenger_id) ON DELETE RESTRICT,
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED'))
);

-- 9. Ticket Table (Junction between Booking, Flight, and Seat)
CREATE TABLE ticket (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_number VARCHAR(14) NOT NULL UNIQUE,
    booking_id INT NOT NULL,
    flight_id INT NOT NULL,
    seat_id INT NULL, -- Set to NULL when ticket is CANCELLED to free up seat
    fare DECIMAL(10,2) NOT NULL CHECK (fare > 0),
    status VARCHAR(10) NOT NULL DEFAULT 'PENDING',
    CONSTRAINT fk_ticket_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_flight FOREIGN KEY (flight_id) REFERENCES flight(flight_id) ON DELETE RESTRICT,
    CONSTRAINT fk_ticket_seat FOREIGN KEY (seat_id) REFERENCES seat(seat_id) ON DELETE RESTRICT,
    CONSTRAINT uk_flight_seat UNIQUE (flight_id, seat_id),
    CONSTRAINT uk_booking_flight UNIQUE (booking_id, flight_id),
    CONSTRAINT chk_ticket_status CHECK (status IN ('PENDING', 'ISSUED', 'CANCELLED')),
    CONSTRAINT chk_seat_required_unless_cancelled CHECK (status = 'CANCELLED' OR seat_id IS NOT NULL)
);

-- 10. Payment Table
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
);

-- 11. Baggage Table
CREATE TABLE baggage (
    baggage_id INT AUTO_INCREMENT PRIMARY KEY,
    ticket_id INT NOT NULL,
    baggage_type VARCHAR(8) NOT NULL,
    weight_kg DECIMAL(5,2) NOT NULL CHECK (weight_kg > 0 AND weight_kg <= 50),
    tag_number VARCHAR(12) NOT NULL UNIQUE,
    CONSTRAINT fk_baggage_ticket FOREIGN KEY (ticket_id) REFERENCES ticket(ticket_id) ON DELETE RESTRICT,
    CONSTRAINT chk_baggage_type CHECK (baggage_type IN ('CABIN', 'CHECKED'))
);

-- 12. Employee Table
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
);
```

---

## 3. Database Normalization (Step-by-Step Proof)

### Unnormalized Form (UNF / `BOOKING_MASTER`)
```
BOOKING_MASTER(booking_id, passenger_name, passenger_email, passenger_phone, 
               flight_no1, flight_no2, airline_name, aircraft_model, manufacturer, 
               dep_airport_city, arr_airport_city, seat_no, fare, payment_method, 
               payment_amount, baggage_weights)
```
* **Anomalies**:
  * **Repeating Groups**: `flight_no1`/`flight_no2` and `baggage_weights` ("15,20").
  * **Update Anomaly**: Updating a passenger phone number requires updating dozens of rows.
  * **Deletion Anomaly**: Deleting the last booking on a flight deletes the airline and aircraft model data.

### 1st Normal Form (1NF)
* Eliminate repeating groups by ensuring all values are atomic.
* Each ticket row represents one flight segment; each baggage row represents one bag.
* Every table has a Primary Key.

### 2nd Normal Form (2NF)
* Eliminate partial functional dependencies.
* Move passenger details to `passenger`, flight details to `flight`, airline details to `airline`.
* Attributes depend on the **entire** Primary Key of their respective entity.

### 3rd Normal Form (3NF)
* Eliminate transitive dependencies (`X → Y` and `Y → Z`).
* **AircraftModel Factorization**: `aircraft_id` → `model_id` → `manufacturer`. Extracted `aircraft_model` table so manufacturer isn't duplicated on every plane.
* **Flight Airline Removal**: `flight_id` → `aircraft_id` → `airline_id`. Removed `airline_id` from `flight` since it is transitively determined through `aircraft`.
* **Booking Amount Removal**: Removed `total_amount` from `booking` since it is derivable from `SUM(ticket.fare)`. Provided via database view `v_booking_total`.

---

## 4. 30 Comprehensive DBMS Evaluation SQL Queries

```sql
-- Q1: Search flights from DEL to BOM on a specific date with remaining seat count
SELECT f.flight_id, f.flight_number, al.name AS airline,
       dep.iata_code AS dep_code, arr.iata_code AS arr_code,
       f.departure_time, f.arrival_time, f.base_price,
       (SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id) -
       (SELECT COUNT(*) FROM ticket t WHERE t.flight_id = f.flight_id AND t.status <> 'CANCELLED') AS seats_left
FROM flight f
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
WHERE dep.iata_code = 'DEL' AND arr.iata_code = 'BOM'
  AND f.departure_time >= '2026-10-15 00:00:00' AND f.departure_time < '2026-10-16 00:00:00'
  AND f.status = 'SCHEDULED';

-- Q2: List all available seats for flight_id = 101 (LEFT JOIN anti-join)
SELECT s.seat_id, s.seat_number, s.seat_class
FROM flight f
JOIN seat s ON s.aircraft_id = f.aircraft_id
LEFT JOIN ticket t ON t.flight_id = f.flight_id AND t.seat_id = s.seat_id AND t.status <> 'CANCELLED'
WHERE f.flight_id = 101 AND t.ticket_id IS NULL;

-- Q3: Retrieve booking history for passenger_id = 12 with route details
SELECT b.booking_reference, b.status AS booking_status, f.flight_number,
       dep.city AS origin_city, arr.city AS dest_city, f.departure_time, t.fare
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id
JOIN flight f ON f.flight_id = t.flight_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
WHERE b.passenger_id = 12
ORDER BY f.departure_time DESC;

-- Q4: Generate boarding ticket information (7-table JOIN)
SELECT t.ticket_number, CONCAT(p.first_name, ' ', p.last_name) AS passenger_name,
       al.name AS airline_name, f.flight_number, s.seat_number, s.seat_class, t.fare
FROM ticket t
JOIN booking b ON b.booking_id = t.booking_id
JOIN passenger p ON p.passenger_id = b.passenger_id
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
LEFT JOIN seat s ON s.seat_id = t.seat_id
WHERE t.ticket_id = 71;

-- Q5: Total revenue per airline from issued tickets
SELECT al.name AS airline_name, COUNT(t.ticket_id) AS tickets_sold, SUM(t.fare) AS total_revenue
FROM ticket t
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
WHERE t.status = 'ISSUED'
GROUP BY al.airline_id, al.name
ORDER BY total_revenue DESC;

-- Q6: Top 5 busiest flight routes by ticket volume
SELECT dep.iata_code AS origin, arr.iata_code AS destination, COUNT(t.ticket_id) AS total_tickets
FROM ticket t
JOIN flight f ON f.flight_id = t.flight_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
WHERE t.status <> 'CANCELLED'
GROUP BY dep.iata_code, arr.iata_code
ORDER BY total_tickets DESC LIMIT 5;

-- Q7: Find airlines operating more than 10 flights (HAVING clause)
SELECT al.name, COUNT(f.flight_id) AS flight_count
FROM airline al
JOIN aircraft ac ON ac.airline_id = al.airline_id
JOIN flight f ON f.aircraft_id = ac.aircraft_id
GROUP BY al.airline_id, al.name
HAVING COUNT(f.flight_id) > 10;

-- Q8: Identify passengers who have never made a booking (LEFT JOIN IS NULL)
SELECT p.passenger_id, p.first_name, p.last_name, p.email
FROM passenger p
LEFT JOIN booking b ON b.passenger_id = p.passenger_id
WHERE b.booking_id IS NULL;

-- Q9: Find flights with zero tickets booked (NOT EXISTS subquery)
SELECT f.flight_id, f.flight_number, f.departure_time
FROM flight f
WHERE NOT EXISTS (
    SELECT 1 FROM ticket t WHERE t.flight_id = f.flight_id AND t.status <> 'CANCELLED'
);

-- Q10: Frequent flyer passengers (3 or more bookings)
SELECT p.passenger_id, p.email, COUNT(b.booking_id) AS booking_count
FROM passenger p
JOIN booking b ON b.passenger_id = p.passenger_id
GROUP BY p.passenger_id, p.email
HAVING COUNT(b.booking_id) >= 3;

-- Q11: Occupancy percentage & status label per flight (CASE Statement)
SELECT f.flight_id, f.flight_number,
       COUNT(t.ticket_id) AS booked_seats,
       (SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id) AS total_capacity,
       CASE 
           WHEN COUNT(t.ticket_id) / (SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id) >= 0.9 THEN 'ALMOST FULL'
           WHEN COUNT(t.ticket_id) / (SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id) >= 0.5 THEN 'FILLING'
           ELSE 'HIGH AVAILABILITY'
       END AS occupancy_status
FROM flight f
LEFT JOIN ticket t ON t.flight_id = f.flight_id AND t.status <> 'CANCELLED'
GROUP BY f.flight_id, f.flight_number, f.aircraft_id;

-- Q12: Average, minimum, and maximum fare by seat class
SELECT s.seat_class, ROUND(AVG(t.fare), 2) AS avg_fare, MIN(t.fare) AS min_fare, MAX(t.fare) AS max_fare
FROM ticket t
JOIN seat s ON s.seat_id = t.seat_id
GROUP BY s.seat_class;

-- Q13: Total checked baggage weight per flight
SELECT f.flight_number, COUNT(b.baggage_id) AS total_bags, SUM(b.weight_kg) AS total_weight_kg
FROM baggage b
JOIN ticket t ON t.ticket_id = b.ticket_id
JOIN flight f ON f.flight_id = t.flight_id
GROUP BY f.flight_id, f.flight_number
ORDER BY total_weight_kg DESC;

-- Q14: Passengers exceeding 30 kg total baggage weight
SELECT p.first_name, p.last_name, t.ticket_number, SUM(b.weight_kg) AS total_checked_weight
FROM baggage b
JOIN ticket t ON t.ticket_id = b.ticket_id
JOIN booking bk ON bk.booking_id = t.booking_id
JOIN passenger p ON p.passenger_id = bk.passenger_id
WHERE b.baggage_type = 'CHECKED'
GROUP BY t.ticket_id, t.ticket_number, p.passenger_id, p.first_name, p.last_name
HAVING SUM(b.weight_kg) > 30.00;

-- Q15: Payment gateway success percentage breakdown by payment method
SELECT method,
       COUNT(*) AS total_attempts,
       SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS successful_payments,
       ROUND(100.0 * SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) / COUNT(*), 1) AS success_rate_pct
FROM payment
GROUP BY method;

-- Q16: Bookings with no successful payment record
SELECT b.booking_id, b.booking_reference, b.status
FROM booking b
WHERE NOT EXISTS (
    SELECT 1 FROM payment p WHERE p.booking_id = b.booking_id AND p.status = 'SUCCESS'
);

-- Q17: Employee headcount by designation per airline
SELECT al.name AS airline_name, e.designation, COUNT(e.employee_id) AS headcount
FROM employee e
JOIN airline al ON al.airline_id = e.airline_id
GROUP BY al.name, e.designation
ORDER BY al.name, headcount DESC;

-- Q18: Employees earning above their airline's average salary (Correlated Subquery)
SELECT e.employee_id, e.first_name, e.last_name, e.salary, al.name AS airline_name
FROM employee e
JOIN airline al ON al.airline_id = e.airline_id
WHERE e.salary > (
    SELECT AVG(e2.salary) FROM employee e2 WHERE e2.airline_id = e.airline_id
);

-- Q19: Manager-Employee organizational hierarchy (Self JOIN)
SELECT CONCAT(e.first_name, ' ', e.last_name) AS employee_name, e.designation,
       CONCAT(m.first_name, ' ', m.last_name) AS manager_name
FROM employee e
LEFT JOIN employee m ON m.employee_id = e.manager_id;

-- Q20: Most expensive flight base price per airline
SELECT al.name AS airline_name, f.flight_number, f.base_price
FROM flight f
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
WHERE f.base_price = (
    SELECT MAX(f2.base_price)
    FROM flight f2
    JOIN aircraft ac2 ON ac2.aircraft_id = f2.aircraft_id
    WHERE ac2.airline_id = al.airline_id
);

-- Q21: Monthly booking volume trends
SELECT DATE_FORMAT(booking_date, '%Y-%m') AS booking_month, COUNT(*) AS total_bookings
FROM booking
GROUP BY booking_month
ORDER BY booking_month ASC;

-- Q22: Passengers who have flown on 2 or more distinct airlines
SELECT p.passenger_id, p.email, COUNT(DISTINCT ac.airline_id) AS distinct_airlines
FROM passenger p
JOIN booking b ON b.passenger_id = p.passenger_id
JOIN ticket t ON t.booking_id = b.booking_id AND t.status = 'ISSUED'
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
GROUP BY p.passenger_id, p.email
HAVING COUNT(DISTINCT ac.airline_id) >= 2;

-- Q23: Airports with no departing flights scheduled
SELECT a.iata_code, a.name, a.city
FROM airport a
LEFT JOIN flight f ON f.departure_airport_id = a.airport_id
WHERE f.flight_id IS NULL;

-- Q24: Cancellation percentage per airline
SELECT al.name AS airline_name,
       COUNT(t.ticket_id) AS total_tickets,
       ROUND(100.0 * SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) / COUNT(t.ticket_id), 1) AS cancellation_pct
FROM ticket t
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
GROUP BY al.airline_id, al.name;

-- Q25: Long-haul flights (duration exceeding 3 hours)
SELECT flight_number, TIMESTAMPDIFF(MINUTE, departure_time, arrival_time) AS duration_minutes
FROM flight
WHERE TIMESTAMPDIFF(MINUTE, departure_time, arrival_time) > 180;

-- Q26: Top 5 revenue-generating passengers
SELECT p.passenger_id, CONCAT(p.first_name, ' ', p.last_name) AS passenger_name, SUM(pay.amount) AS total_spent
FROM passenger p
JOIN booking b ON b.passenger_id = p.passenger_id
JOIN payment pay ON pay.booking_id = b.booking_id AND pay.status = 'SUCCESS'
GROUP BY p.passenger_id, p.first_name, p.last_name
ORDER BY total_spent DESC LIMIT 5;

-- Q27: Round-trip bookings containing multiple tickets
SELECT b.booking_reference, COUNT(t.ticket_id) AS ticket_count
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id
GROUP BY b.booking_id, b.booking_reference
HAVING COUNT(t.ticket_id) > 1;

-- Q28: UPDATE Statement — Confirm booking & issue tickets on successful payment
UPDATE booking SET status = 'CONFIRMED' WHERE booking_id = 55;
UPDATE ticket SET status = 'ISSUED' WHERE booking_id = 55;

-- Q29: UPDATE Statement — Cancel ticket and free seat (Soft Cancellation)
UPDATE ticket SET status = 'CANCELLED', seat_id = NULL WHERE ticket_id = 71;

-- Q30: DELETE Statement — Purge failed payment attempts older than 90 days
DELETE FROM payment WHERE status = 'FAILED' AND paid_at < NOW() - INTERVAL 90 DAY;
```

---

## 5. Views and Performance Indexes

### Database Views
```sql
-- View 1: Flight Details Summary View
CREATE VIEW v_flight_details AS
SELECT f.flight_id, f.flight_number, al.name AS airline_name,
       dep.iata_code AS dep_iata, arr.iata_code AS arr_iata,
       f.departure_time, f.arrival_time, f.base_price, f.status
FROM flight f
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id;

-- View 2: Booking Total Amount View (Replaces unnormalized total_amount column)
CREATE VIEW v_booking_total AS
SELECT b.booking_id, b.booking_reference, b.status, SUM(t.fare) AS total_calculated_amount
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id AND t.status <> 'CANCELLED'
GROUP BY b.booking_id, b.booking_reference, b.status;
```

### Strategic Indexes for Query Optimization
```sql
-- Composite index for rapid flight search (Q1)
CREATE INDEX idx_flight_search ON flight (departure_airport_id, arrival_airport_id, departure_time);

-- Index for passenger history lookups (Q3)
CREATE INDEX idx_booking_passenger ON booking (passenger_id);

-- Index for payment verification lookups
CREATE INDEX idx_payment_booking ON payment (booking_id);

-- Index for baggage tracking by ticket
CREATE INDEX idx_baggage_ticket ON baggage (ticket_id);
```

---

## 6. DBMS Viva Questions & Bulletproof Answers

* **Q1: Why did you remove `airline_id` from the `flight` table?**
  * *Answer*: `Flight` already references `aircraft_id`. Each `Aircraft` is owned by an `Airline` (`aircraft.airline_id`). Storing `airline_id` in `flight` would create a transitive dependency (`flight_id` → `aircraft_id` → `airline_id`), violating 3NF and introducing data inconsistency risks.

* **Q2: How do you prevent double-booking a seat on the same flight?**
  * *Answer*: We enforce a composite unique key constraint `UNIQUE(flight_id, seat_id)` on the `ticket` table in MySQL. Even if concurrent application threads pass the application-level seat availability check, the database engine will reject the second `INSERT` with a duplicate entry error.

* **Q3: What happens to a seat when a booking is cancelled?**
  * *Answer*: We perform a soft cancellation: `UPDATE ticket SET status = 'CANCELLED', seat_id = NULL WHERE ticket_id = ?`. Setting `seat_id` to `NULL` releases the seat for new bookings because MySQL's `UNIQUE` constraint permits multiple `NULL` entries while preserving the historical ticket record.
