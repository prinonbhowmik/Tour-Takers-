package com.example.tureguideversion1.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = eventList.get(position);
        holder.eTitle.setText(event.getPlace());
        holder.eDate.setText(event.getDate());
        holder.eTime.setText(event.getTime());
        holder.ePlace.setText(event.getMeetPlace());
        holder.eMembers.setText(String.valueOf(event.getJoinMemberCount()));

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eTitle, eDate, eTime, ePlace, eMembers;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eTitle = itemView.findViewById(R.id.e_title);
            eDate = itemView.findViewById(R.id.e_date);
            eTime = itemView.findViewById(R.id.e_time);
            ePlace = itemView.findViewById(R.id.e_place);
            eMembers = itemView.findViewById(R.id.e_member);

        }
    }
}
