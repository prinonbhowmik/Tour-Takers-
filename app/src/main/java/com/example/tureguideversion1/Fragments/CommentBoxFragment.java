package com.example.tureguideversion1.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.ChatAdapter;
import com.example.tureguideversion1.Model.Chat;
import com.example.tureguideversion1.Model.Profile;
import com.example.tureguideversion1.Notifications.APIService;
import com.example.tureguideversion1.Notifications.Client;
import com.example.tureguideversion1.Notifications.Data;
import com.example.tureguideversion1.Notifications.Response;
import com.example.tureguideversion1.Notifications.Sender;
import com.example.tureguideversion1.Notifications.Token;
import com.example.tureguideversion1.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.Context.MODE_PRIVATE;

public class CommentBoxFragment extends Fragment {

    public static final String TAG = "CommentsBox";
    private String senderID, currentEventId, senderName, senderImage, senderSex;
    private EditText commentET;
    private ImageButton sendMessage;
    private ImageView notiBTMS, closeBTMS;
    private int e, d;
    FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private RecyclerView chatRecyclerView;
    private List<Chat> mChat;
    private ChatAdapter chatAdapter;
    private APIService apiService;
    private boolean notify = false;
    private RadioRealButtonGroup radioGroup;
    private LinearLayout radioLayout;
    private View view;

    public CommentBoxFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_comment_box, container, false);
        init(view);


        Bundle bundle = getArguments();
        currentEventId = bundle.getString("event_id");
        //currentEventId="-M9zMMxVM07BOzpgapJQ";

        DatabaseReference sendr = databaseReference.child("profile").child(senderID);
        sendr.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                java.util.Map<String, Object> values = (java.util.Map<String, Object>) dataSnapshot.getValue();
                senderName = values.get("name").toString();
                senderImage = values.get("image").toString();
                senderSex = values.get("sex").toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notiBTMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioLayout.getVisibility() == View.GONE) {
                    radioLayout.setVisibility(View.VISIBLE);
                    radioLayout.setAlpha(0.0f);

                    // Start the animation
                    radioLayout.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(200)
                            .setListener(null);
                } else if (radioLayout.getVisibility() == View.VISIBLE) {
                    radioLayout.animate()
                            .translationY(-150)
                            .alpha(0.0f)
                            .setDuration(200)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    radioLayout.setVisibility(View.GONE);
                                }
                            });
                }
            }
        });

        radioGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if (position == 0) {
                    e = 1;
                    d = 0;

                } else if (position == 1) {
                    e = 0;
                    d = 1;

                }
                //Toast.makeText(getContext(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        closeBTMS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dismiss();
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String mess = commentET.getText().toString();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
                String commentTime = simpleDateFormat.format(calendar.getTime());
                if (mess.trim().length() != 0) {
                    setSendMessage(mess, senderID, senderName, senderImage, senderSex, commentTime);
                }
                commentET.setText(null);
            }
        });

        Profile profile = new Profile();
        Chat chat = new Chat();
        readMessage();


        return view;
    }

    private void init(View view) {
        commentET = view.findViewById(R.id.commentET);
        sendMessage = view.findViewById(R.id.sendMessage);
        auth = FirebaseAuth.getInstance();
        senderID = auth.getUid();
        mChat = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        chatRecyclerView = view.findViewById(R.id.chatRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(linearLayoutManager);
        chatAdapter = new ChatAdapter(getContext(), mChat);
        chatRecyclerView.setAdapter(chatAdapter);
        //chatRecyclerView.setHasFixedSize(true);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        radioGroup = view.findViewById(R.id.radioGroup);
        notiBTMS = view.findViewById(R.id.notiBTMS);
        closeBTMS = view.findViewById(R.id.closeBTMS);
        radioLayout = view.findViewById(R.id.radioLayout);
    }


    void setSendMessage(String message, String senderID, String senderName, String senderImage, String senderSex, String commentTime) {

        DatabaseReference ref = databaseReference.child("eventComments").child(currentEventId);
        String id = ref.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("message", message);
        hashMap.put("senderID", senderID);
        hashMap.put("senderName", senderName);
        hashMap.put("senderImage", senderImage);
        hashMap.put("senderSex", senderSex);
        hashMap.put("ID", id);
        hashMap.put("commentTime", commentTime);
        hashMap.put("eventID", currentEventId);
        ref.child(id).setValue(hashMap);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                updateToken(instanceIdResult.getToken());
            }
        });
        if (notify) {
            sendNotifiaction(currentEventId, senderName, "also commented");
        }
        notify = false;

//        reference = FirebaseDatabase.getInstance().getReference("Users").child(fuser.getUid());
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                User user = dataSnapshot.getValue(User.class);
//                if (notify) {
//                    sendNotifiaction(senderName, user.getUsername(), msg);
//                }
//                notify = false;
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void readMessage() {
        mChat = new ArrayList<>();
        DatabaseReference ref = databaseReference.child("eventComments").child(currentEventId);
        Query locations = ref.orderByKey();
        locations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mChat.clear();
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {

                        Chat chat = childSnapshot.getValue(Chat.class);
                        mChat.add(chat);
                        chatAdapter = new ChatAdapter(getActivity(), mChat);
                        chatRecyclerView.setAdapter(chatAdapter);
                        chatAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateToken(String token) {
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        DatabaseReference ref = databaseReference.child("eventCommentsTokens");
        Token token1 = new Token(token);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", auth.getUid());
        hashMap.put("token", token1.getToken());
        ref.child(currentEventId).child(auth.getUid()).setValue(hashMap);
    }

    private void sendNotifiaction(String eventID, final String username, final String message) {
        ArrayList<String> membersID = new ArrayList<>();
        DatabaseReference members = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventID);
        members.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    java.util.Map map = (Map) snapshot.getValue();
                    if (!auth.getUid().matches((String) map.get("userID"))) {
                        membersID.add((String) map.get("userID"));
                    }
                    //Log.d(TAG, "onDataChange: "+map.get("userID"));
                }

                for (int i = 0; i < membersID.size(); i++) {
                    DatabaseReference tokens = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventID);
                    Query query = tokens.orderByKey().equalTo(membersID.get(i));
                    int finalI = i;
                    query.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                    Token token = snapshot.getValue(Token.class);
                                    Data data = new Data(eventID, R.drawable.ic_stat_ic_notification, username + " " + message, "Event", membersID.get(finalI), auth.getUid());

                                    Sender sender = new Sender(data, token.getToken());

                                    apiService.sendNotification(sender)
                                            .enqueue(new Callback<Response>() {
                                                @Override
                                                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                                                    if (response.code() == 200) {
                                                        if (response.body().success != 1) {
                                                            Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<Response> call, Throwable t) {

                                                }
                                            });

                                }
                            } else {
                                Log.d(TAG, "onDataChange: not exist");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void currentUser(String userid) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentuser", userid);
        editor.apply();
    }

//    @Override
//    public void onDismiss(@NonNull DialogInterface dialog) {
//        super.onDismiss(dialog);
//        currentUser("none");
//    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser(auth.getUid());
    }


}