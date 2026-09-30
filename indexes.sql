USE airline_db;

-- Speeds up flight search by origin, destination, and departure date
CREATE INDEX idx_flight_search 
ON flight (departure_airport_id, arrival_airport_id, departure_time);

-- Quick lookup for a passenger's booking history
CREATE INDEX idx_booking_passenger 
ON booking (passenger_id);

-- Speeds up payment verification when checking out or reconciling
CREATE INDEX idx_payment_booking 
ON payment (booking_id);

-- Helps retrieve luggage details quickly using the ticket
CREATE INDEX idx_baggage_ticket 
ON baggage (ticket_id);
