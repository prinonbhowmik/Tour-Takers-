package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tureguideversion1.Model.LocationItem;
import com.example.tureguideversion1.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteLocationAdapter extends ArrayAdapter<LocationItem> {
    private List<LocationItem> locationListFull;

    public AutoCompleteLocationAdapter(@NonNull Context context, @NonNull List<LocationItem> locationList) {
        super(context, 0, locationList);
        locationListFull = new ArrayList<>(locationList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return countryFilter;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.location_autocomplete_row, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.locationView);
        ImageView imageViewFlag = convertView.findViewById(R.id.travelIcon);

        LocationItem locationItem = getItem(position);

        if (locationItem != null) {
            textViewName.setText(locationItem.getCountryName());
            imageViewFlag.setImageResource(locationItem.getIcon());
        }

        return convertView;
    }

    private Filter countryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<LocationItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(locationListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (LocationItem item : locationListFull) {
                    if (item.getCountryName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }

            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((LocationItem) resultValue).getCountryName();
        }
    };
}
