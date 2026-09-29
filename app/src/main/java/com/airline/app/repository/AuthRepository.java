package com.airline.app.repository;

import com.airline.app.model.request.LoginRequest;
import com.airline.app.model.request.RegisterRequest;
import com.airline.app.model.response.AuthResponseDto;
import com.airline.app.model.response.PassengerDto;
import com.airline.app.network.ApiService;
import com.airline.app.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private final ApiService apiService;

    public AuthRepository() {
        this.apiService = RetrofitClient.getApiService();
    }

    public AuthRepository(ApiService apiService) {
        this.apiService = apiService;
    }

    public void register(RegisterRequest request, RepositoryCallback<PassengerDto> callback) {
        apiService.registerPassenger(request).enqueue(new Callback<PassengerDto>() {
            @Override
            public void onResponse(Call<PassengerDto> call, Response<PassengerDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Registration failed (code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<PassengerDto> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error during registration");
            }
        });
    }

    public void login(LoginRequest request, RepositoryCallback<AuthResponseDto> callback) {
        apiService.loginPassenger(request).enqueue(new Callback<AuthResponseDto>() {
            @Override
            public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMsg = "Invalid email or password (code: " + response.code() + ")";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception ignored) {}
                    callback.onError(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                callback.onError(t.getMessage() != null ? t.getMessage() : "Network error during login");
            }
        });
    }
}
