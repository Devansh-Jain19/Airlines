USE airline_db;

-- Combines flight, airline, and airport details for easy lookup
CREATE OR REPLACE VIEW v_flight_details AS
SELECT f.flight_id,
       f.flight_number,
       al.name AS airline_name,
       dep.iata_code AS dep_iata,
       arr.iata_code AS arr_iata,
       f.departure_time,
       f.arrival_time,
       f.base_price,
       f.status
FROM flight f
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id;

-- Calculates total booking cost by summing active tickets (ignores cancelled tickets)
CREATE OR REPLACE VIEW v_booking_total AS
SELECT b.booking_id,
       b.booking_reference,
       b.status,
       SUM(t.fare) AS total_calculated_amount
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id AND t.status <> 'CANCELLED'
GROUP BY b.booking_id, b.booking_reference, b.status;
