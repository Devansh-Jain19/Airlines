package com.airline.app.ui.ticket;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airline.app.R;
import com.airline.app.databinding.ItemBookingHistoryBinding;
import com.airline.app.model.response.BookingResponseDto;
import com.airline.app.model.response.FlightSummaryDto;

import java.util.ArrayList;
import java.util.List;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {
    public interface BookingActionListener {
        void onViewPass(BookingResponseDto booking);
        void onCancelBooking(BookingResponseDto booking);
    }

    private final List<BookingResponseDto> bookings = new ArrayList<>();
    private final BookingActionListener listener;

    public BookingHistoryAdapter(BookingActionListener listener) {
        this.listener = listener;
    }

    public void setBookings(List<BookingResponseDto> list) {
        this.bookings.clear();
        if (list != null) {
            this.bookings.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBookingHistoryBinding binding = ItemBookingHistoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BookingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position));
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        private final ItemBookingHistoryBinding binding;

        public BookingViewHolder(ItemBookingHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(BookingResponseDto booking) {
            binding.tvBookingRef.setText("REF: " + (booking.getBookingReference() != null ? booking.getBookingReference() : "BK-" + booking.getId()));

            String status = booking.getStatus();
            binding.tvBookingStatusBadge.setText(status.toUpperCase());

            if ("CONFIRMED".equalsIgnoreCase(status)) {
                binding.tvBookingStatusBadge.setBackgroundResource(R.drawable.bg_badge_confirmed);
                binding.tvBookingStatusBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_success));
                binding.btnCancel.setVisibility(View.VISIBLE);
                binding.btnViewTicket.setVisibility(View.VISIBLE);
            } else if ("CANCELLED".equalsIgnoreCase(status)) {
                binding.tvBookingStatusBadge.setBackgroundResource(R.drawable.bg_badge_cancelled);
                binding.tvBookingStatusBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_error));
                binding.btnCancel.setVisibility(View.GONE);
                binding.btnViewTicket.setVisibility(View.GONE);
            } else {
                binding.tvBookingStatusBadge.setBackgroundResource(R.drawable.bg_badge_pending);
                binding.tvBookingStatusBadge.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.status_warning));
                binding.btnCancel.setVisibility(View.VISIBLE);
                binding.btnViewTicket.setVisibility(View.VISIBLE);
            }

            FlightSummaryDto f = booking.getFlightSummary();
            if (f != null) {
                binding.tvRouteSummary.setText(f.getDepartureAirportIata() + " ➔ " + f.getArrivalAirportIata());
                binding.tvFlightNumber.setText(f.getAirlineName() + " (" + f.getFlightNumber() + ")");
                binding.tvBookingDate.setText("Dep: " + f.getDepartureTime() + " | Seat: " + booking.getSeatNumber());
            } else {
                binding.tvRouteSummary.setText("Flight Reservation #" + booking.getId());
                binding.tvFlightNumber.setText("Seat: " + booking.getSeatNumber());
                binding.tvBookingDate.setText("Booking Date: " + (booking.getBookingTime() != null ? booking.getBookingTime() : "Recent"));
            }

            binding.btnViewTicket.setOnClickListener(v -> {
                if (listener != null) listener.onViewPass(booking);
            });

            binding.btnCancel.setOnClickListener(v -> {
                if (listener != null) listener.onCancelBooking(booking);
            });
        }
    }
}
