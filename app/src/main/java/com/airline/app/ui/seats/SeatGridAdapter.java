package com.airline.app.ui.seats;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airline.app.R;
import com.airline.app.databinding.ItemSeatBinding;
import com.airline.app.model.response.SeatDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class SeatGridAdapter extends RecyclerView.Adapter<SeatGridAdapter.SeatViewHolder> {
    private final List<SeatDto> seatList = new ArrayList<>();
    private final Set<Long> selectedSeatIds = new HashSet<>();
    private final OnSeatSelectionChangeListener selectionListener;
    private int maxSelectableSeats = 1;

    public interface OnSeatSelectionChangeListener {
        void onSeatSelectionChanged(List<SeatDto> selectedSeats);
    }

    public SeatGridAdapter(OnSeatSelectionChangeListener listener) {
        this.selectionListener = listener;
    }

    public void setSeats(List<SeatDto> seats) {
        this.seatList.clear();
        this.selectedSeatIds.clear();
        if (seats != null) {
            this.seatList.addAll(seats);
        }
        notifyDataSetChanged();
    }

    public void setMaxSelectableSeats(int max) {
        this.maxSelectableSeats = max;
    }

    public List<SeatDto> getSelectedSeats() {
        List<SeatDto> selected = new ArrayList<>();
        for (SeatDto seat : seatList) {
            if (seat != null && selectedSeatIds.contains(seat.getId())) {
                selected.add(seat);
            }
        }
        return selected;
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSeatBinding binding = ItemSeatBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SeatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        holder.bind(seatList.get(position));
    }

    @Override
    public int getItemCount() {
        return seatList.size();
    }

    class SeatViewHolder extends RecyclerView.ViewHolder {
        private final ItemSeatBinding binding;

        public SeatViewHolder(ItemSeatBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(SeatDto seat) {
            if (seat == null) {
                // Dummy aisle slot
                binding.llSeatContainer.setVisibility(View.GONE);
                binding.tvAisleLabel.setVisibility(View.VISIBLE);
                return;
            }

            binding.tvAisleLabel.setVisibility(View.GONE);
            binding.llSeatContainer.setVisibility(View.VISIBLE);

            binding.tvSeatNumber.setText(seat.getSeatNumber());
            binding.tvSeatPrice.setText(String.format(Locale.US, "$%.0f", seat.getPrice()));

            boolean isSelected = selectedSeatIds.contains(seat.getId());
            boolean isAvailable = seat.isAvailable();

            if (!isAvailable) {
                // Section 5.2: Grey Box: Occupied Seat (available == false, non-clickable)
                binding.llSeatContainer.setBackgroundResource(R.drawable.bg_seat_occupied);
                binding.tvSeatNumber.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.seat_occupied_text));
                binding.ivSeatIcon.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.seat_occupied_text));
                binding.tvSeatPrice.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.seat_occupied_text));
                binding.llSeatContainer.setClickable(false);
                binding.llSeatContainer.setOnClickListener(null);
            } else if (isSelected) {
                // Section 5.2: Blue Accent Box: Currently Selected Seat by User
                binding.llSeatContainer.setBackgroundResource(R.drawable.bg_seat_selected);
                binding.tvSeatNumber.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.seat_selected_text));
                binding.ivSeatIcon.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.seat_selected_text));
                binding.tvSeatPrice.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.seat_selected_text));
                binding.llSeatContainer.setClickable(true);
                binding.llSeatContainer.setOnClickListener(v -> toggleSelection(seat));
            } else {
                // Section 5.2: White Box: Available Seat (available == true)
                binding.llSeatContainer.setBackgroundResource(R.drawable.bg_seat_available);
                binding.tvSeatNumber.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.text_primary));
                binding.ivSeatIcon.setColorFilter(ContextCompat.getColor(binding.getRoot().getContext(), R.color.text_secondary));
                binding.tvSeatPrice.setTextColor(ContextCompat.getColor(binding.getRoot().getContext(), R.color.text_muted));
                binding.llSeatContainer.setClickable(true);
                binding.llSeatContainer.setOnClickListener(v -> toggleSelection(seat));
            }
        }

        private void toggleSelection(SeatDto seat) {
            if (selectedSeatIds.contains(seat.getId())) {
                selectedSeatIds.remove(seat.getId());
            } else {
                if (maxSelectableSeats == 1) {
                    selectedSeatIds.clear();
                }
                if (selectedSeatIds.size() < maxSelectableSeats) {
                    selectedSeatIds.add(seat.getId());
                }
            }
            notifyDataSetChanged();
            if (selectionListener != null) {
                selectionListener.onSeatSelectionChanged(getSelectedSeats());
            }
        }
    }
}
