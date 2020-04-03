package com.example.tureguideversion1.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Model.EventJoinMemberList;
import com.example.tureguideversion1.R;

import java.util.List;

public class EventJoinMemberAdapter extends RecyclerView.Adapter<EventJoinMemberAdapter.ViewHolder> {
    private List<EventJoinMemberList> eventJoinMemberList;


    public EventJoinMemberAdapter() {
    }

    public EventJoinMemberAdapter(List<EventJoinMemberList> eventJoinMemberList) {
        this.eventJoinMemberList = eventJoinMemberList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_member_layout_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventJoinMemberList event_member = eventJoinMemberList.get(position);
        holder.member_name.setText(event_member.getMemberName());
        holder.member_phone.setText(event_member.getMemberPhone());


    }

    @Override
    public int getItemCount() {
        return eventJoinMemberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private EditText member_name, member_phone, member_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            member_name = itemView.findViewById(R.id.member_nameET);
            member_phone = itemView.findViewById(R.id.member_phoneET);
            member_image = itemView.findViewById(R.id.member_image);
        }
    }
}
