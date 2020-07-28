package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Activities.CommentSettingsBottomSheet;
import com.example.tureguideversion1.Activities.ReplySettingsBottomSheet;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Reply;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {
    private Context mContext;
    private List<Reply> mReply;
    private int row_index = -1, pos2 = -1;


    public ReplyAdapter(Context rContext, List<Reply> mReply) {
        this.mContext = rContext;
        this.mReply = mReply;
    }

    @NonNull
    @Override
    public ReplyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.reply_item_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyAdapter.ViewHolder holder, int position) {
        Reply reply = mReply.get(position);
        holder.senderName2.setText(reply.getSenderName());

        if(reply.getImageMessage() != null){
            if(reply.getImageMessage().trim().length() != 0) {
                try {
                    holder.showMessage2.setVisibility(View.GONE);
                    GlideApp.with(mContext)
                            .load(reply.getImageMessage())
                            .fitCenter()
                            .into(holder.image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                holder.image.setVisibility(View.GONE);
                holder.showMessage2.setText(reply.getMessage());
            }
        }else {
            holder.image.setVisibility(View.GONE);
            holder.showMessage2.setText(reply.getMessage());
        }

        if (reply.getSenderImage() != null) {
            if (!reply.getSenderImage().isEmpty()) {
                try {
                    GlideApp.with(mContext)
                            .load(reply.getSenderImage())
                            .fitCenter()
                            .into(holder.reply_profileImage);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            } else {
                if (reply.getSenderSex().matches("male")) {
                    try {
                        GlideApp.with(mContext)
                                .load(R.drawable.man)
                                .centerInside()
                                .into(holder.reply_profileImage);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                } else if (reply.getSenderSex().matches("female")) {
                    try {
                        GlideApp.with(mContext)
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(holder.reply_profileImage);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
        Date past = null;
        try {
            past = format.parse(reply.getReplyTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date now = new Date();
        long seconds = TimeUnit.MILLISECONDS.toSeconds(now.getTime() - past.getTime());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - past.getTime());
        long hours = TimeUnit.MILLISECONDS.toHours(now.getTime() - past.getTime());
        long days = TimeUnit.MILLISECONDS.toDays(now.getTime() - past.getTime());
        if (seconds < 60) {
            holder.commentTimeTV2.setText("now");
        } else if (minutes < 60) {
            if (minutes == 1) {
                holder.commentTimeTV2.setText("1 minute ago");
            } else
                holder.commentTimeTV2.setText(minutes + " minutes ago");
        } else if (hours < 24) {
            if (hours == 1) {
                holder.commentTimeTV2.setText("1 hour ago");
            } else
                holder.commentTimeTV2.setText(hours + " hours ago");
        } else {
            if (days == 1) {
                holder.commentTimeTV2.setText("1 day ago");
            } else
                holder.commentTimeTV2.setText(days + " days ago");
        }

/*
        holder.crl2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row_index = holder.getAdapterPosition();
                notifyDataSetChanged();

            }
        });

        if (position == row_index) {
            holder.crl2.setSelected(true);
            if (position > pos2 || position < pos2) {
                holder.commentTimeTV2.setVisibility(View.VISIBLE);
                pos2 = position;
            } else if (position == pos2) {
                holder.commentTimeTV2.setVisibility(View.GONE);
                pos2 = -1;
            }
        } else {
            holder.crl2.setSelected(false);
            holder.commentTimeTV2.setVisibility(View.GONE);
        }*/

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppCompatActivity appCompatActivity = new AppCompatActivity();
                FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                String sender_id = reply.getSenderID();
                String R_id = reply.getID();
                String E_id = reply.getEventID();
                String message = reply.getMessage();
                String C_id = reply.getCommentID();
                String userID = FirebaseAuth.getInstance().getUid();
                if (userID.equals(sender_id)) {
                    Bundle args = new Bundle();
                    args.putString("r_id", R_id);
                    args.putString("e_id", E_id);
                    args.putString("c_id", C_id);
                    args.putString("check", "true");
                    args.putString("message", message);
                    ReplySettingsBottomSheet bottom_sheet = new ReplySettingsBottomSheet();
                    bottom_sheet.setArguments(args);
                    bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                } else {
                    Bundle args = new Bundle();
                    args.putString("r_id", R_id);
                    args.putString("e_id", E_id);
                    args.putString("check", "false");
                    args.putString("message", message);
                    CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                    bottom_sheet.setArguments(args);
                    bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mReply.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView senderName2, showMessage2, commentTimeTV2;
        private CircleImageView reply_profileImage;
        private RelativeLayout crl2;
        private ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderName2 = itemView.findViewById(R.id.senderName2);
            showMessage2 = itemView.findViewById(R.id.showMessage2);
            reply_profileImage = itemView.findViewById(R.id.reply_profileImage);
            commentTimeTV2 = itemView.findViewById(R.id.commentTimeTV2);
            crl2 = itemView.findViewById(R.id.crl2);
            image = itemView.findViewById(R.id.image);
        }
    }
}
