# Comprehensive Database Architecture & Integrity Review (`database_review.md`)

**Role**: Database Engineering Lead  
**Target Engine**: MySQL 8.0+  
**Scope**: In-depth audit of schema, constraints, queries, relational integrity, and backend integration.

Per strict project instructions, **these findings are documented here without silently altering the primary `schema.sql`**, enabling the engineering team to review each trade-off, rationale, and recommended fix.

---

## Summary Matrix of Findings

| ID | Category | Severity | Item | Recommendation Status |
| :--- | :--- | :--- | :--- | :--- |
| **B-01** | Potential Bug | **Medium** | View `v_booking_total` drops bookings without tickets or with all cancelled tickets | **RECOMMENDED** |
| **B-02** | Potential Bug | **Low** | Division-by-zero risk in Query Q11 when aircraft has no seats | **OPTIONAL** |
| **C-01** | Missing Constraint | **High** | Ticket seat aircraft is not validated against flight aircraft | **RECOMMENDED** |
| **C-02** | Missing Constraint | **Medium** | Physical aircraft schedule overlapping flights | **OPTIONAL** |
| **D-01** | Data Integrity | **High** | `uk_booking_flight` restricts a booking to exactly 1 ticket per flight | **ARCHITECTURAL NOTE** |
| **D-02** | Data Integrity | **Medium** | Soft cancellation (`seat_id = NULL`) erases historical seat assignment | **OPTIONAL** |
| **D-03** | Data Integrity | **Low** | `password_hash` width `VARCHAR(100)` limits hashing algorithms | **OPTIONAL** |
| **E-01** | Query Correctness | **Medium** | Q20 tie handling for highest base price per airline | **OPTIONAL** |
| **F-01** | Performance | **Medium** | Missing composite index on `ticket(flight_id, status)` | **RECOMMENDED** |
| **G-01** | Integration | **High** | Seat booking race condition error handling (MySQL Error 1062) | **REQUIRED (Backend)** |

---

## A. Syntax, DDL & MySQL 8.0+ Compatibility

### Finding A-01: Native CHECK Constraint Enforcement
- **Observation**: The schema leverages several `CHECK` constraints (e.g., `range_km > 0`, `manufacture_year BETWEEN 1990 AND 2035`, `status IN (...)`, `arrival_time > departure_time`).
- **Engine Verification**: In MySQL 5.7 and older, `CHECK` constraints were parsed but ignored. In **MySQL 8.0.16+**, `CHECK` constraints are strictly validated and enforced on both `INSERT` and `UPDATE`.
- **Verdict**: The schema syntax is fully compliant and production-ready for MySQL 8.0+.

---

## B. Potential Bugs

### Finding B-01: View `v_booking_total` Drops Bookings with Cancelled/Zero Tickets
- **Problem**: 
  The view definition in `db.md` is:
  ```sql
  CREATE VIEW v_booking_total AS
  SELECT b.booking_id, b.booking_reference, b.status, SUM(t.fare) AS total_calculated_amount
  FROM booking b
  JOIN ticket t ON t.booking_id = b.booking_id AND t.status <> 'CANCELLED'
  GROUP BY b.booking_id, b.booking_reference, b.status;
  ```
  Because it uses an **INNER JOIN**, if a booking is created before tickets are attached, or if all tickets in a booking are `CANCELLED`, the entire booking record is omitted from this view.
- **Why It Matters**:
  A backend query querying `v_booking_total` to get the invoice amount for a cancelled booking or pending booking will return `0 rows` instead of `0.00`, causing `NullPointerException` or 404 errors in the backend API.
- **Suggested Fix (RECOMMENDED)**:
  Use a `LEFT JOIN` and `COALESCE`:
  ```sql
  CREATE OR REPLACE VIEW v_booking_total AS
  SELECT b.booking_id,
         b.booking_reference,
         b.status,
         COALESCE(SUM(t.fare), 0.00) AS total_calculated_amount
  FROM booking b
  LEFT JOIN ticket t ON t.booking_id = b.booking_id AND t.status <> 'CANCELLED'
  GROUP BY b.booking_id, b.booking_reference, b.status;
  ```

