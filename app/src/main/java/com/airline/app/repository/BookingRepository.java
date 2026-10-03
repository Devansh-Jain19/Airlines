package com.airline.app.repository;

import com.airline.app.model.request.BookingRequest;
import com.airline.app.model.request.PaymentRequest;
import com.airline.app.model.response.BookingResponseDto;
import com.airline.app.model.response.PaymentResponseDto;
import com.airline.app.model.response.TicketDetailDto;
import com.airline.app.network.ApiService;
import com.airline.app.network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingRepository {
    private final ApiService apiService;

    public BookingRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public BookingRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void createBooking(BookingRequest request, RepositoryCallback<BookingResponseDto> callback) {
        apiService.createBooking(request).enqueue(new Callback<BookingResponseDto>() {
            @Override
            public void onResponse(Call<BookingResponseDto> call, Response<BookingResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String msg = "Booking creation failed (code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            msg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<BookingResponseDto> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error creating booking");
            }
        });
    }

    public void processPayment(PaymentRequest request, RepositoryCallback<PaymentResponseDto> callback) {
        apiService.processPayment(request).enqueue(new Callback<PaymentResponseDto>() {
            @Override
            public void onResponse(Call<PaymentResponseDto> call, Response<PaymentResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String msg = "Payment processing failed (code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            msg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<PaymentResponseDto> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error processing payment");
            }
        });
    }

    public void getTicketDetails(Long ticketId, RepositoryCallback<TicketDetailDto> callback) {
        apiService.getTicketDetails(ticketId).enqueue(new Callback<TicketDetailDto>() {
            @Override
            public void onResponse(Call<TicketDetailDto> call, Response<TicketDetailDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch ticket details (code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<TicketDetailDto> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error fetching ticket");
            }
        });
    }

    public void getPassengerBookings(Long passengerId, RepositoryCallback<List<BookingResponseDto>> callback) {
        apiService.getPassengerBookings(passengerId).enqueue(new Callback<List<BookingResponseDto>>() {
            @Override
            public void onResponse(Call<List<BookingResponseDto>> call, Response<List<BookingResponseDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load booking history (code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<BookingResponseDto>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error loading history");
            }
        });
    }

    public void cancelBooking(Long bookingId, RepositoryCallback<Void> callback) {
        apiService.cancelBooking(bookingId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(null);
                } else {
                    callback.onError("Failed to cancel booking (code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error cancelling booking");
            }
        });
    }
}
