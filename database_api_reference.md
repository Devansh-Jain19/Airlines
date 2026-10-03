# Airline Management System (AMS) — Database API & Integration Reference

This document serves as the authoritative integration contract between the **MySQL 8.0+ Database Layer** and the **Backend API / Android Client Application**.

---

## 1. System Architecture & Entity Reference

The database consists of **12 normalized tables (3NF)** operating on the MySQL InnoDB storage engine with strict ACID compliance.

| Table | Purpose | Primary Key | Foreign Keys / Dependencies | Key Constraints |
| :--- | :--- | :--- | :--- | :--- |
| `airline` | Registered airline carriers | `airline_id` | None | `name` UNIQUE, `iata_code` UNIQUE (2 chars) |
| `airport` | Global airport hubs | `airport_id` | None | `iata_code` UNIQUE (3 chars) |
| `aircraft_model` | Aircraft technical catalog | `model_id` | None | `(manufacturer, model_name)` UNIQUE, `range_km > 0` |
| `aircraft` | Physical fleet inventory | `aircraft_id` | `airline_id`, `model_id` | `registration_number` UNIQUE, `manufacture_year` [1990-2035] |
| `seat` | Physical seat maps per aircraft | `seat_id` | `aircraft_id` | `(aircraft_id, seat_number)` UNIQUE, `seat_class` IN ('ECONOMY', 'BUSINESS') |
| `flight` | Scheduled flight instances | `flight_id` | `aircraft_id`, `departure_airport_id`, `arrival_airport_id` | `(flight_number, departure_time)` UNIQUE, `arrival_time > departure_time` |
| `passenger` | Customer accounts & credentials | `passenger_id` | None | `email` UNIQUE, `phone` UNIQUE, `passport_number` UNIQUE (NULLable) |
| `booking` | High-level booking orders | `booking_id` | `passenger_id` | `booking_reference` UNIQUE (6 chars), status IN ('PENDING', 'CONFIRMED', 'CANCELLED') |
| `ticket` | Flight segment reservations & seat junction | `ticket_id` | `booking_id`, `flight_id`, `seat_id` | `(flight_id, seat_id)` UNIQUE, `(booking_id, flight_id)` UNIQUE |
| `payment` | Financial transaction ledger | `payment_id` | `booking_id` | `transaction_ref` UNIQUE, `amount > 0` |
| `baggage` | Check-in & cabin luggage items | `baggage_id` | `ticket_id` | `tag_number` UNIQUE, `weight_kg` BETWEEN 0.01 AND 50.00 |
| `employee` | Airline staff & hierarchy | `employee_id` | `airline_id`, `base_airport_id`, `manager_id` | `email` UNIQUE, `manager_id` self-referencing FK |

---

## 2. State Transition Models & Status Enums

### A. Booking Status (`booking.status`)
```
    [PENDING]  ──(Payment Verified)──► [CONFIRMED]
        │                                  │
        └──────(User/Timeout Cancel)───────┼──► [CANCELLED]
```
- `PENDING`: Initial state upon checkout creation. Seat is temporarily reserved by unconfirmed ticket.
- `CONFIRMED`: Financial payment succeeded (`payment.status = 'SUCCESS'`).
- `CANCELLED`: Explicit cancellation or automated payment expiry timeout.

### B. Ticket Status (`ticket.status`)
```
    [PENDING]  ──(Payment Confirmed)──► [ISSUED]
        │                                  │
        └──────(Cancel / Seat Release)─────┼──► [CANCELLED] (seat_id = NULL)
```
- `PENDING`: Ticket record holds a valid `seat_id`.
- `ISSUED`: Active passenger ticket valid for boarding and baggage check-in.
- `CANCELLED`: `seat_id` MUST be reset to `NULL` to release the physical seat.

### C. Payment Status (`payment.status`)
- `SUCCESS`: Webhook/gateway callback validated; updates booking & ticket to `CONFIRMED`/`ISSUED`.
- `FAILED`: Payment rejected by gateway; booking remains `PENDING` for retry or auto-cancellation.
- `REFUNDED`: Reversal completed upon booking cancellation.