---

### Finding B-02: Potential Division-by-Zero in Query Q11
- **Problem**:
  In Query Q11:
  ```sql
  COUNT(t.ticket_id) / (SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id)
  ```
  If a new aircraft has just been added to the fleet without seats populated yet, the subquery returns `0`, causing a division-by-zero.
- **Why It Matters**:
  In MySQL, division by zero returns `NULL`, which evaluates the `CASE` statement to the `ELSE` branch (`HIGH AVAILABILITY`), which is misleading for an unconfigured aircraft.
- **Suggested Fix (OPTIONAL)**:
  Wrap denominator in `NULLIF`:
  ```sql
  COUNT(t.ticket_id) / NULLIF((SELECT COUNT(*) FROM seat s WHERE s.aircraft_id = f.aircraft_id), 0)
  ```

---

## C. Missing Constraints

### Finding C-01: Aircraft Consistency Gap between `flight`, `seat`, and `ticket`
- **Problem**:
  `flight` references `aircraft_id`. `seat` references `aircraft_id`.
  `ticket` references both `flight_id` and `seat_id`.
  However, there is **no database-level constraint** ensuring that `seat.aircraft_id == flight.aircraft_id`.
- **Why It Matters**:
  If a backend bug or rogue API call assigns `seat_id = 21` (which belongs to a Boeing 777) to a ticket on Flight 101 (which flies an Airbus A320), MySQL foreign keys will accept it because both foreign keys individually exist!
- **Suggested Fix (RECOMMENDED)**:
  1. *Database Trigger Approach* (keeps 3NF untouched):
     ```sql
     DELIMITER $$
     CREATE TRIGGER trg_ticket_seat_aircraft_check
     BEFORE INSERT ON ticket
     FOR EACH ROW
     BEGIN
         DECLARE v_flight_aircraft INT;
         DECLARE v_seat_aircraft INT;
         
         IF NEW.seat_id IS NOT NULL THEN
             SELECT aircraft_id INTO v_flight_aircraft FROM flight WHERE flight_id = NEW.flight_id;
             SELECT aircraft_id INTO v_seat_aircraft FROM seat WHERE seat_id = NEW.seat_id;
             
             IF v_flight_aircraft <> v_seat_aircraft THEN
                 SIGNAL SQLSTATE '45000'
                 SET MESSAGE_TEXT = 'Integrity Violation: Seat does not belong to the aircraft assigned to this flight.';
             END IF;
         END IF;
     END$$
     DELIMITER ;
     ```
  2. *Backend Validation Rule*:
     The backend API must validate `seat.aircraft_id = flight.aircraft_id` in its seat reservation transaction.

---

### Finding C-02: Overlapping Flight Scheduling on Same Physical Aircraft
- **Problem**:
  The schema ensures `(flight_number, departure_time)` is unique. However, an airline could inadvertently schedule the same physical aircraft (`aircraft_id = 1`) on:
  - Flight A: DEL -> BOM, Depart 10:00, Arrive 12:15
  - Flight B: DEL -> BLR, Depart 11:00, Arrive 13:45
- **Why It Matters**:
  One physical plane cannot be in two places at the same time.
- **Suggested Fix (OPTIONAL)**:
  Enforce schedule non-overlap via application scheduling validation before inserting new flight records, or implement an overlap check trigger.

---

## D. Possible Data-Integrity Problems & Architectural Insights

### Finding D-01: Single Ticket per Flight per Booking (`uk_booking_flight`)
- **Problem**:
  The `ticket` table defines:
  ```sql
  CONSTRAINT uk_booking_flight UNIQUE (booking_id, flight_id)
  ```
