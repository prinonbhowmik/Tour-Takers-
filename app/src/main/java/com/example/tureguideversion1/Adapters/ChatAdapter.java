package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    private List<Profile> mProfile;
    private String imageUrl = "";
    private DatabaseReference databaseReference;
    String userid;
    FirebaseUser fUser;
    FirebaseAuth auth;

    public ChatAdapter(Context context, List<Chat> mChat, List<Profile> mProfile) {
        this.mContext = context;
        this.mChat = mChat;
        this.mProfile = mProfile;
        //this.imageUrl = imageUrl;
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ChatAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        Profile profile = mProfile.get(position);
        holder.showMessage.setText(chat.getMessage());

        holder.senderName.setText(profile.getName());
       /* if (imageUrl.equals("")) {
            holder.profileImageView.setImageResource(R.drawable.man);
        } else {
            holder.profileImageView.setImageResource(R.drawable.woman);
        }*/
        if (!profile.getImage().isEmpty()) {
            GlideApp.with(mContext)
                    .load(profile.getImage())
                    .fitCenter()
                    .into(holder.profileImageView);
        } else {
            if (profile.getSex().matches("male")) {
                GlideApp.with(mContext)
                        .load(R.drawable.man)
                        .centerInside()
                        .into(holder.profileImageView);
            } else if (profile.getSex().matches("female")) {
                GlideApp.with(mContext)
                        .load(R.drawable.woman)
                        .centerInside()
                        .into(holder.profileImageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName;
        private CircleImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        userid = auth.getUid();
        if (mChat.get(position).getSender().equals(fUser.getUid())) {
            //if(mChat.get(position).getSender().equals(userid)){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }
}
