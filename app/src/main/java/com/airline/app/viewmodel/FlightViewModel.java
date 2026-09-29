package com.airline.app.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.airline.app.model.response.AirportDto;
import com.airline.app.model.response.FlightSummaryDto;
import com.airline.app.model.response.SeatDto;
import com.airline.app.network.Resource;
import com.airline.app.repository.FlightRepository;
import com.airline.app.repository.RepositoryCallback;

import java.util.List;

public class FlightViewModel extends ViewModel {
    private final FlightRepository repository;
    private final MutableLiveData<Resource<List<AirportDto>>> airportResults = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<FlightSummaryDto>>> flightSearchResults = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<SeatDto>>> seatMapResults = new MutableLiveData<>();

    public FlightViewModel() {
        this.repository = new FlightRepository();
    }

    public FlightViewModel(FlightRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<AirportDto>>> getAirportResults() {
        return airportResults;
    }

    public LiveData<Resource<List<FlightSummaryDto>>> getFlightSearchResults() {
        return flightSearchResults;
    }

    public LiveData<Resource<List<SeatDto>>> getSeatMapResults() {
        return seatMapResults;
    }

    public void loadAirports() {
        airportResults.setValue(Resource.loading());
        repository.getAirports(new RepositoryCallback<List<AirportDto>>() {
            @Override
            public void onSuccess(List<AirportDto> data) {
                airportResults.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                airportResults.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void searchFlights(String from, String to, String date) {
        flightSearchResults.setValue(Resource.loading());
        repository.searchFlights(from, to, date, new RepositoryCallback<List<FlightSummaryDto>>() {
            @Override
            public void onSuccess(List<FlightSummaryDto> data) {
                flightSearchResults.postValue(Resource.success(data));
            }

            @Override
            public void onError(String errorMessage) {
                flightSearchResults.postValue(Resource.error(errorMessage));
            }
        });
    }

    public void loadSeatMap(Long flightId) {
        seatMapResults.setValue(Resource.loading());
        repository.loadSeats(flightId, new RepositoryCallback<List<SeatDto>>() {
            @Override
            public void onSuccess(List<SeatDto> seats) {
                seatMapResults.postValue(Resource.success(seats));
            }

            @Override
            public void onError(String error) {
                seatMapResults.postValue(Resource.error(error));
            }
        });
    }
}
