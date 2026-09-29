package com.airline.app.network;

import com.airline.app.model.request.BookingRequest;
import com.airline.app.model.request.LoginRequest;
import com.airline.app.model.request.PaymentRequest;
import com.airline.app.model.request.RegisterRequest;
import com.airline.app.model.response.AirportDto;
import com.airline.app.model.response.AuthResponseDto;
import com.airline.app.model.response.BookingResponseDto;
import com.airline.app.model.response.FlightSummaryDto;
import com.airline.app.model.response.PassengerDto;
import com.airline.app.model.response.PaymentResponseDto;
import com.airline.app.model.response.SeatDto;
import com.airline.app.model.response.TicketDetailDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("api/auth/register")
    Call<PassengerDto> registerPassenger(@Body RegisterRequest request);

    @POST("api/auth/login")
    Call<AuthResponseDto> loginPassenger(@Body LoginRequest request);

    @GET("api/airports")
    Call<List<AirportDto>> getAirports();

    @GET("api/flights/search")
    Call<List<FlightSummaryDto>> searchFlights(
        @Query("from") String departureIata,
        @Query("to") String arrivalIata,
        @Query("date") String flightDate
    );

    @GET("api/flights/{id}/seats")
    Call<List<SeatDto>> getFlightSeats(@Path("id") Long flightId);

    @POST("api/bookings")
    Call<BookingResponseDto> createBooking(@Body BookingRequest request);

    @POST("api/payments")
    Call<PaymentResponseDto> processPayment(@Body PaymentRequest request);

    @GET("api/tickets/{id}")
    Call<TicketDetailDto> getTicketDetails(@Path("id") Long ticketId);

    @GET("api/passengers/{id}/bookings")
    Call<List<BookingResponseDto>> getPassengerBookings(@Path("id") Long passengerId);

    @DELETE("api/bookings/{id}")
    Call<Void> cancelBooking(@Path("id") Long bookingId);
}
