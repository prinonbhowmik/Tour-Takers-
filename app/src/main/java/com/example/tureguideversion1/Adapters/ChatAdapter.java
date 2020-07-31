package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Activities.CommentSettingsBottomSheet;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Reply;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "ChatAdapter";
    public static final int CHAT_ITEM = 0;
    public static final int CHAT_ITEM_WITH_REPLY = 1;
    public static final int CHAT_ITEM_IMAGE = 2;
    public static final int CHAT_ITEM_IMAGE_WITH_REPLY = 3;
    public static final int CHAT_ITEM_WITH_REPLY_IMAGE = 4;
    public static final int CHAT_ITEM_IMAGE_WITH_REPLY_IMAGE = 5;
    private Context mContext;
    private List<Chat> mChat;
    private List<Reply> replyList;

    public ChatAdapter(Context context, List<Chat> mChat, List<Reply> replyList) {
        this.mContext = context;
        this.mChat = mChat;
        this.replyList = replyList;
        setHasStableIds(true);
    }

    @NonNull

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == CHAT_ITEM_WITH_REPLY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_with_reply, parent, false);
            return new ViewHolderWithReply(view);
        } else if (viewType == CHAT_ITEM) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, parent, false);
            return new ViewHolder(view);
        } else if (viewType == CHAT_ITEM_IMAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_image, parent, false);
            return new ViewHolderImage(view);
        } else if (viewType == CHAT_ITEM_IMAGE_WITH_REPLY) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_image_with_reply, parent, false);
            return new ViewHolderImageWithReply(view);
        } else if (viewType == CHAT_ITEM_WITH_REPLY_IMAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_with_reply_image, parent, false);
            return new ViewHolderWithReplyImage(view);
        } else if (viewType == CHAT_ITEM_IMAGE_WITH_REPLY_IMAGE) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_image_with_reply_image, parent, false);
            return new ViewHolderImageWithReplyImage(view);
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == CHAT_ITEM_WITH_REPLY) {
            ((ViewHolderWithReply) holder).setDataWithReply(mChat.get(position));
        } else if (getItemViewType(position) == CHAT_ITEM) {
            ((ViewHolder) holder).setData(mChat.get(position));
        } else if (getItemViewType(position) == CHAT_ITEM_IMAGE) {
            ((ViewHolderImage) holder).setData(mChat.get(position));
        } else if (getItemViewType(position) == CHAT_ITEM_WITH_REPLY_IMAGE) {
            ((ViewHolderWithReplyImage) holder).setDataWithReply(mChat.get(position));
        } else if (getItemViewType(position) == CHAT_ITEM_IMAGE_WITH_REPLY) {
            ((ViewHolderImageWithReply) holder).setDataWithReply(mChat.get(position));
        } else if (getItemViewType(position) == CHAT_ITEM_IMAGE_WITH_REPLY_IMAGE) {
            ((ViewHolderImageWithReplyImage) holder).setDataWithReply(mChat.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public String getSwipePosition(int position) {
        Chat chat = mChat.get(position);
        String commentId = chat.getID();
        return commentId;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName, commentTimeTV;
        private CircleImageView profileImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
        }


        void setData(Chat chat) {
            showMessage.setText(chat.getMessage());
            senderName.setText(chat.getSenderName());

            if (chat.getSenderImage() != null) {
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView.getContext())
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(profileImageView);
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
                commentTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    commentTimeTV.setText("1 minute ago");
                } else
                    commentTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    commentTimeTV.setText("1 hour ago");
                } else
                    commentTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    commentTimeTV.setText("1 day ago");
                } else
                    commentTimeTV.setText(days + " days ago");
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppCompatActivity appCompatActivity = new AppCompatActivity();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    String sender_id = chat.getSenderID();
                    String C_id = chat.getID();
                    String E_id = chat.getEventID();
                    String message = chat.getMessage();
                    String userID = FirebaseAuth.getInstance().getUid();
                    if (userID.equals(sender_id)) {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
                        args.putString("e_id", E_id);
                        args.putString("check", "true");
                        args.putString("message", message);
                        CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                        bottom_sheet.setArguments(args);
                        bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                    } else {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
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
    }

    public class ViewHolderImage extends RecyclerView.ViewHolder {
        private TextView senderName, commentTimeTV;
        private CircleImageView profileImageView;
        private ImageView image;

        public ViewHolderImage(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            image = itemView.findViewById(R.id.image);
        }


        void setData(Chat chat) {
            try {
                GlideApp.with(itemView.getContext())
                        .load(chat.getImageMessage())
                        .fitCenter()
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            senderName.setText(chat.getSenderName());

            if (chat.getSenderImage() != null) {
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView.getContext())
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(profileImageView);
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
                commentTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    commentTimeTV.setText("1 minute ago");
                } else
                    commentTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    commentTimeTV.setText("1 hour ago");
                } else
                    commentTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    commentTimeTV.setText("1 day ago");
                } else
                    commentTimeTV.setText(days + " days ago");
            }

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppCompatActivity appCompatActivity = new AppCompatActivity();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    String sender_id = chat.getSenderID();
                    String C_id = chat.getID();
                    String E_id = chat.getEventID();
                    String message = chat.getMessage();
                    String userID = FirebaseAuth.getInstance().getUid();
                    if (userID.equals(sender_id)) {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
                        args.putString("e_id", E_id);
                        args.putString("check", "true");
                        args.putString("message", message);
                        CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                        bottom_sheet.setArguments(args);
                        bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                    } else {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
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
    }

    public class ViewHolderWithReply extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName, commentTimeTV, replyerName, replyerComment, commentCounted;
        private CircleImageView profileImageView, replyerImage;

        public ViewHolderWithReply(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            replyerName = itemView.findViewById(R.id.replyerName);
            replyerComment = itemView.findViewById(R.id.replyerComment);
            commentCounted = itemView.findViewById(R.id.commentCounted);
            replyerImage = itemView.findViewById(R.id.replyerImage);
        }

        void setDataWithReply(Chat chat) {
            showMessage.setText(chat.getMessage());
            senderName.setText(chat.getSenderName());

            if (chat.getSenderImage() != null) {
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView.getContext())
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(profileImageView);
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
                commentTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    commentTimeTV.setText("1 minute ago");
                } else
                    commentTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    commentTimeTV.setText("1 hour ago");
                } else
                    commentTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    commentTimeTV.setText("1 day ago");
                } else
                    commentTimeTV.setText(days + " days ago");
            }

            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(chat.getEventID()).child(chat.getID());
            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long c = dataSnapshot.getChildrenCount() - 1;
                        if (c > 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous replies...");
                        } else if (c == 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous reply...");
                        } else if (c == 0) {
                            commentCounted.setText("Swipe right to view reply...");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Query query = commentRef.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            if (!map.get("senderImage").toString().isEmpty()) {
                                if (replyerImage != null) {
                                    try {
                                        GlideApp.with(itemView.getContext())
                                                .load(map.get("senderImage"))
                                                .fitCenter()
                                                .into(replyerImage);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (replyerImage != null) {
                                    if (chat.getSenderSex().matches("male")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.man)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (chat.getSenderSex().matches("female")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.woman)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if ((replyerName != null) && (replyerComment != null)) {
                                replyerName.setText(map.get("senderName").toString());
                                replyerComment.setText(map.get("message").toString());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppCompatActivity appCompatActivity = new AppCompatActivity();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    String sender_id = chat.getSenderID();
                    String C_id = chat.getID();
                    String E_id = chat.getEventID();
                    String message = chat.getMessage();
                    String userID = FirebaseAuth.getInstance().getUid();
                    if (userID.equals(sender_id)) {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
                        args.putString("e_id", E_id);
                        args.putString("check", "true");
                        args.putString("message", message);
                        CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                        bottom_sheet.setArguments(args);
                        bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                    } else {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
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

    }

    public class ViewHolderImageWithReply extends RecyclerView.ViewHolder {
        private TextView senderName, commentTimeTV, replyerName, replyerComment, commentCounted;
        private CircleImageView profileImageView, replyerImage;
        private ImageView image;

        public ViewHolderImageWithReply(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            replyerName = itemView.findViewById(R.id.replyerName);
            replyerComment = itemView.findViewById(R.id.replyerComment);
            commentCounted = itemView.findViewById(R.id.commentCounted);
            replyerImage = itemView.findViewById(R.id.replyerImage);
            image = itemView.findViewById(R.id.image);
        }

        void setDataWithReply(Chat chat) {
            try {
                GlideApp.with(itemView.getContext())
                        .load(chat.getImageMessage())
                        .fitCenter()
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
            }

            senderName.setText(chat.getSenderName());

            if (chat.getSenderImage() != null) {
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView.getContext())
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(profileImageView);
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
                commentTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    commentTimeTV.setText("1 minute ago");
                } else
                    commentTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    commentTimeTV.setText("1 hour ago");
                } else
                    commentTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    commentTimeTV.setText("1 day ago");
                } else
                    commentTimeTV.setText(days + " days ago");
            }

            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(chat.getEventID()).child(chat.getID());
            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long c = dataSnapshot.getChildrenCount() - 1;
                        if (c > 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous replies...");
                        } else if (c == 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous reply...");
                        } else if (c == 0) {
                            commentCounted.setText("Swipe right to view reply...");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Query query = commentRef.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            if (!map.get("senderImage").toString().isEmpty()) {
                                if (replyerImage != null) {
                                    try {
                                        GlideApp.with(itemView.getContext())
                                                .load(map.get("senderImage"))
                                                .fitCenter()
                                                .into(replyerImage);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (replyerImage != null) {
                                    if (chat.getSenderSex().matches("male")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.man)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (chat.getSenderSex().matches("female")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.woman)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if ((replyerName != null) && (replyerComment != null)) {
                                replyerName.setText(map.get("senderName").toString());
                                replyerComment.setText(map.get("message").toString());
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppCompatActivity appCompatActivity = new AppCompatActivity();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    String sender_id = chat.getSenderID();
                    String C_id = chat.getID();
                    String E_id = chat.getEventID();
                    String message = chat.getMessage();
                    String userID = FirebaseAuth.getInstance().getUid();
                    if (userID.equals(sender_id)) {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
                        args.putString("e_id", E_id);
                        args.putString("check", "true");
                        args.putString("message", message);
                        CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                        bottom_sheet.setArguments(args);
                        bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                    } else {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
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

    }

    public class ViewHolderWithReplyImage extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName, commentTimeTV, replyerName, commentCounted;
        private CircleImageView profileImageView, replyerImage;
        private ImageView replyImage;

        public ViewHolderWithReplyImage(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            replyerName = itemView.findViewById(R.id.replyerName);
            commentCounted = itemView.findViewById(R.id.commentCounted);
            replyerImage = itemView.findViewById(R.id.replyerImage);
            replyImage = itemView.findViewById(R.id.replyImage);
        }

        void setDataWithReply(Chat chat) {
            showMessage.setText(chat.getMessage());
            senderName.setText(chat.getSenderName());

            if (chat.getSenderImage() != null) {
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView.getContext())
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(profileImageView);
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
                commentTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    commentTimeTV.setText("1 minute ago");
                } else
                    commentTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    commentTimeTV.setText("1 hour ago");
                } else
                    commentTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    commentTimeTV.setText("1 day ago");
                } else
                    commentTimeTV.setText(days + " days ago");
            }

            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(chat.getEventID()).child(chat.getID());
            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long c = dataSnapshot.getChildrenCount() - 1;
                        if (c > 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous replies...");
                        } else if (c == 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous reply...");
                        } else if (c == 0) {
                            commentCounted.setText("Swipe right to view reply...");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Query query = commentRef.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            if (!map.get("senderImage").toString().isEmpty()) {
                                if (replyerImage != null) {
                                    try {
                                        GlideApp.with(itemView.getContext())
                                                .load(map.get("senderImage"))
                                                .fitCenter()
                                                .into(replyerImage);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (replyerImage != null) {
                                    if (chat.getSenderSex().matches("male")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.man)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (chat.getSenderSex().matches("female")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.woman)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if ((replyerName != null) && (replyerImage != null)) {
                                replyerName.setText(map.get("senderName").toString());
                                try {
                                    GlideApp.with(itemView.getContext())
                                            .load(map.get("imageMessage").toString())
                                            .fitCenter()
                                            .into(replyImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppCompatActivity appCompatActivity = new AppCompatActivity();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    String sender_id = chat.getSenderID();
                    String C_id = chat.getID();
                    String E_id = chat.getEventID();
                    String message = chat.getMessage();
                    String userID = FirebaseAuth.getInstance().getUid();
                    if (userID.equals(sender_id)) {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
                        args.putString("e_id", E_id);
                        args.putString("check", "true");
                        args.putString("message", message);
                        CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                        bottom_sheet.setArguments(args);
                        bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                    } else {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
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

    }

    public class ViewHolderImageWithReplyImage extends RecyclerView.ViewHolder {
        private TextView senderName, commentTimeTV, replyerName, commentCounted;
        private CircleImageView profileImageView, replyerImage;
        private ImageView image, replyImage;

        public ViewHolderImageWithReplyImage(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            replyerName = itemView.findViewById(R.id.replyerName);
            commentCounted = itemView.findViewById(R.id.commentCounted);
            replyerImage = itemView.findViewById(R.id.replyerImage);
            image = itemView.findViewById(R.id.image);
            replyImage = itemView.findViewById(R.id.replyImage);
        }

        void setDataWithReply(Chat chat) {
            try {
                GlideApp.with(itemView.getContext())
                        .load(chat.getImageMessage())
                        .fitCenter()
                        .into(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            senderName.setText(chat.getSenderName());

            if (chat.getSenderImage() != null) {
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView.getContext())
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView.getContext())
                                .load(R.drawable.woman)
                                .centerInside()
                                .into(profileImageView);
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
                commentTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    commentTimeTV.setText("1 minute ago");
                } else
                    commentTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    commentTimeTV.setText("1 hour ago");
                } else
                    commentTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    commentTimeTV.setText("1 day ago");
                } else
                    commentTimeTV.setText(days + " days ago");
            }

            DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(chat.getEventID()).child(chat.getID());
            commentRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        long c = dataSnapshot.getChildrenCount() - 1;
                        if (c > 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous replies...");
                        } else if (c == 1) {
                            commentCounted.setText("Swipe right to view " + c + " more previous reply...");
                        } else if (c == 0) {
                            commentCounted.setText("Swipe right to view reply...");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            Query query = commentRef.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                            if (!map.get("senderImage").toString().isEmpty()) {
                                if (replyerImage != null) {
                                    try {
                                        GlideApp.with(itemView.getContext())
                                                .load(map.get("senderImage"))
                                                .fitCenter()
                                                .into(replyerImage);
                                    } catch (IllegalArgumentException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                if (replyerImage != null) {
                                    if (chat.getSenderSex().matches("male")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.man)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    } else if (chat.getSenderSex().matches("female")) {
                                        try {
                                            GlideApp.with(itemView.getContext())
                                                    .load(R.drawable.woman)
                                                    .centerInside()
                                                    .into(replyerImage);
                                        } catch (IllegalArgumentException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if ((replyerName != null) && (replyerImage != null)) {
                                replyerName.setText(map.get("senderName").toString());
                                try {
                                    GlideApp.with(itemView.getContext())
                                            .load(map.get("imageMessage").toString())
                                            .fitCenter()
                                            .into(replyImage);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AppCompatActivity appCompatActivity = new AppCompatActivity();
                    FragmentManager fragmentManager = appCompatActivity.getSupportFragmentManager();
                    String sender_id = chat.getSenderID();
                    String C_id = chat.getID();
                    String E_id = chat.getEventID();
                    String message = chat.getMessage();
                    String userID = FirebaseAuth.getInstance().getUid();
                    if (userID.equals(sender_id)) {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
                        args.putString("e_id", E_id);
                        args.putString("check", "true");
                        args.putString("message", message);
                        CommentSettingsBottomSheet bottom_sheet = new CommentSettingsBottomSheet();
                        bottom_sheet.setArguments(args);
                        bottom_sheet.show(((FragmentActivity) mContext).getSupportFragmentManager(), bottom_sheet.getTag());
                    } else {
                        Bundle args = new Bundle();
                        args.putString("c_id", C_id);
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

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: reply " + replyList);
        if (mChat.get(position).getHasReply().equals("yes")) {
            if ((mChat.get(position).getMessage().matches(""))) {
                if ((mChat.get(position).getReplyType().matches("image"))) {
                    return CHAT_ITEM_IMAGE_WITH_REPLY_IMAGE;
                } else {
                    return CHAT_ITEM_IMAGE_WITH_REPLY;
                }
            } else {
                if ((mChat.get(position).getReplyType().matches("image"))) {
                    return CHAT_ITEM_WITH_REPLY_IMAGE;
                } else {
                    return CHAT_ITEM_WITH_REPLY;
                }
            }
        } else {
            if (mChat.get(position).getMessage().matches("")) {
                return CHAT_ITEM_IMAGE;
            } else {
                return CHAT_ITEM;
            }
        }
    }
}
