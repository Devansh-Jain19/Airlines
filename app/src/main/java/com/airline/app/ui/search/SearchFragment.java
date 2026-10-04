package com.airline.app.ui.search;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.airline.app.databinding.FragmentSearchBinding;
import com.airline.app.model.response.AirportDto;
import com.airline.app.network.Resource;
import com.airline.app.ui.auth.LoginActivity;
import com.airline.app.ui.flights.FlightListActivity;
import com.airline.app.ui.profile.ProfileActivity;
import com.airline.app.util.DateUtils;
import com.airline.app.util.SessionManager;
import com.airline.app.viewmodel.FlightViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private FlightViewModel flightViewModel;
    private AirportAdapter originAdapter;
    private AirportAdapter destinationAdapter;
    private Calendar selectedDateCalendar;
    private String selectedSearchDate;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());
        flightViewModel = new ViewModelProvider(this).get(FlightViewModel.class);

        // Update user greeting and profile avatar logo
        updateProfileHeader();

        // Clicking the user greeting opens Profile (if logged in) or Login (if logged out)
        binding.llHeaderUser.setOnClickListener(v -> handleProfileClick());

        // Clicking the profile logo button
        binding.layoutProfileButton.setOnClickListener(v -> handleProfileClick());

        // Initialize default search date (Today or Tomorrow)
        selectedDateCalendar = Calendar.getInstance();
        selectedDateCalendar.add(Calendar.DAY_OF_YEAR, 1); // Default to tomorrow
        updateDateDisplay();

        // Setup airport autocomplete adapters with initial airport options
        List<AirportDto> initialAirports = getDefaultAirportList();
        originAdapter = new AirportAdapter(requireContext(), initialAirports);
        destinationAdapter = new AirportAdapter(requireContext(), initialAirports);

        binding.actvOrigin.setAdapter(originAdapter);
        binding.actvDestination.setAdapter(destinationAdapter);

        // Set default demo inputs
        binding.actvOrigin.setText("JFK", false);
        binding.actvDestination.setText("LAX", false);

        // Setup DatePicker Dialog
        binding.llDatePicker.setOnClickListener(v -> showDatePicker());

        // Setup Swap Button
        binding.btnSwapAirports.setOnClickListener(v -> {
            String origin = binding.actvOrigin.getText().toString();
            String dest = binding.actvDestination.getText().toString();
            binding.actvOrigin.setText(dest, false);
            binding.actvDestination.setText(origin, false);
        });

        // Setup Quick Destinations
        binding.cardJfkLax.setOnClickListener(v -> {
            binding.actvOrigin.setText("JFK", false);
            binding.actvDestination.setText("LAX", false);
        });

        binding.cardLhrDxb.setOnClickListener(v -> {
            binding.actvOrigin.setText("LHR", false);
            binding.actvDestination.setText("DXB", false);
        });

        // Search Button click listener
        binding.btnSearchFlights.setOnClickListener(v -> performFlightSearch());

        // Observe Airports from Backend API
        flightViewModel.getAirportResults().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                originAdapter.updateData(resource.data);
                destinationAdapter.updateData(resource.data);
            }
        });

        // Fetch airports from network
        flightViewModel.loadAirports();
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            requireContext(),
            (view, year, month, dayOfMonth) -> {
                selectedDateCalendar.set(Calendar.YEAR, year);
                selectedDateCalendar.set(Calendar.MONTH, month);
                selectedDateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateDisplay();
            },
            selectedDateCalendar.get(Calendar.YEAR),
            selectedDateCalendar.get(Calendar.MONTH),
            selectedDateCalendar.get(Calendar.DAY_OF_MONTH)
        );

        // Date cannot be in the past
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateDisplay() {
        selectedSearchDate = DateUtils.formatDateForSearch(selectedDateCalendar);
        binding.tvSelectedDate.setText(DateUtils.formatDateForDisplay(selectedDateCalendar));
    }

    private void performFlightSearch() {
        String origin = binding.actvOrigin.getText().toString().trim().toUpperCase();
        String destination = binding.actvDestination.getText().toString().trim().toUpperCase();

        if (origin.isEmpty()) {
            binding.tilOrigin.setError("Please enter origin airport");
            return;
        } else {
            binding.tilOrigin.setError(null);
        }

        if (destination.isEmpty()) {
            binding.tilDestination.setError("Please enter destination airport");
            return;
        } else {
            binding.tilDestination.setError(null);
        }

        // Validation Rule: Origin and Destination cannot be identical
        if (origin.equalsIgnoreCase(destination)) {
            Toast.makeText(requireContext(), "Origin and Destination airports cannot be identical", Toast.LENGTH_LONG).show();
            return;
        }

        // Validation Rule: Date cannot be in the past
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar checkDate = (Calendar) selectedDateCalendar.clone();
        checkDate.set(Calendar.HOUR_OF_DAY, 0);
        checkDate.set(Calendar.MINUTE, 0);
        checkDate.set(Calendar.SECOND, 0);
        checkDate.set(Calendar.MILLISECOND, 0);

        if (checkDate.before(today)) {
            Toast.makeText(requireContext(), "Flight departure date cannot be in the past", Toast.LENGTH_LONG).show();
            return;
        }

        // Navigate to FlightListActivity
        Intent intent = new Intent(requireContext(), FlightListActivity.class);
        intent.putExtra(FlightListActivity.EXTRA_ORIGIN, origin);
        intent.putExtra(FlightListActivity.EXTRA_DESTINATION, destination);
        intent.putExtra(FlightListActivity.EXTRA_DATE, selectedSearchDate);
        startActivity(intent);
    }

    private List<AirportDto> getDefaultAirportList() {
        List<AirportDto> list = new ArrayList<>();
        list.add(new AirportDto(1L, "JFK", "John F. Kennedy Intl", "New York", "USA"));
        list.add(new AirportDto(2L, "LAX", "Los Angeles Intl", "Los Angeles", "USA"));
        list.add(new AirportDto(3L, "LHR", "Heathrow Airport", "London", "UK"));
        list.add(new AirportDto(4L, "DXB", "Dubai Intl Airport", "Dubai", "UAE"));
        list.add(new AirportDto(5L, "ORD", "O'Hare Intl Airport", "Chicago", "USA"));
        list.add(new AirportDto(6L, "SFO", "San Francisco Intl", "San Francisco", "USA"));
        list.add(new AirportDto(7L, "DEL", "Indira Gandhi Intl", "New Delhi", "India"));
        list.add(new AirportDto(8L, "BOM", "Chhatrapati Shivaji Maharaj", "Mumbai", "India"));
        list.add(new AirportDto(9L, "SIN", "Singapore Changi Airport", "Singapore", "Singapore"));
        list.add(new AirportDto(10L, "HND", "Haneda Airport", "Tokyo", "Japan"));
        return list;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateProfileHeader();
    }

    private void handleProfileClick() {
        if (sessionManager != null && sessionManager.isLoggedIn()) {
            startActivity(new Intent(requireContext(), ProfileActivity.class));
        } else {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        }
    }

    private void updateProfileHeader() {
        if (binding == null) return;

        if (sessionManager != null && sessionManager.isLoggedIn()) {
            String name = sessionManager.getName();
            binding.tvWelcomeUser.setText("Hello, " + (name != null && !name.isEmpty() ? name : "Traveler") + " 👋");

            // Show avatar initials in the profile logo button
            String initials = computeInitials(name);
            binding.tvProfileInitials.setText(initials);
            binding.tvProfileInitials.setVisibility(View.VISIBLE);
            binding.ivProfileIcon.setVisibility(View.GONE);
        } else {
            binding.tvWelcomeUser.setText("Hello, Guest 👋");
            binding.tvProfileInitials.setVisibility(View.GONE);
            binding.ivProfileIcon.setVisibility(View.VISIBLE);
        }
    }

    private String computeInitials(String name) {
        if (name == null || name.trim().isEmpty()) return "US";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) {
            return parts[0].substring(0, Math.min(2, parts[0].length())).toUpperCase();
        } else if (parts.length >= 2) {
            String first = parts[0].substring(0, 1).toUpperCase();
            String second = parts[parts.length - 1].substring(0, 1).toUpperCase();
            return first + second;
        }
        return "US";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
