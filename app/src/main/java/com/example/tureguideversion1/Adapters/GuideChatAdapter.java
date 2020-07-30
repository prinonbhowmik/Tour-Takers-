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
import com.example.tureguideversion1.Model.GuidChat;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class GuideChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public static final String TAG = "GuideChatAdapter";
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    private Context mContext;
    private List<GuidChat> mChat;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private int row_index = -1, pos, pos2 = -1;
    private String ID = "";

    public GuideChatAdapter(Context mContext, List<GuidChat> mChat) {
        this.mContext = mContext;
        this.mChat = mChat;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LEFT) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.guide_chat_item_left, parent, false);
            return new GuideChatAdapter.ViewHolderLeft(view);
        } else if (viewType == RIGHT){
            View view = LayoutInflater.from(mContext).inflate(R.layout.guide_chat_item_right, parent, false);
            return new GuideChatAdapter.ViewHolderRight(view);
        }else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == LEFT) {
            ((GuideChatAdapter.ViewHolderLeft) holder).setData(mChat.get(position), position);
        } else if (getItemViewType(position) == RIGHT){
            ((GuideChatAdapter.ViewHolderRight) holder).setData(mChat.get(position), position);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (mChat.get(position).getSenderID().equals(auth.getUid())) {
            return RIGHT;
        } else {
            return LEFT;
        }
    }

    public class ViewHolderLeft extends RecyclerView.ViewHolder {
        private TextView showMessage, chatTimeTV;
        private CircleImageView chat_profileImage;
        private RelativeLayout chatLayout;
        private ImageView image;

        public ViewHolderLeft(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.showMessage);
            chat_profileImage = itemView.findViewById(R.id.chat_profileImage);
            chatTimeTV = itemView.findViewById(R.id.chatTimeTV);
            chatLayout = itemView.findViewById(R.id.chatLayout);
            image = itemView.findViewById(R.id.image);
        }


        void setData(GuidChat chat, int position) {
            if(chat.getImageMessage() != null){
                if(chat.getImageMessage().trim().length() != 0) {
                    try {
                        showMessage.setVisibility(View.GONE);
                        GlideApp.with(itemView.getContext())
                                .load(chat.getImageMessage())
                                .fitCenter()
                                .into(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    image.setVisibility(View.GONE);
                    showMessage.setText(chat.getMessage());
                }
            }else {
                image.setVisibility(View.GONE);
                showMessage.setText(chat.getMessage());
            }
            if(!ID.matches(chat.getSenderID())) {
                chat_profileImage.setVisibility(View.VISIBLE);
                if (chat.getSenderImage() != null) {
                    if (!chat.getSenderImage().isEmpty()) {
                        GlideApp.with(itemView.getContext())
                                .load(chat.getSenderImage())
                                .fitCenter()
                                .into(chat_profileImage);
                    } else {
                        if (chat.getSenderSex().matches("male")) {
                            GlideApp.with(itemView.getContext())
                                    .load(R.drawable.man)
                                    .centerInside()
                                    .into(chat_profileImage);
                        } else if (chat.getSenderSex().matches("female")) {
                            GlideApp.with(itemView.getContext())
                                    .load(R.drawable.woman)
                                    .centerInside()
                                    .into(chat_profileImage);
                        }
                    }
                }
            }else {
                chat_profileImage.setVisibility(View.INVISIBLE);
            }
            ID = chat.getSenderID();
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
                chatTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    chatTimeTV.setText("1 minute ago");
                } else
                    chatTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    chatTimeTV.setText("1 hour ago");
                } else
                    chatTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    chatTimeTV.setText("1 day ago");
                } else
                    chatTimeTV.setText(days + " days ago");
            }

            if(chatLayout != null) {
                chatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = getAdapterPosition();
                        ID = "";
                        notifyDataSetChanged();
                        //Log.d(TAG, "onClick: "+row_index);
                    }
                });
            }

            if (position == row_index) {
                chatLayout.setSelected(true);
                if (position > pos2 || position < pos2) {
                    chatTimeTV.setVisibility(View.VISIBLE);
                    pos2 = position;
                } else if (position == pos2) {
                    chatTimeTV.setVisibility(View.GONE);
                    pos2 = -1;
                }
            } else {
                if(chatLayout != null) {
                    chatLayout.setSelected(false);
                }
                chatTimeTV.setVisibility(View.GONE);
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

    public class ViewHolderRight extends RecyclerView.ViewHolder {
        private TextView showMessage, chatTimeTV;
        private CircleImageView chat_profileImage;
        private RelativeLayout chatLayout;
        private ImageView image;

        public ViewHolderRight(@NonNull View itemView) {
            super(itemView);
            showMessage = itemView.findViewById(R.id.showMessage);
            chat_profileImage = itemView.findViewById(R.id.chat_profileImage);
            chatTimeTV = itemView.findViewById(R.id.chatTimeTV);
            chatLayout = itemView.findViewById(R.id.chatLayout);
            image = itemView.findViewById(R.id.image);
        }

        void setData(GuidChat chat, int position) {
            if(chat.getImageMessage() != null){
                if(chat.getImageMessage().trim().length() != 0) {
                    try {
                        showMessage.setVisibility(View.GONE);
                        GlideApp.with(itemView.getContext())
                                .load(chat.getImageMessage())
                                .fitCenter()
                                .into(image);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    image.setVisibility(View.GONE);
                    showMessage.setText(chat.getMessage());
                }
            }else {
                image.setVisibility(View.GONE);
                showMessage.setText(chat.getMessage());
            }
            if(!ID.matches(chat.getSenderID())) {
                chat_profileImage.setVisibility(View.VISIBLE);
                if (chat.getSenderImage() != null) {
                    if (!chat.getSenderImage().isEmpty()) {
                        GlideApp.with(itemView.getContext())
                                .load(chat.getSenderImage())
                                .fitCenter()
                                .into(chat_profileImage);
                    } else {
                        if (chat.getSenderSex().matches("male")) {
                            GlideApp.with(itemView.getContext())
                                    .load(R.drawable.man)
                                    .centerInside()
                                    .into(chat_profileImage);
                        } else if (chat.getSenderSex().matches("female")) {
                            GlideApp.with(itemView.getContext())
                                    .load(R.drawable.woman)
                                    .centerInside()
                                    .into(chat_profileImage);
                        }
                    }
                }
            }else {
                chat_profileImage.setVisibility(View.INVISIBLE);
            }
            ID = chat.getSenderID();
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
                chatTimeTV.setText("now");
            } else if (minutes < 60) {
                if (minutes == 1) {
                    chatTimeTV.setText("1 minute ago");
                } else
                    chatTimeTV.setText(minutes + " minutes ago");
            } else if (hours < 24) {
                if (hours == 1) {
                    chatTimeTV.setText("1 hour ago");
                } else
                    chatTimeTV.setText(hours + " hours ago");
            } else {
                if (days == 1) {
                    chatTimeTV.setText("1 day ago");
                } else
                    chatTimeTV.setText(days + " days ago");
            }

            if(chatLayout != null) {
                chatLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        row_index = getAdapterPosition();
                        ID = "";
                        notifyDataSetChanged();
                        //Log.d(TAG, "onClick: "+row_index);
                    }
                });
            }

            if (position == row_index) {
                chatLayout.setSelected(true);
                if (position > pos2 || position < pos2) {
                    chatTimeTV.setVisibility(View.VISIBLE);
                    pos2 = position;
                } else if (position == pos2) {
                    chatTimeTV.setVisibility(View.GONE);
                    pos2 = -1;
                }
            } else {
                if(chatLayout != null) {
                    chatLayout.setSelected(false);
                }
                chatTimeTV.setVisibility(View.GONE);
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
}
