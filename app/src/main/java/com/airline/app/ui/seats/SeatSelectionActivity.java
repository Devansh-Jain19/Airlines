package com.airline.app.ui.seats;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.airline.app.databinding.ActivitySeatSelectionBinding;
import com.airline.app.model.response.FlightSummaryDto;
import com.airline.app.model.response.SeatDto;
import com.airline.app.network.Resource;
import com.airline.app.ui.booking.CheckoutActivity;
import com.airline.app.viewmodel.FlightViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeatSelectionActivity extends AppCompatActivity {
    public static final String EXTRA_FLIGHT_ID = "extra_flight_id";
    public static final String EXTRA_FLIGHT_SUMMARY = "extra_flight_summary";

    private ActivitySeatSelectionBinding binding;
    private FlightViewModel flightViewModel;
    private SeatGridAdapter seatAdapter;
    private FlightSummaryDto flightSummary;
    private Long flightId;
    private final List<SeatDto> selectedSeats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeatSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        flightId = getIntent().getLongExtra(EXTRA_FLIGHT_ID, 101L);
        flightSummary = (FlightSummaryDto) getIntent().getSerializableExtra(EXTRA_FLIGHT_SUMMARY);

        setupToolbar();
        setupRecyclerView();

        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);

        observeViewModel();

        loadSeats();

        binding.btnProceedToCheckout.setOnClickListener(v -> {
            if (!selectedSeats.isEmpty()) {
                Intent intent = new Intent(SeatSelectionActivity.this, CheckoutActivity.class);
                intent.putExtra(CheckoutActivity.EXTRA_FLIGHT_SUMMARY, flightSummary);
                intent.putExtra(CheckoutActivity.EXTRA_SELECTED_SEAT, selectedSeats.get(0));
                intent.putExtra(CheckoutActivity.EXTRA_TOTAL_FARE, calculateTotalFare());
                startActivity(intent);
            }
        });
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        if (flightSummary != null) {
            binding.tvFlightDetails.setText(
                flightSummary.getFlightNumber() + " (" +
                flightSummary.getDepartureAirportIata() + " ➔ " +
                flightSummary.getArrivalAirportIata() + ")"
            );
        }
    }

    private void setupRecyclerView() {
        // Section 5.2: 4 columns layout: A, B | C, D
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        binding.rvSeats.setLayoutManager(layoutManager);

        seatAdapter = new SeatGridAdapter(selected -> {
            selectedSeats.clear();
            selectedSeats.addAll(selected);
            updateSelectionUi();
        });

        binding.rvSeats.setAdapter(seatAdapter);
    }

    private void updateSelectionUi() {
        if (selectedSeats.isEmpty()) {
            binding.tvSelectedSeatInfo.setText("No seat selected");
            binding.tvTotalPrice.setText(String.format(Locale.US, "$%.2f", flightSummary != null ? flightSummary.getBasePrice() : 0.0));
            binding.btnProceedToCheckout.setEnabled(false);
        } else {
            StringBuilder seatsStr = new StringBuilder("Seat: ");
            for (int i = 0; i < selectedSeats.size(); i++) {
                if (i > 0) seatsStr.append(", ");
                seatsStr.append(selectedSeats.get(i).getSeatNumber());
            }
            binding.tvSelectedSeatInfo.setText(seatsStr.toString());
            double total = calculateTotalFare();
            binding.tvTotalPrice.setText(String.format(Locale.US, "$%.2f", total));
            binding.btnProceedToCheckout.setEnabled(true);
        }
    }

    private double calculateTotalFare() {
        double base = flightSummary != null ? flightSummary.getBasePrice() : 200.0;
        double seatAddons = 0;
        for (SeatDto s : selectedSeats) {
            if (s.getPrice() != null) {
                seatAddons += s.getPrice();
            }
        }
        return base + seatAddons;
    }

    private void loadSeats() {
        flightViewModel.loadSeatMap(flightId);
    }

    private void observeViewModel() {
        flightViewModel.getSeatMapResults().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.tvErrorMessage.setVisibility(View.GONE);
                    break;
                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    List<SeatDto> seats = resource.data;
                    if (seats == null || seats.isEmpty()) {
                        seats = generateMockSeats(flightId);
                    }
                    seatAdapter.setSeats(seats);
                    break;
                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    // Fallback to sample cabin seat map
                    List<SeatDto> sampleSeats = generateMockSeats(flightId);
                    seatAdapter.setSeats(sampleSeats);
                    break;
            }
        });
    }

    private List<SeatDto> generateMockSeats(Long fId) {
        List<SeatDto> list = new ArrayList<>();
        String[] rows = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
        String[] cols = {"A", "B", "C", "D"};

        long idCounter = 1;
        for (String row : rows) {
            for (String col : cols) {
                String seatNo = row + col;
                // Make some seats occupied for realistic layout
                boolean isOccupied = (row.equals("2") && col.equals("B")) ||
                                     (row.equals("3") && col.equals("C")) ||
                                     (row.equals("5") && col.equals("A")) ||
                                     (row.equals("7") && col.equals("D"));

                String seatClass = Integer.parseInt(row) <= 2 ? "BUSINESS" : "ECONOMY";
                double price = Integer.parseInt(row) <= 2 ? 80.0 : 35.0;

                list.add(new SeatDto(idCounter++, seatNo, seatClass, price, !isOccupied, fId));
            }
        }
        return list;
    }
}
