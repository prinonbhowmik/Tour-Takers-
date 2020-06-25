package com.example.tureguideversion1.Adapters;

import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Reply;
import com.example.tureguideversion1.R;

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
        holder.showMessage2.setText(reply.getMessage());

        if (reply.getSenderImage() != null) {
            if (!reply.getSenderImage().isEmpty()) {
                GlideApp.with(mContext)
                        .load(reply.getSenderImage())
                        .fitCenter()
                        .into(holder.reply_profileImage);
            } else {
                if (reply.getSenderSex().matches("male")) {
                    GlideApp.with(mContext)
                            .load(R.drawable.man)
                            .centerInside()
                            .into(holder.reply_profileImage);
                } else if (reply.getSenderSex().matches("female")) {
                    GlideApp.with(mContext)
                            .load(R.drawable.woman)
                            .centerInside()
                            .into(holder.reply_profileImage);
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

        holder.crl2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(holder.showMessage2.getText());
                Toast.makeText(mContext, "Copied to clipboard", Toast.LENGTH_SHORT).show();
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            senderName2 = itemView.findViewById(R.id.senderName2);
            showMessage2 = itemView.findViewById(R.id.showMessage2);
            reply_profileImage = itemView.findViewById(R.id.reply_profileImage);
            commentTimeTV2 = itemView.findViewById(R.id.commentTimeTV2);
            crl2 = itemView.findViewById(R.id.crl2);
        }
    }
}
