package com.airline.app.repository;

import com.airline.app.model.response.AirportDto;
import com.airline.app.model.response.FlightSummaryDto;
import com.airline.app.model.response.SeatDto;
import com.airline.app.network.ApiService;
import com.airline.app.network.RetrofitClient;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FlightRepository {
    private final ApiService apiService;

    public FlightRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public FlightRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void getAirports(RepositoryCallback<List<AirportDto>> callback) {
        apiService.getAirports().enqueue(new Callback<List<AirportDto>>() {
            @Override
            public void onResponse(Call<List<AirportDto>> call, Response<List<AirportDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to fetch airports (code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<AirportDto>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error fetching airports");
            }
        });
    }

    public void searchFlights(String from, String to, String date, RepositoryCallback<List<FlightSummaryDto>> callback) {
        apiService.searchFlights(from, to, date).enqueue(new Callback<List<FlightSummaryDto>>() {
            @Override
            public void onResponse(Call<List<FlightSummaryDto>> call, Response<List<FlightSummaryDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String msg = "Failed to load flights (code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            msg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(msg);
                }
            }

            @Override
            public void onFailure(Call<List<FlightSummaryDto>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error during flight search");
            }
        });
    }

    public void loadSeats(Long flightId, RepositoryCallback<List<SeatDto>>() {
        apiService.getFlightSeats(flightId).enqueue(new Callback<List<SeatDto>>() {
            @Override
            public void onResponse(Call<List<SeatDto>> call, Response<List<SeatDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load seat map (code: " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(Call<List<SeatDto>> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error loading seats");
            }
        });
    }
}
