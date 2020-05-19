package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.EventJoinMemberList;
import com.example.tureguideversion1.R;

import java.util.List;

public class EventJoinMemberAdapter extends RecyclerView.Adapter<EventJoinMemberAdapter.ViewHolder> {
    private List<EventJoinMemberList> eventJoinMemberList;
    private Context context;


    public EventJoinMemberAdapter(List<EventJoinMemberList> eventJoinMemberList, Context context) {
        this.eventJoinMemberList = eventJoinMemberList;
        this.context = context;
    }

    public EventJoinMemberAdapter() {
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
        holder.member_name.setText(event_member.getName());
        holder.member_phone.setText(event_member.getEmail());
        if(!event_member.getImage().isEmpty()) {
            GlideApp.with(context)
                    .load(event_member
                            .getImage())
                    .fitCenter()
                    .into(holder.member_image);
        }else {
            if(event_member.getGender().matches("male")){
                GlideApp.with(context)
                        .load(getImageFromDrawable("man"))
                        .centerInside()
                        .into(holder.member_image);
            }else if(event_member.getGender().matches("female")){
                GlideApp.with(context)
                        .load(getImageFromDrawable("woman"))
                        .centerInside()
                        .into(holder.member_image);
            }
        }

    }

    public int getImageFromDrawable(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }

    @Override
    public int getItemCount() {
        return eventJoinMemberList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView member_name, member_phone;
        private ImageView member_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            member_name = itemView.findViewById(R.id.member_nameET);
            member_phone = itemView.findViewById(R.id.member_phoneET);
            member_image = itemView.findViewById(R.id.member_image);
        }
    }
}
