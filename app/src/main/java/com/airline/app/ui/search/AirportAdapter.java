package com.airline.app.ui.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airline.app.R;
import com.airline.app.model.response.AirportDto;

import java.util.ArrayList;
import java.util.List;

public class AirportAdapter extends ArrayAdapter<AirportDto> {
    private final List<AirportDto> originalList;
    private final List<AirportDto> filteredList;

    public AirportAdapter(@NonNull Context context, @NonNull List<AirportDto> airports) {
        super(context, 0, airports);
        this.originalList = new ArrayList<>(airports);
        this.filteredList = new ArrayList<>(airports);
    }

    public void updateData(List<AirportDto> newAirports) {
        this.originalList.clear();
        this.originalList.addAll(newAirports);
        this.filteredList.clear();
        this.filteredList.addAll(newAirports);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Nullable
    @Override
    public AirportDto getItem(int position) {
        if (position >= 0 && position < filteredList.size()) {
            return filteredList.get(position);
        }
        return null;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_airport, parent, false);
        }

        AirportDto airport = getItem(position);
        if (airport != null) {
            TextView tvIata = convertView.findViewById(R.id.tvAirportIata);
            TextView tvCity = convertView.findViewById(R.id.tvAirportCity);
            TextView tvName = convertView.findViewById(R.id.tvAirportName);

            tvIata.setText(airport.getIataCode());
            tvCity.setText(airport.getCity());
            tvName.setText(airport.getName());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                List<AirportDto> suggestions = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    suggestions.addAll(originalList);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (AirportDto item : originalList) {
                        if ((item.getIataCode() != null && item.getIataCode().toLowerCase().contains(query)) ||
                            (item.getCity() != null && item.getCity().toLowerCase().contains(query)) ||
                            (item.getName() != null && item.getName().toLowerCase().contains(query))) {
                            suggestions.add(item);
                        }
                    }
                }

                results.values = suggestions;
                results.count = suggestions.size();
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results != null && results.count > 0) {
                    filteredList.addAll((List<AirportDto>) results.values);
                }
                notifyDataSetChanged();
            }

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (resultValue instanceof AirportDto) {
                    return ((AirportDto) resultValue).getIataCode();
                }
                return super.convertResultToString(resultValue);
            }
        };
    }
}
