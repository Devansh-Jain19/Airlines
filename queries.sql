USE airline_db;

-- 1. Search flights from DEL to BOM on 2026-10-15 and calculate remaining seats
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

-- 2. Find seats on flight 101 that haven't been booked yet
SELECT s.seat_id, s.seat_number, s.seat_class
FROM flight f
JOIN seat s ON s.aircraft_id = f.aircraft_id
LEFT JOIN ticket t ON t.flight_id = f.flight_id AND t.seat_id = s.seat_id AND t.status <> 'CANCELLED'
WHERE f.flight_id = 101 AND t.ticket_id IS NULL;

-- 3. Passenger 12's booking history, newest flights first
SELECT b.booking_reference, b.status AS booking_status, f.flight_number,
       dep.city AS origin_city, arr.city AS dest_city, f.departure_time, t.fare
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id
JOIN flight f ON f.flight_id = t.flight_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
WHERE b.passenger_id = 12
ORDER BY f.departure_time DESC;

-- 4. Boarding pass details for ticket 71 (passenger, seat, flight, airline)
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

-- 5. Total revenue by airline from issued tickets
SELECT al.name AS airline_name, COUNT(t.ticket_id) AS tickets_sold, SUM(t.fare) AS total_revenue
FROM ticket t
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
WHERE t.status = 'ISSUED'
GROUP BY al.airline_id, al.name
ORDER BY total_revenue DESC;

-- 6. Top 5 busiest routes by number of tickets sold
SELECT dep.iata_code AS origin, arr.iata_code AS destination, COUNT(t.ticket_id) AS total_tickets
FROM ticket t
JOIN flight f ON f.flight_id = t.flight_id
JOIN airport dep ON dep.airport_id = f.departure_airport_id
JOIN airport arr ON arr.airport_id = f.arrival_airport_id
WHERE t.status <> 'CANCELLED'
GROUP BY dep.iata_code, arr.iata_code
ORDER BY total_tickets DESC LIMIT 5;

-- 7. Airlines operating more than 10 flights
SELECT al.name, COUNT(f.flight_id) AS flight_count
FROM airline al
JOIN aircraft ac ON ac.airline_id = al.airline_id
JOIN flight f ON f.aircraft_id = ac.aircraft_id
GROUP BY al.airline_id, al.name
HAVING COUNT(f.flight_id) > 10;

-- 8. Passengers who haven't made any bookings yet
SELECT p.passenger_id, p.first_name, p.last_name, p.email
FROM passenger p
LEFT JOIN booking b ON b.passenger_id = p.passenger_id
WHERE b.booking_id IS NULL;

-- 9. Scheduled flights with zero tickets booked
SELECT f.flight_id, f.flight_number, f.departure_time
FROM flight f
WHERE NOT EXISTS (
    SELECT 1 FROM ticket t WHERE t.flight_id = f.flight_id AND t.status <> 'CANCELLED'
);

-- 10. Frequent flyers (passengers with 3 or more bookings)
SELECT p.passenger_id, p.email, COUNT(b.booking_id) AS booking_count
FROM passenger p
JOIN booking b ON b.passenger_id = p.passenger_id
GROUP BY p.passenger_id, p.email
HAVING COUNT(b.booking_id) >= 3;

-- 11. Flight occupancy rate and status label
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

-- 12. Average, min, and max fare by seat class (Economy vs Business)
SELECT s.seat_class, ROUND(AVG(t.fare), 2) AS avg_fare, MIN(t.fare) AS min_fare, MAX(t.fare) AS max_fare
FROM ticket t
JOIN seat s ON s.seat_id = t.seat_id
GROUP BY s.seat_class;

-- 13. Total checked baggage weight per flight
SELECT f.flight_number, COUNT(b.baggage_id) AS total_bags, SUM(b.weight_kg) AS total_weight_kg
FROM baggage b
JOIN ticket t ON t.ticket_id = b.ticket_id
JOIN flight f ON f.flight_id = t.flight_id
GROUP BY f.flight_id, f.flight_number
ORDER BY total_weight_kg DESC;

-- 14. Passengers with over 30 kg in checked bags
SELECT p.first_name, p.last_name, t.ticket_number, SUM(b.weight_kg) AS total_checked_weight
FROM baggage b
JOIN ticket t ON t.ticket_id = b.ticket_id
JOIN booking bk ON bk.booking_id = t.booking_id
JOIN passenger p ON p.passenger_id = bk.passenger_id
WHERE b.baggage_type = 'CHECKED'
GROUP BY t.ticket_id, t.ticket_number, p.passenger_id, p.first_name, p.last_name
HAVING SUM(b.weight_kg) > 30.00;

