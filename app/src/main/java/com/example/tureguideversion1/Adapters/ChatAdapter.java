package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Activities.ReplyBox;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    private int row_index, pos, pos2 = -1;

    private int timeVisibility = 0;

    public ChatAdapter(Context context, List<Chat> mChat) {
        this.mContext = context;
        this.mChat = mChat;
//        this.mProfile = mProfile;
        //this.imageUrl = imageUrl;
        setHasStableIds(true);
    }

    @NonNull

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Chat chat = mChat.get(position);
        holder.showMessage.setText(chat.getMessage());

        holder.senderName.setText(chat.getSenderName());

       if(chat.getSenderImage() != null){
        if (!chat.getSenderImage().isEmpty()) {
            GlideApp.with(mContext)
                    .load(chat.getSenderImage())
                    .fitCenter()
                    .into(holder.profileImageView);
        } else {
            if (chat.getSenderSex().matches("male")) {
                GlideApp.with(mContext)
                        .load(R.drawable.man)
                        .centerInside()
                        .into(holder.profileImageView);
            } else if (chat.getSenderSex().matches("female")) {
                GlideApp.with(mContext)
                        .load(R.drawable.woman)
                        .centerInside()
                        .into(holder.profileImageView);
            }
        }
       }

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
        Date past = null;
        try {
            past = format.parse(chat.getCommentTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
        if (seconds < 60) {
            holder.commentTimeTV.setText("now");
        } else if (minutes < 60) {
            if (minutes == 1) {
                holder.commentTimeTV.setText("1 minute ago");
            } else
                holder.commentTimeTV.setText(minutes + " minutes ago");
        } else if (hours < 24) {
            if (hours == 1) {
                holder.commentTimeTV.setText("1 hour ago");
            } else
                holder.commentTimeTV.setText(hours + " hours ago");
        } else {
            if (days == 1) {
                holder.commentTimeTV.setText("1 day ago");
            } else
                holder.commentTimeTV.setText(days + " days ago");
        }

        holder.crl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = holder.getAdapterPosition();
                notifyDataSetChanged();

            }
        });

        if (position == row_index) {
            holder.crl.setSelected(true);
            if (position > pos2 || position < pos2) {
                holder.commentTimeTV.setVisibility(View.VISIBLE);
                pos2 = position;
            } else if (position == pos2) {
                holder.commentTimeTV.setVisibility(View.GONE);
                pos2 = -1;
            }
        } else {
            holder.crl.setSelected(false);
            holder.commentTimeTV.setVisibility(View.GONE);
        }
        holder.crl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                Intent intent = new Intent(mContext, ReplyBox.class);
                //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                mContext.startActivity(intent);
                return true;

            }
        });
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName, commentTimeTV;
        private CircleImageView profileImageView;
        private RelativeLayout crl;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            crl = itemView.findViewById(R.id.crl);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
/*
    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        auth = FirebaseAuth.getInstance();
        userid = auth.getUid();
        if (mChat.get(position).getSenderId().equals(fUser.getUid())) {
            //if(mChat.get(position).getSender().equals(userid)){
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
        return position;
    }
    */
}