### D. Flight Status (`flight.status`)
- `SCHEDULED` ──► `DELAYED` ──► `DEPARTED` ──► `ARRIVED` (or `CANCELLED`).

---

## 3. Core Database Flows & Concurrency Controls

### Flow 1: Flight Search & Real-Time Seat Availability
The frontend Android app queries flights based on departure airport, destination airport, and date:
```sql
SELECT f.flight_id,
       f.flight_number,
       al.name AS airline_name,
       al.iata_code AS airline_code,
       dep.city AS departure_city,
       dep.iata_code AS departure_code,
       arr.city AS arrival_city,
       arr.iata_code AS arrival_code,
       f.departure_time,
       f.arrival_time,
       f.base_price,
       (SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id) -
       (SELECT COUNT(*) FROM ticket t WHERE t.flight_id = f.flight_id AND t.status <> 'CANCELLED') AS seats_available
FROM flight f
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
WHERE dep.iata_code = ? 
  AND arr.iata_code = ?
  AND f.departure_time >= ? 
  AND f.departure_time < ?
  AND f.status = 'SCHEDULED'
ORDER BY f.departure_time ASC;
```

#### Fetch Available Seat Map for Selected Flight:
```sql
SELECT s.seat_id,
       s.seat_number,
       s.seat_class,
       CASE WHEN t.ticket_id IS NOT NULL THEN FALSE ELSE TRUE END AS is_available
FROM seat s
JOIN flight f ON f.aircraft_id = s.aircraft_id
LEFT JOIN ticket t ON t.flight_id = f.flight_id 
                  AND t.seat_id = s.seat_id 
                  AND t.status <> 'CANCELLED'
WHERE f.flight_id = ?
ORDER BY s.seat_id ASC;
```

---

### Flow 2: The Booking & Seat Selection Transaction (Preventing Double Booking)

#### Concurrency Threat:
When two passengers simultaneously attempt to book seat `2A` on Flight `101`, application-level checks alone can fail due to race conditions.

#### Dual-Layer Protection Mechanism:
1. **Application-Level Row Lock**: Use `SELECT ... FOR UPDATE` inside an explicit InnoDB transaction to lock seat availability during checkout initiation.
2. **Database Engine Guarantee**: The `ticket` table enforces `CONSTRAINT uk_flight_seat UNIQUE (flight_id, seat_id)`. If two concurrent transactions execute simultaneously, InnoDB will immediately reject the second with:
   `Error 1062 (23000): Duplicate entry '<flight_id>-<seat_id>' for key 'uk_flight_seat'`

#### Complete Backend Transaction Sequence:

```sql
-- Step 1: Start Transaction
START TRANSACTION;

-- Step 2: Validate seat belongs to this flight's aircraft AND is not already booked
SELECT s.seat_id 
FROM seat s
JOIN flight f ON f.aircraft_id = s.aircraft_id
WHERE f.flight_id = ? AND s.seat_id = ?
  AND NOT EXISTS (
      SELECT 1 FROM ticket t 
      WHERE t.flight_id = f.flight_id 
        AND t.seat_id = s.seat_id 
        AND t.status <> 'CANCELLED'
  )
FOR UPDATE;

-- Backend Check:
-- If the SELECT above returns 0 rows:
--   ROLLBACK;
--   Return HTTP 409 Conflict ("Seat is no longer available. Please choose another seat.")

-- Step 3: Insert Booking Header (Generate 6-char random alphanumeric reference)
INSERT INTO booking (booking_reference, passenger_id, booking_date, status)
VALUES (?, ?, NOW(), 'PENDING');

SET @new_booking_id = LAST_INSERT_ID();

-- Step 4: Insert Ticket Record (Enforces uk_flight_seat)
-- Ticket number generated by backend (e.g. 'TK-XXXX-YYYY')
INSERT INTO ticket (ticket_number, booking_id, flight_id, seat_id, fare, status)
VALUES (?, @new_booking_id, ?, ?, ?, 'PENDING');

-- Step 5: Commit Reservation
COMMIT;
```

