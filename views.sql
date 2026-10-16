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
       COALESCE(SUM(t.fare), 0.00) AS total_calculated_amount
FROM booking b
LEFT JOIN ticket t ON t.booking_id = b.booking_id AND t.status <> 'CANCELLED'
GROUP BY b.booking_id, b.booking_reference, b.status;

-- 3. Station Connectivity & Booking Availability View (One-Way & Return Trips)
CREATE OR REPLACE VIEW v_station_connectivity AS
SELECT 
    dep.airport_id AS station_id,
    dep.iata_code AS station_code,
    dep.name AS station_name,
    dep.city AS station_city,
    COUNT(DISTINCT f.arrival_airport_id) AS connected_destination_stations,
    COUNT(f.flight_id) AS total_outbound_flights,
    (
        SELECT COUNT(DISTINCT ret.departure_airport_id)
        FROM flight ret
        WHERE ret.arrival_airport_id = dep.airport_id
          AND ret.status <> 'CANCELLED'
          AND ret.departure_airport_id IN (
              SELECT f2.arrival_airport_id
              FROM flight f2
              WHERE f2.departure_airport_id = dep.airport_id
                AND f2.status <> 'CANCELLED'
          )
    ) AS return_trip_stations_available,
    (
        SELECT COUNT(*)
        FROM flight ret2
        WHERE ret2.arrival_airport_id = dep.airport_id
          AND ret2.status <> 'CANCELLED'
    ) AS total_inbound_flights
FROM airport dep
LEFT JOIN flight f ON f.departure_airport_id = dep.airport_id AND f.status <> 'CANCELLED'
GROUP BY dep.airport_id, dep.iata_code, dep.name, dep.city;

-- 4. Bidirectional Route Pairing View (One-Way and Return Trip ticket options)
CREATE OR REPLACE VIEW v_station_route_pairs AS
SELECT 
    dep.iata_code AS origin_station,
    arr.iata_code AS destination_station,
    COUNT(f.flight_id) AS outbound_flights_count,
    MIN(f.base_price) AS min_outbound_fare,
    (
        SELECT COUNT(*)
        FROM flight ret
        WHERE ret.departure_airport_id = arr.airport_id
          AND ret.arrival_airport_id = dep.airport_id
          AND ret.status <> 'CANCELLED'
    ) AS return_flights_count,
    (
        SELECT MIN(ret.base_price)
        FROM flight ret
        WHERE ret.departure_airport_id = arr.airport_id
          AND ret.arrival_airport_id = dep.airport_id
          AND ret.status <> 'CANCELLED'
    ) AS min_return_fare
FROM airport dep
JOIN flight f ON f.departure_airport_id = dep.airport_id AND f.status <> 'CANCELLED'
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
GROUP BY dep.airport_id, dep.iata_code, arr.airport_id, arr.iata_code;