-- 15. Payment success rate by method (Card, UPI, Net Banking, Wallet)
SELECT method,
       COUNT(*) AS total_attempts,
       SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) AS successful_payments,
       ROUND(100.0 * SUM(CASE WHEN status = 'SUCCESS' THEN 1 ELSE 0 END) / COUNT(*), 1) AS success_rate_pct
FROM payment
GROUP BY method;

-- 16. Bookings that don't have a successful payment
SELECT b.booking_id, b.booking_reference, b.status
FROM booking b
WHERE NOT EXISTS (
    SELECT 1 FROM payment p WHERE p.booking_id = b.booking_id AND p.status = 'SUCCESS'
);

-- 17. Employee count by designation for each airline
SELECT al.name AS airline_name, e.designation, COUNT(e.employee_id) AS headcount
FROM employee e
JOIN airline al ON al.airline_id = e.airline_id
GROUP BY al.name, e.designation
ORDER BY al.name, headcount DESC;

-- 18. Employees earning above their airline's average salary
SELECT e.employee_id, e.first_name, e.last_name, e.salary, al.name AS airline_name
FROM employee e
JOIN airline al ON al.airline_id = e.airline_id
WHERE e.salary > (
    SELECT AVG(e2.salary) FROM employee e2 WHERE e2.airline_id = e.airline_id
);

-- 19. Employee hierarchy (who reports to which manager)
SELECT CONCAT(e.first_name, ' ', e.last_name) AS employee_name, e.designation,
       CONCAT(m.first_name, ' ', m.last_name) AS manager_name
FROM employee e
LEFT JOIN employee m ON m.employee_id = e.manager_id;

-- 20. Highest base fare flight for each airline
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

-- 21. Monthly booking count trend
SELECT DATE_FORMAT(booking_date, '%Y-%m') AS booking_month, COUNT(*) AS total_bookings
FROM booking
GROUP BY booking_month
ORDER BY booking_month ASC;

-- 22. Passengers who have flown on 2 or more different airlines
SELECT p.passenger_id, p.email, COUNT(DISTINCT ac.airline_id) AS distinct_airlines
FROM passenger p
JOIN booking b ON b.passenger_id = p.passenger_id
JOIN ticket t ON t.booking_id = b.booking_id AND t.status = 'ISSUED'
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
GROUP BY p.passenger_id, p.email
HAVING COUNT(DISTINCT ac.airline_id) >= 2;

-- 23. Airports with no scheduled departures
SELECT a.iata_code, a.name, a.city
FROM airport a
LEFT JOIN flight f ON f.departure_airport_id = a.airport_id
WHERE f.flight_id IS NULL;

-- 24. Ticket cancellation rate per airline
SELECT al.name AS airline_name,
       COUNT(t.ticket_id) AS total_tickets,
       ROUND(100.0 * SUM(CASE WHEN t.status = 'CANCELLED' THEN 1 ELSE 0 END) / COUNT(t.ticket_id), 1) AS cancellation_pct
FROM ticket t
JOIN flight f ON f.flight_id = t.flight_id
JOIN aircraft ac ON ac.aircraft_id = f.aircraft_id
JOIN airline al ON al.airline_id = ac.airline_id
GROUP BY al.airline_id, al.name;

-- 25. Long-haul flights (longer than 3 hours)
SELECT flight_number, TIMESTAMPDIFF(MINUTE, departure_time, arrival_time) AS duration_minutes
FROM flight
WHERE TIMESTAMPDIFF(MINUTE, departure_time, arrival_time) > 180;

-- 26. Top 5 highest paying passengers
SELECT p.passenger_id, CONCAT(p.first_name, ' ', p.last_name) AS passenger_name, SUM(pay.amount) AS total_spent
FROM passenger p
JOIN booking b ON b.passenger_id = p.passenger_id
JOIN payment pay ON pay.booking_id = b.booking_id AND pay.status = 'SUCCESS'
GROUP BY p.passenger_id, p.first_name, p.last_name
ORDER BY total_spent DESC LIMIT 5;

-- 27. Bookings with multiple flight segments (e.g. round trips)
SELECT b.booking_reference, COUNT(t.ticket_id) AS ticket_count
FROM booking b
JOIN ticket t ON t.booking_id = b.booking_id
GROUP BY b.booking_id, b.booking_reference
HAVING COUNT(t.ticket_id) > 1;

-- 28. Confirm booking 55 and issue its tickets after successful payment
UPDATE booking SET status = 'CONFIRMED' WHERE booking_id = 55;
UPDATE ticket SET status = 'ISSUED' WHERE booking_id = 55;

-- 29. Cancel ticket 71 and free up the seat
UPDATE ticket SET status = 'CANCELLED', seat_id = NULL WHERE ticket_id = 71;

-- 30. Clean up failed payments older than 90 days
DELETE FROM payment WHERE status = 'FAILED' AND paid_at < NOW() - INTERVAL 90 DAY;