#### Backend Error Handling for Race Condition:
```typescript
try {
    await db.execute("INSERT INTO ticket (...) VALUES (...)");
} catch (error) {
    if (error.code === 'ER_DUP_ENTRY' || error.errno === 1062) {
        await db.execute("ROLLBACK");
        return res.status(409).json({
            error: "SEAT_ALREADY_TAKEN",
            message: "The selected seat has just been reserved by another customer. Please choose another seat."
        });
    }
    throw error;
}
```

---

### Flow 3: Payment Processing & Ticket Issuance

When the payment gateway responds via webhook:

```sql
START TRANSACTION;

-- Step 1: Record Payment Attempt
INSERT INTO payment (booking_id, amount, method, status, transaction_ref, paid_at)
VALUES (?, ?, ?, 'SUCCESS', ?, NOW());

-- Step 2: Transition Booking to CONFIRMED
UPDATE booking 
SET status = 'CONFIRMED' 
WHERE booking_id = ? AND status = 'PENDING';

-- Step 3: Transition Tickets to ISSUED
UPDATE ticket 
SET status = 'ISSUED' 
WHERE booking_id = ? AND status = 'PENDING';

COMMIT;
```

If the payment fails:
```sql
INSERT INTO payment (booking_id, amount, method, status, transaction_ref, paid_at)
VALUES (?, ?, ?, 'FAILED', ?, NOW());
-- Booking remains PENDING until retry or expiry cron cancels it
```

---

### Flow 4: Ticket Cancellation & Soft Seat Release

When a passenger cancels their booking or ticket:
1. To preserve financial history, audit logs, and analytics, **do not execute `DELETE FROM ticket`**.
2. Perform a **Soft Cancellation**:
   - Change `ticket.status = 'CANCELLED'`.
   - Set `ticket.seat_id = NULL`.

#### Why `seat_id = NULL` Releases the Seat:
In standard SQL and MySQL InnoDB:
- A `UNIQUE (flight_id, seat_id)` constraint allows multiple rows where `seat_id IS NULL`.
- Freeing `seat_id` to `NULL` enables another passenger to immediately book that physical seat on that flight without violating `uk_flight_seat`.
- The `CHECK (status = 'CANCELLED' OR seat_id IS NOT NULL)` constraint guarantees that only cancelled tickets can hold a `NULL` seat.

```sql
START TRANSACTION;

-- Step 1: Soft-cancel the ticket and free the seat
UPDATE ticket 
SET status = 'CANCELLED', 
    seat_id = NULL 
WHERE ticket_id = ? AND booking_id = ?;

-- Step 2: If all tickets under the booking are cancelled, update booking status
UPDATE booking b
SET b.status = 'CANCELLED'
WHERE b.booking_id = ?
  AND NOT EXISTS (
      SELECT 1 FROM ticket t 
      WHERE t.booking_id = b.booking_id AND t.status <> 'CANCELLED'
  );

-- Step 3: Record refund if applicable
INSERT INTO payment (booking_id, amount, method, status, transaction_ref, paid_at)
VALUES (?, ?, ?, 'REFUNDED', ?, NOW());

COMMIT;
```

---

## 4. End-to-End Application Operation Catalog

### A. Authentication & Passenger Profile

#### 1. Register Passenger
```sql
INSERT INTO passenger (first_name, last_name, email, password_hash, phone, date_of_birth, gender, nationality, passport_number)
VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);
```

#### 2. Passenger Login
```sql
SELECT passenger_id, first_name, last_name, email, password_hash, phone
FROM passenger
WHERE email = ?;
-- Backend compares submitted plain password with password_hash via bcrypt
```

