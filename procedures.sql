USE airline_db;

-- Drop procedure if it already exists
DROP PROCEDURE IF EXISTS sp_book_flight_seat;

DELIMITER $$

-- ============================================================================
-- Stored Procedure: sp_book_flight_seat
-- Purpose: Safely books a seat for a passenger on a flight with ACID concurrency
--          protection. Resolves simultaneous bookings using pessimistic locking
--          (SELECT ... FOR UPDATE) and composite UNIQUE constraint guarantees.
--
-- Concurrency Outcome:
-- - The transaction that commits or acquires the row lock first WINS the seat.
-- - The concurrent competing transaction is rejected safely with ROLLBACK.
-- ============================================================================
CREATE PROCEDURE sp_book_flight_seat(
    IN  p_passenger_id   INT,
    IN  p_flight_id      INT,
    IN  p_seat_id        INT,
    IN  p_fare           DECIMAL(10,2),
    OUT p_booking_id     INT,
    OUT p_ticket_number  VARCHAR(20),
    OUT p_status_code    VARCHAR(30),
    OUT p_status_message VARCHAR(255)
)
proc_label: BEGIN
    DECLARE v_pnr          VARCHAR(6);
    DECLARE v_seat_exists  INT DEFAULT 0;
    DECLARE v_already_taken INT DEFAULT 0;
    DECLARE v_new_booking_id INT;

    -- Error handler for database exceptions (duplicate key, deadlock, etc.)
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        SET p_booking_id     = NULL;
        SET p_ticket_number  = NULL;
        SET p_status_code    = 'CONCURRENCY_COLLISION';
        SET p_status_message = 'Failed: Seat was concurrently claimed by another passenger or constraint violated.';
    END;

    -- 1. Begin Atomic Transaction
    START TRANSACTION;

    -- 2. Verify that seat exists for this flight's aircraft and lock the seat row
    SELECT COUNT(*) INTO v_seat_exists
    FROM seat s
    JOIN flight f ON f.aircraft_id = s.aircraft_id
    WHERE f.flight_id = p_flight_id AND s.seat_id = p_seat_id
    FOR UPDATE;

    IF v_seat_exists = 0 THEN
        ROLLBACK;
        SET p_booking_id     = NULL;
        SET p_ticket_number  = NULL;
        SET p_status_code    = 'INVALID_SEAT';
        SET p_status_message = 'Seat does not belong to this flight aircraft.';
        LEAVE proc_label;
    END IF;

    -- 3. Check if seat is already booked by another active (non-cancelled) ticket
    -- Row-level lock ensures competing transactions wait here
    SELECT COUNT(*) INTO v_already_taken
    FROM ticket t
    WHERE t.flight_id = p_flight_id 
      AND t.seat_id = p_seat_id 
      AND t.status <> 'CANCELLED'
    FOR UPDATE;

    IF v_already_taken > 0 THEN
        ROLLBACK;
        SET p_booking_id     = NULL;
        SET p_ticket_number  = NULL;
        SET p_status_code    = 'SEAT_UNAVAILABLE';
        SET p_status_message = 'Seat is already booked. The other passenger got the seat.';
        LEAVE proc_label;
    END IF;

    -- 4. Generate 6-character random alphanumeric PNR
    SET v_pnr = UPPER(SUBSTRING(MD5(CONCAT(RAND(), NOW(), p_passenger_id)), 1, 6));

    -- 5. Insert Booking
    INSERT INTO booking (booking_reference, passenger_id, booking_date, status)
    VALUES (v_pnr, p_passenger_id, NOW(), 'CONFIRMED');

    SET v_new_booking_id = LAST_INSERT_ID();

    -- 6. Generate Ticket Number (TK + timestamp + random)
    SET p_ticket_number = CONCAT('TK', UNIX_TIMESTAMP(), LPAD(FLOOR(RAND() * 1000), 3, '0'));

    -- 7. Insert Ticket record
    -- The table's CONSTRAINT uk_flight_seat UNIQUE (flight_id, seat_id) acts as the final barrier
    INSERT INTO ticket (ticket_number, booking_id, flight_id, seat_id, fare, status)
    VALUES (p_ticket_number, v_new_booking_id, p_flight_id, p_seat_id, p_fare, 'ISSUED');

    -- 8. Commit reservation
    COMMIT;

    SET p_booking_id     = v_new_booking_id;
    SET p_status_code    = 'SUCCESS';
    SET p_status_message = 'Seat successfully reserved and ticket issued.';
END proc_label$$

DELIMITER ;
