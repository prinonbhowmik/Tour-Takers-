package com.example.tureguideversion1.Adapters;

import android.content.ClipboardManager;
import android.content.Context;
import android.telecom.Call;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Chat;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final String TAG = "ChatAdapter";
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;
    private Context mContext;
    private List<Chat> mChat;
    private DatabaseReference databaseReference;
    String userid;
    FirebaseUser fUser;
    FirebaseAuth auth;
    private int row_index = -1, pos, pos2 = -1;
    private long replyDataCount = 0;
    private int timeVisibility = 0;
    private List<String> key;

    public ChatAdapter(Context context, List<Chat> mChat) {
        this.mContext = context;
        this.mChat = mChat;
//        this.mProfile = mProfile;
        //this.imageUrl = imageUrl;
        setHasStableIds(true);
    }

    @NonNull

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
            return new ViewHolderWithReply(view);
        } else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == MSG_TYPE_RIGHT) {
            ((ViewHolderWithReply) holder).setDataWithReply(mChat.get(position));
        } else {
            ((ViewHolder) holder).setData(mChat.get(position));
        }
        //Chat chat = mChat.get(position);

    }

    public interface replyCount{
        void onCallback(long l);
    }

    private void getReplyCount(replyCount replyCount, String eventID, String commentID){
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(eventID).child(commentID);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    replyCount.onCallback(dataSnapshot.getChildrenCount());
//                    if(c >1){
//                        holder.commentCounted.setText("Swipe right to view "+c+" more previous replies...");
//                    }else if(c == 1){
//                        holder.commentCounted.setText("Swipe right to view "+c+" more previous reply...");
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface replyData {
        void onCallback(HashMap<String,Object> data);
    }

    private static void getReplyData(replyData replyData, String eventID, String commentID){
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(eventID).child(commentID);
        Query query = commentRef.orderByKey().limitToLast(1);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String,Object> map = (HashMap<String, Object>) snapshot.getValue();
                        replyData.onCallback(map);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public interface CID {
        void onCallback(String ID);
    }

    private void getCID(CID cid, String eventID, String commentID){
        DatabaseReference commentRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(eventID).child(commentID);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    cid.onCallback(dataSnapshot.getKey());
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getKey());
                }else {
                    Log.d(TAG, "onDataChange: not exist");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName, commentTimeTV;
        private CircleImageView profileImageView;
        private RelativeLayout mainCommentLayout;
        private HashMap<String,Object> replyData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            mainCommentLayout = itemView.findViewById(R.id.mainCommentLayout);
        }

        void setData(Chat chat) {

            getReplyData(new replyData() {
                @Override
                public void onCallback(HashMap<String, Object> data) {
                    replyData = new HashMap<>();
                    replyData = (HashMap<String, Object>) data;
                }
            },chat.getEventID(),chat.getID());
            showMessage.setText(chat.getMessage());

            senderName.setText(chat.getSenderName());

            if(chat.getSenderImage() != null){
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView)
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView)
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView)
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

//            if(mainCommentLayout != null) {
//                mainCommentLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        row_index = getAdapterPosition();
//                        notifyDataSetChanged();
//                        Log.d(TAG, "onClick: "+row_index);
//                    }
//                });
//            }