#### 3. View Profile
```sql
SELECT passenger_id, first_name, last_name, email, phone, date_of_birth, gender, nationality, passport_number, created_at
FROM passenger
WHERE passenger_id = ?;
```

#### 4. Update Profile
```sql
UPDATE passenger
SET first_name = ?, last_name = ?, phone = ?, passport_number = ?
WHERE passenger_id = ?;
```

---

### B. Passenger Booking History & Boarding Pass Details

#### 1. Passenger Booking History List
```sql
SELECT b.booking_id,
       b.booking_reference,
       b.booking_date,
       b.status AS booking_status,
       COALESCE(vt.total_calculated_amount, 0.00) AS total_amount,
       COUNT(t.ticket_id) AS total_tickets
FROM booking b
LEFT JOIN v_booking_total vt ON vt.booking_id = b.booking_id
LEFT JOIN ticket t ON t.booking_id = b.booking_id
WHERE b.passenger_id = ?
GROUP BY b.booking_id, b.booking_reference, b.booking_date, b.status, vt.total_calculated_amount
ORDER BY b.booking_date DESC;
```

#### 2. Get Single Booking with Detailed Tickets & Routes
```sql
SELECT b.booking_id,
       b.booking_reference,
       b.status AS booking_status,
       t.ticket_id,
       t.ticket_number,
       t.status AS ticket_status,
       t.fare,
       f.flight_number,
       al.name AS airline_name,
       dep.city AS departure_city,
       dep.iata_code AS departure_code,
       arr.city AS arrival_city,
       arr.iata_code AS arrival_code,
       f.departure_time,
       f.arrival_time,
       s.seat_number,
       s.seat_class
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
LEFT JOIN seat s ON s.seat_id = t.seat_id
WHERE b.booking_id = ? AND b.passenger_id = ?;
```

#### 3. Generate Boarding Pass
```sql
SELECT t.ticket_number,
       CONCAT(p.first_name, ' ', p.last_name) AS passenger_name,
       al.name AS airline_name,
       al.iata_code AS airline_code,
       f.flight_number,
       dep.iata_code AS origin,
       dep.name AS origin_airport,
       arr.iata_code AS destination,
       arr.name AS destination_airport,
       f.departure_time,
       f.arrival_time,
       s.seat_number,
       s.seat_class,
       t.status AS ticket_status
FROM ticket t
JOIN booking b ON b.booking_id = t.booking_id
JOIN passenger p ON p.passenger_id = b.passenger_id
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
LEFT JOIN seat s ON s.seat_id = t.seat_id
WHERE t.ticket_id = ? AND t.status = 'ISSUED';
```

---

### C. Baggage Management

#### 1. Add Baggage to Ticket
```sql
INSERT INTO baggage (ticket_id, baggage_type, weight_kg, tag_number)
VALUES (?, ?, ?, ?);
```

#### 2. Get Baggage Information by Ticket ID
```sql
SELECT baggage_id, ticket_id, baggage_type, weight_kg, tag_number
FROM baggage
WHERE ticket_id = ?;
```

---

## 5. Important Backend Integration Rules & Gotchas

1. **Round-Trip vs Single Passenger**:
   Because `ticket` enforces `CONSTRAINT uk_booking_flight UNIQUE (booking_id, flight_id)`, **a single `booking_id` cannot contain more than one ticket for the same flight**. Multi-passenger bookings for the same flight must create separate booking records or group them via a higher-level group ID in the backend.
2. **Never Insert Seat from a Different Aircraft**:
   Always verify `seat.aircraft_id = flight.aircraft_id` before inserting into `ticket`.
3. **Password Security**:
   Always hash passwords using BCrypt (salt rounds >= 10) in the backend before inserting into `passenger.password_hash`.
4. **Dates & Timestamps**:
   All flight dates and booking dates are stored in MySQL `DATETIME`. Always supply ISO 8601 strings (`YYYY-MM-DD HH:MM:SS`) in the database server's local time zone or UTC.
