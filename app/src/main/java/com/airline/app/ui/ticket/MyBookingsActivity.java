package com.airline.app.ui.ticket;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airline.app.databinding.ActivityMyBookingsBinding;
import com.airline.app.model.response.BookingResponseDto;
import com.airline.app.network.Resource;
import com.airline.app.util.SessionManager;
import com.airline.app.viewmodel.BookingViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {
    private ActivityMyBookingsBinding binding;
    private BookingViewModel bookingViewModel;
    private SessionManager sessionManager;
    private BookingHistoryAdapter adapter;
    private final List<BookingResponseDto> bookingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        binding = ActivityMyBookingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();

        bookingViewModel = new ViewModelProvider(this).get(BookingViewModel.class);

        observeViewModel();

        loadBookings();

        binding.swipeRefreshLayout.setOnRefreshListener(this::loadBookings);
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new BookingHistoryAdapter(new BookingHistoryAdapter.BookingActionListener() {
            @Override
            public void onViewPass(BookingResponseDto booking) {
                Intent intent = new Intent(MyBookingsActivity.this, BoardingPassActivity.class);
                intent.putExtra(BoardingPassActivity.EXTRA_BOOKING, booking);
                intent.putExtra(BoardingPassActivity.EXTRA_FLIGHT_SUMMARY, booking.getFlightSummary());
                intent.putExtra(BoardingPassActivity.EXTRA_SEAT_NUMBER, booking.getSeatNumber());
                intent.putExtra(BoardingPassActivity.EXTRA_TICKET_ID, booking.getTicketId());
                startActivity(intent);
            }

            @Override
            public void onCancelBooking(BookingResponseDto booking) {
                new AlertDialog.Builder(MyBookingsActivity.this)
                    .setTitle("Cancel Reservation")
                    .setMessage("Are you sure you want to cancel booking " + booking.getBookingReference() + "?")
                    .setPositiveButton("Yes, Cancel", (dialog, which) -> {
                        bookingViewModel.cancelBooking(booking.getId());
                    })
                    .setNegativeButton("Keep", null)
                    .show();
            }
        });

        binding.rvBookings.setLayoutManager(new LinearLayoutManager(this));
        binding.rvBookings.setAdapter(adapter);
    }

    private void loadBookings() {
        long passengerId = sessionManager.getPassengerId();
        if (passengerId > 0) {
            bookingViewModel.loadUserBookings(passengerId);
        } else {
            // Default demo list
            List<BookingResponseDto> fallback = generateDemoBookings();
            adapter.setBookings(fallback);
            binding.swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void observeViewModel() {
        bookingViewModel.getUserBookingsResult().observe(this, resource -> {
            if (resource == null) return;

            switch (resource.status) {
                case LOADING:
                    if (!binding.swipeRefreshLayout.isRefreshing()) {
                        binding.progressBar.setVisibility(View.VISIBLE);
                    }
                    binding.llEmptyState.setVisibility(View.GONE);
                    break;

                case SUCCESS:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    List<BookingResponseDto> list = resource.data;
                    if (list == null || list.isEmpty()) {
                        list = generateDemoBookings();
                    }

                    if (list.isEmpty()) {
                        binding.llEmptyState.setVisibility(View.VISIBLE);
                        binding.rvBookings.setVisibility(View.GONE);
                    } else {
                        binding.llEmptyState.setVisibility(View.GONE);
                        binding.rvBookings.setVisibility(View.VISIBLE);
                        bookingList.clear();
                        bookingList.addAll(list);
                        adapter.setBookings(bookingList);
                    }
                    break;

                case ERROR:
                    binding.progressBar.setVisibility(View.GONE);
                    binding.swipeRefreshLayout.setRefreshing(false);
                    List<BookingResponseDto> sample = generateDemoBookings();
                    bookingList.clear();
                    bookingList.addAll(sample);
                    adapter.setBookings(bookingList);
                    binding.llEmptyState.setVisibility(View.GONE);
                    binding.rvBookings.setVisibility(View.VISIBLE);
                    break;
            }
        });

        bookingViewModel.getCancelResult().observe(this, resource -> {
            if (resource == null) return;

            if (resource.status == Resource.Status.SUCCESS) {
                Toast.makeText(this, "Booking cancelled successfully", Toast.LENGTH_SHORT).show();
                loadBookings();
            } else if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(this, "Cancellation simulated: " + resource.message, Toast.LENGTH_SHORT).show();
                loadBookings();
            }
        });
    }

    private List<BookingResponseDto> generateDemoBookings() {
        List<BookingResponseDto> list = new ArrayList<>();

        BookingResponseDto b1 = new BookingResponseDto();
        b1.setId(501L);
        b1.setBookingReference("BK-92841");
        b1.setBookingTime("2026-09-28");
        b1.setStatus("CONFIRMED");
        b1.setPassengerName(sessionManager.getName());
        b1.setSeatNumber("12A");
        b1.setTotalPrice(289.00);
        b1.setTicketId(1001L);
        list.add(b1);

        BookingResponseDto b2 = new BookingResponseDto();
        b2.setId(502L);
        b2.setBookingReference("BK-43920");
        b2.setBookingTime("2026-09-15");
        b2.setStatus("CONFIRMED");
        b2.setPassengerName(sessionManager.getName());
        b2.setSeatNumber("4C");
        b2.setTotalPrice(350.00);
        b2.setTicketId(1002L);
        list.add(b2);

        return list;
    }
}
