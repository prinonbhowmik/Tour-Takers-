package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Activities.AdminChatBox;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Inbox;
import com.example.tureguideversion1.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.ViewHolder>{
    public static final String TAG = "InboxAdapter";
    private List<Inbox> chatList;
    private Context context;

    public InboxAdapter(List<Inbox> chatList, Context context) {
        this.chatList = chatList;
        this.context = context;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.inbox_recycler_item, parent, false);
        return new InboxAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Inbox inbox = chatList.get(position);
        if (inbox.getImage().trim().length() != 0) {
            try {
                GlideApp.with(context)
                        .load(inbox.getImage())
                        .fitCenter()
                        .into(holder.chat_profileImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (inbox.getMemberSex().matches("male")) {
                GlideApp.with(context)
                        .load(getImageFromDrawable("man"))
                        .centerInside()
                        .into(holder.chat_profileImage);
            } else if (inbox.getMemberSex().matches("female")) {
                GlideApp.with(context)
                        .load(getImageFromDrawable("woman"))
                        .centerInside()
                        .into(holder.chat_profileImage);
            }
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(context, AdminChatBox.class);
                chatIntent.putExtra("chatPartnerID", inbox.getMemberID());
                chatIntent.putExtra("eventId", inbox.getEventID());
                chatIntent.putExtra("childID", inbox.getMemberID());
                context.startActivity(chatIntent);
                ((FragmentActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView chat_profileImage;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chat_profileImage = itemView.findViewById(R.id.chat_profileImage);
            layout = itemView.findViewById(R.id.layout);
        }
    }
    public int getImageFromDrawable(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }
}
