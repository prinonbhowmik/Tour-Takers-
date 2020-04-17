package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Model.EventLocationList;
import com.example.tureguideversion1.R;

import java.util.List;

public class EventLocationListAdapter extends RecyclerView.Adapter<EventLocationListAdapter.ViewHolder> {

    private List<EventLocationList> locationList;
    private Context context;

    public EventLocationListAdapter(List<EventLocationList> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventLocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_location_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventLocationListAdapter.ViewHolder holder, int position) {
        EventLocationList locations = locationList.get(position);
        holder.location.setText(locations.getLocation());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView location, status;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.eventLocation);
            status = itemView.findViewById(R.id.status);
        }
    }
}