- **Why It Matters**:
  This constraint physically prevents booking **two seats on the same flight within the same booking ID**.
  - A family of four booking together on Flight 101 cannot have 4 tickets under a single `booking_id`.
  - Conversely, this design is intentionally tailored for:
    - Single-passenger bookings, OR
    - Round-trip / multi-segment bookings (e.g., Booking 10 has 1 ticket on Flight 101 outbound, and 1 ticket on Flight 102 return).
- **Recommendation (ARCHITECTURAL CLARIFICATION)**:
  For this college project, keep this constraint as intended by `db.md`. Backend developers must be informed: **Each booking represents 1 passenger's complete itinerary (single flight or round-trip/multi-city). Multi-passenger group bookings require separate booking references**.

---

### Finding D-02: Soft Cancellation Erases Historical Seat Number
- **Problem**:
  When a ticket is cancelled, the system executes:
  ```sql
  UPDATE ticket SET status = 'CANCELLED', seat_id = NULL WHERE ticket_id = ?;
  ```
  Setting `seat_id = NULL` allows MySQL's `UNIQUE(flight_id, seat_id)` to let another customer book the seat. However, the record of which seat the passenger originally held is lost.
- **Why It Matters**:
  Auditing or customer service cannot see what seat was cancelled from the `ticket` row alone.
- **Suggested Fix (OPTIONAL)**:
  If seat history is needed, add an audit log table `ticket_seat_history (history_id, ticket_id, seat_id, assigned_at, released_at)` or keep `seat_id` and handle uniqueness via filtered index (available in PostgreSQL/SQL Server, simulated in MySQL via generated columns).

---

### Finding D-03: `password_hash` Column Width
- **Problem**:
  `passenger.password_hash` is defined as `VARCHAR(100)`.
- **Why It Matters**:
  BCrypt hashes are 60 characters and fit comfortably. However, if the backend uses Argon2id with long salt/parameters, hash strings can exceed 100 characters.
- **Suggested Fix (OPTIONAL)**:
  Expand column width to `VARCHAR(255)` in production migrations.

---

## E. Query Analysis & Optimization

### Finding E-01: Tie Handling in Q20 (Most Expensive Flight per Airline)
- **Problem**:
  Query Q20 uses `= (SELECT MAX(f2.base_price)...)`. If an airline operates two flights that share the exact same maximum base price (e.g., both 8,500.00), both rows will appear.
- **Verdict**:
  This is standard SQL behavior and acceptable for evaluation. If strict 1-row-per-airline is needed, `ROW_NUMBER() OVER (PARTITION BY al.airline_id ORDER BY f.base_price DESC)` can be utilized.

---

## F. Performance & Index Optimization

### Finding F-01: High-Traffic Seat Counting Index Gap
- **Problem**:
  Query Q1 (Flight Search with seats left) executes:
  ```sql
  (SELECT COUNT(*) FROM ticket t WHERE t.flight_id = f.flight_id AND t.status <> 'CANCELLED')
  ```
  While `ticket` has indexes on `(flight_id, seat_id)` and `(booking_id, flight_id)`, an index covering `(flight_id, status)` will allow this count to be answered entirely from index memory (Covering Index) without touching data pages.
- **Suggested Fix (RECOMMENDED)**:
  ```sql
  CREATE INDEX idx_ticket_flight_status ON ticket (flight_id, status);
  ```

---

## G. Backend Integration & Concurrency Checklist

1. **Double Booking Guard**:
   Backend developers must catch MySQL Error Code `1062` (Duplicate entry for key `uk_flight_seat`) and translate it into a user-friendly HTTP 409 Conflict response.
2. **Transaction Isolation**:
   Set transaction isolation to `READ COMMITTED` or `REPEATABLE READ` (default in InnoDB).
3. **Atomic Operations**:
   Always execute payment verification and status updates inside `START TRANSACTION ... COMMIT`.
