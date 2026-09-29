package com.airline.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airline.app.model.request.LoginRequest;
import com.airline.app.model.request.RegisterRequest;
import com.airline.app.model.response.AuthResponseDto;
import com.airline.app.model.response.PassengerDto;
import com.airline.app.network.Resource;
import com.airline.app.repository.AuthRepository;
import com.airline.app.repository.RepositoryCallback;

public class AuthViewModel extends ViewModel {
    private final AuthRepository repository;
    private final MutableLiveData<Resource<AuthResponseDto>> loginResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<PassengerDto>> registerResult = new MutableLiveData<>();

    public AuthViewModel() {
        this.repository = new AuthRepository();
    }

    public AuthViewModel(AuthRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<AuthResponseDto>> getLoginResult() {
        return loginResult;
    }

    public LiveData<Resource<PassengerDto>> getRegisterResult() {
        return registerResult;
    }

    public void login(String email, String password) {
        loginResult.setValue(Resource.loading());
        repository.login(new LoginRequest(email, password), new RepositoryCallback<AuthResponseDto>() {
            @Override
            public void onSuccess(AuthResponseDto data) {
                loginResult.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                loginResult.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void register(RegisterRequest request) {
        registerResult.setValue(Resource.loading());
        repository.register(request, new RepositoryCallback<PassengerDto>() {
            @Override
            public void onSuccess(PassengerDto data) {
                registerResult.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                registerResult.postValue(Resource.error(errorMessage));
            }
        });
    }
}
