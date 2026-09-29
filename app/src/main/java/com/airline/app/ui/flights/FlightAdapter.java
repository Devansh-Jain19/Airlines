package com.airline.app.ui.flights;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airline.app.databinding.ItemFlightBinding;
import com.airline.app.model.response.FlightSummaryDto;
import com.airline.app.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.FlightViewHolder> {
    private final List<FlightSummaryDto> flights = new ArrayList<>();
    private final OnFlightClickListener listener;

    public interface OnFlightClickListener {
        void onFlightClick(FlightSummaryDto flight);
    }

    public FlightAdapter(OnFlightClickListener listener) {
        this.listener = listener;
    }

    public void setFlights(List<FlightSummaryDto> newFlights) {
        this.flights.clear();
        if (newFlights != null) {
            this.flights.addAll(newFlights);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FlightViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFlightBinding binding = ItemFlightBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FlightViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightViewHolder holder, int position) {
        holder.bind(flights.get(position));
    }

    @Override
    public int getItemCount() {
        return flights.size();
    }

    class FlightViewHolder extends RecyclerView.ViewHolder {
        private final ItemFlightBinding binding;

        public FlightViewHolder(ItemFlightBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(FlightSummaryDto flight) {
            binding.tvAirlineName.setText(flight.getAirlineName() != null ? flight.getAirlineName() : "SkyWings");
            binding.tvFlightNumber.setText(flight.getFlightNumber() != null ? flight.getFlightNumber() : "FLIGHT");

            binding.tvDepIata.setText(flight.getDepartureAirportIata());
            binding.tvDepCity.setText(flight.getDepartureCity());
            binding.tvDepTime.setText(DateUtils.formatIsoToDisplayTime(flight.getDepartureTime()));

            binding.tvArrIata.setText(flight.getArrivalAirportIata());
            binding.tvArrCity.setText(flight.getArrivalCity());
            binding.tvArrTime.setText(DateUtils.formatIsoToDisplayTime(flight.getArrivalTime()));

            binding.tvDuration.setText(flight.getDuration() != null ? flight.getDuration() : "Direct");
            binding.tvAvailableSeats.setText(String.format(Locale.US, "%d seats available", flight.getAvailableSeats()));
            binding.tvBasePrice.setText(String.format(Locale.US, "$%.2f", flight.getBasePrice()));

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onFlightClick(flight);
                }
            });
        }
    }
}
