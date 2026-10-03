package com.airline.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airline.app.model.request.BookingRequest;
import com.airline.app.model.request.PaymentRequest;
import com.airline.app.model.response.BookingResponseDto;
import com.airline.app.model.response.PaymentResponseDto;
import com.airline.app.model.response.TicketDetailDto;
import com.airline.app.network.Resource;
import com.airline.app.repository.BookingRepository;
import com.airline.app.repository.RepositoryCallback;

import java.util.List;

public class BookingViewModel extends ViewModel {
    private final BookingRepository repository;
    private final MutableLiveData<Resource<BookingResponseDto>> bookingResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<PaymentResponseDto>> paymentResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<TicketDetailDto>> ticketResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<BookingResponseDto>>> userBookingsResult = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> cancelResult = new MutableLiveData<>();

    public BookingViewModel() {
        this.repository = new BookingRepository();
    }

    public BookingViewModel(BookingRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<BookingResponseDto>> getBookingResult() {
        return bookingResult;
    }

    public LiveData<Resource<PaymentResponseDto>> getPaymentResult() {
        return paymentResult;
    }

    public LiveData<Resource<TicketDetailDto>> getTicketResult() {
        return ticketResult;
    }

    public LiveData<Resource<List<BookingResponseDto>>> getUserBookingsResult() {
        return userBookingsResult;
    }

    public LiveData<Resource<Void>> getCancelResult() {
        return cancelResult;
    }

    public void createBooking(BookingRequest request) {
        bookingResult.setValue(Resource.loading());
        repository.createBooking(request, new RepositoryCallback<BookingResponseDto>() {
            @Override
            public void onSuccess(BookingResponseDto data) {
                bookingResult.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                bookingResult.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void processPayment(PaymentRequest request) {
        paymentResult.setValue(Resource.loading());
        repository.processPayment(request, new RepositoryCallback<PaymentResponseDto>() {
            @Override
            public void onSuccess(PaymentResponseDto data) {
                paymentResult.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                paymentResult.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadTicket(Long ticketId) {
        ticketResult.setValue(Resource.loading());
        repository.getTicketDetails(ticketId, new RepositoryCallback<TicketDetailDto>() {
            @Override
            public void onSuccess(TicketDetailDto data) {
                ticketResult.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                ticketResult.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadUserBookings(Long passengerId) {
        userBookingsResult.setValue(Resource.loading());
        repository.getPassengerBookings(passengerId, new RepositoryCallback<List<BookingResponseDto>>() {
            @Override
            public void onSuccess(List<BookingResponseDto> data) {
                userBookingsResult.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                userBookingsResult.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void cancelBooking(Long bookingId) {
        cancelResult.setValue(Resource.loading());
        repository.cancelBooking(bookingId, new RepositoryCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                cancelResult.postValue(Resource.success(null));
            }

            @Override
            public void onError(String errorMessage) {
                cancelResult.postValue(Resource.error(errorMessage));
            }
        });
    }
}
