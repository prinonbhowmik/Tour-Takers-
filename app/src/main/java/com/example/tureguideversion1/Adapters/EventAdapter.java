package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Activities.EventDetails;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private Context context;
    private String member_id, member_name, member_image, member_phone;
    private DatabaseReference databaseReference;

    public EventAdapter() {
    }

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Event event = eventList.get(position);
        holder.eTitle.setText(event.getPlace());
        holder.eDate.setText(event.getDate());
        holder.eTime.setText(event.getTime());
        holder.ePlace.setText(event.getMeetPlace());
        holder.eMembers.setText(String.valueOf(event.getJoinMemberCount()));
        holder.eCost.setText(event.getCost());
        holder.eGroupName.setText(event.getGroupName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetails.class);
                intent.putExtra("event_place", event.getPlace());
                intent.putExtra("event_date", event.getDate());
                intent.putExtra("event_time", event.getTime());
                intent.putExtra("event_description", event.getDescription());
                intent.putExtra("event_publish_date", event.getPublishDate());
                intent.putExtra("event_join_member_count", String.valueOf(event.getJoinMemberCount()));
                intent.putExtra("event_meeting_place", event.getMeetPlace());
                intent.putExtra("group_name", event.getGroupName());
                intent.putExtra("cost", event.getCost());
                intent.putExtra("event_id", event.getId());
                intent.putExtra("member_id", event.getEventPublisherId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eTitle, eDate, eTime, ePlace, eMembers, eGroupName, eCost, eMemberid;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eTitle = itemView.findViewById(R.id.e_title);
            eDate = itemView.findViewById(R.id.e_date);
            eTime = itemView.findViewById(R.id.e_time);
            ePlace = itemView.findViewById(R.id.e_place);
            eMembers = itemView.findViewById(R.id.e_member);
            eMemberid = itemView.findViewById(R.id.member_id);
            eGroupName = itemView.findViewById(R.id.e_group);
            eCost = itemView.findViewById(R.id.e_cost);

        }
    }
}