//            if (position == row_index) {
//                mainCommentLayout.setSelected(true);
//                if (position > pos2 || position < pos2) {
//                    if(replyLayoutView.getVisibility() == View.GONE) {
//                        if(replyData != null) {
//                            getReplyCount(new replyCount() {
//                                @Override
//                                public void onCallback(long l) {
//                                    replyDataCount = l;
//                                    long c = replyDataCount - 1;
//                                    if(c >1){
//                                        commentCounted.setText("Swipe right to view "+c+" more previous replies...");
//                                    }else if(c == 1){
//                                        commentCounted.setText("Swipe right to view "+c+" more previous reply...");
//                                    }else if(c == 0){
//                                        commentCounted.setText("Swipe right to view reply...");
//                                    }
//                                }
//                            },chat.getEventID(),chat.getID());
//                            replyLayoutView.setVisibility(View.VISIBLE);
//                        }else {
//                            replyLayoutView.setVisibility(View.GONE);
//                            commentCounted.setText("Swipe right to reply...");
//                        }
//                    }
//                    commentCounted.setVisibility(View.VISIBLE);
//                    commentTimeTV.setVisibility(View.VISIBLE);
//                    pos2 = position;
//                } else if (position == pos2) {
//                    if(replyLayoutView.getVisibility() == View.VISIBLE) {
//                        replyLayoutView.setVisibility(View.GONE);
//                    }
//                    commentTimeTV.setVisibility(View.GONE);
//                    commentCounted.setVisibility(View.GONE);
//                    pos2 = -1;
//                }
//            } else {
//                if(mainCommentLayout != null) {
//                    mainCommentLayout.setSelected(false);
//                }
//                commentTimeTV.setVisibility(View.GONE);
//                if(commentCounted != null) {
//                    commentCounted.setVisibility(View.GONE);
//                }
//                if(replyLayoutView != null) {
//                    replyLayoutView.setVisibility(View.GONE);
//                }
//            }
//            if(mainCommentLayout != null) {
//                mainCommentLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        ClipboardManager cm = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                        cm.setText(showMessage.getText());
//                        Toast.makeText(itemView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//            }

        }
    }

    public static class ViewHolderWithReply extends RecyclerView.ViewHolder {
        private TextView showMessage, senderName, commentTimeTV, replyerName, replyerComment, commentCounted;
        private CircleImageView profileImageView, replyerImage;
        private RelativeLayout mainCommentLayout;
        private LinearLayout replyLayoutView;
        private HashMap<String,Object> replyData;

        public ViewHolderWithReply(@NonNull View itemView) {
            super(itemView);
            senderName = itemView.findViewById(R.id.senderName);
            showMessage = itemView.findViewById(R.id.showMessage);
            profileImageView = itemView.findViewById(R.id.chat_profileImage);
            commentTimeTV = itemView.findViewById(R.id.commentTimeTV);
            mainCommentLayout = itemView.findViewById(R.id.mainCommentLayout);
            replyerName = itemView.findViewById(R.id.replyerName);
            replyerComment = itemView.findViewById(R.id.replyerComment);
            commentCounted = itemView.findViewById(R.id.commentCounted);
            replyerImage = itemView.findViewById(R.id.replyerImage);
            replyLayoutView = itemView.findViewById(R.id.replyLayoutView);
        }
        void setDataWithReply(Chat chat) {
            getReplyData(new replyData() {
                @Override
                public void onCallback(HashMap<String, Object> data) {
                    replyData = new HashMap<>();
                    replyData = (HashMap<String, Object>) data;
                }
            },chat.getEventID(),chat.getID());
            showMessage.setText(chat.getMessage());

            senderName.setText(chat.getSenderName());

            if(chat.getSenderImage() != null){
                if (!chat.getSenderImage().isEmpty()) {
                    GlideApp.with(itemView)
                            .load(chat.getSenderImage())
                            .fitCenter()
                            .into(profileImageView);
                } else {
                    if (chat.getSenderSex().matches("male")) {
                        GlideApp.with(itemView)
                                .load(R.drawable.man)
                                .centerInside()
                                .into(profileImageView);
                    } else if (chat.getSenderSex().matches("female")) {
                        GlideApp.with(itemView)
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
//        commentRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//               if(dataSnapshot.exists()){
//                   long c = dataSnapshot.getChildrenCount();
//                   if(c >1){
//                       holder.commentCounted.setText("Swipe right to view "+c+" more previous replies...");
//                   }else if(c == 1){
//                       holder.commentCounted.setText("Swipe right to view "+c+" more previous reply...");
//                   }
//               }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
            Query query = commentRef.orderByKey().limitToLast(1);
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            HashMap<String,Object> map = (HashMap<String, Object>) snapshot.getValue();
                            if (!map.get("senderImage").toString().isEmpty()) {
                                if(replyerImage != null) {
                                    GlideApp.with(itemView)
                                            .load(map.get("senderImage"))
                                            .fitCenter()
                                            .into(replyerImage);
                                }
                            } else {
                                if(replyerImage != null) {
                                    if (chat.getSenderSex().matches("male")) {
                                        GlideApp.with(itemView)
                                                .load(R.drawable.man)
                                                .centerInside()
                                                .into(replyerImage);
                                    } else if (chat.getSenderSex().matches("female")) {
                                        GlideApp.with(itemView)
                                                .load(R.drawable.woman)
                                                .centerInside()
                                                .into(replyerImage);
                                    }
                                }
                            }
                            if((replyerName != null) && (replyerComment != null)) {
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

//            if(mainCommentLayout != null) {
//                mainCommentLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        row_index = getAdapterPosition();
//                        notifyDataSetChanged();
//                        Log.d(TAG, "onClick: "+row_index);
//                    }
//                });
//            }

//            if (position == row_index) {
//                mainCommentLayout.setSelected(true);
//                if (position > pos2 || position < pos2) {
//                    if(replyLayoutView.getVisibility() == View.GONE) {
//                        if(replyData != null) {
//                            getReplyCount(new replyCount() {
//                                @Override
//                                public void onCallback(long l) {
//                                    replyDataCount = l;
//                                    long c = replyDataCount - 1;
//                                    if(c >1){
//                                        commentCounted.setText("Swipe right to view "+c+" more previous replies...");
//                                    }else if(c == 1){
//                                        commentCounted.setText("Swipe right to view "+c+" more previous reply...");
//                                    }else if(c == 0){
//                                        commentCounted.setText("Swipe right to view reply...");
//                                    }
//                                }
//                            },chat.getEventID(),chat.getID());
//                            replyLayoutView.setVisibility(View.VISIBLE);
//                        }else {
//                            replyLayoutView.setVisibility(View.GONE);
//                            commentCounted.setText("Swipe right to reply...");
//                        }
//                    }
//                    commentCounted.setVisibility(View.VISIBLE);
//                    commentTimeTV.setVisibility(View.VISIBLE);
//                    pos2 = position;
//                } else if (position == pos2) {
//                    if(replyLayoutView.getVisibility() == View.VISIBLE) {
//                        replyLayoutView.setVisibility(View.GONE);
//                    }
//                    commentTimeTV.setVisibility(View.GONE);
//                    commentCounted.setVisibility(View.GONE);
//                    pos2 = -1;
//                }
//            } else {
//                if(mainCommentLayout != null) {
//                    mainCommentLayout.setSelected(false);
//                }
//                commentTimeTV.setVisibility(View.GONE);
//                if(commentCounted != null) {
//                    commentCounted.setVisibility(View.GONE);
//                }
//                if(replyLayoutView != null) {
//                    replyLayoutView.setVisibility(View.GONE);
//                }
//            }
//            if(mainCommentLayout != null) {
//                mainCommentLayout.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        ClipboardManager cm = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//                        cm.setText(showMessage.getText());
//                        Toast.makeText(itemView.getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
//                        return true;
//                    }
//                });
//            }
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {

        getCID(new CID() {
            @Override
            public void onCallback(String ID) {
                key = new ArrayList<>();
                key.add(ID);
                //Log.d(TAG, "onCallback: called "+key);
            }
        },mChat.get(position).getEventID(),mChat.get(position).getID());
        //Log.d(TAG, "getItemViewType: eventID: "+mChat.get(position).getEventID()+" ID "+mChat.get(position).getID());
        Log.d(TAG, "getItemViewType: "+key);
        if(mChat.get(position).getHasReply().equals("yes")){
            //Log.d(TAG, "getItemViewType: "+key);
            return MSG_TYPE_RIGHT;
        }else {
            //Log.d(TAG, "getItemViewType: left"+key);
            return MSG_TYPE_LEFT;
        }

//        if (mChat.get(position).getSenderId().equals(fUser.getUid())) {
//            //if(mChat.get(position).getSender().equals(userid)){
//            return MSG_TYPE_RIGHT;
//        } else {
//            return MSG_TYPE_LEFT;
//        }
    }

}
