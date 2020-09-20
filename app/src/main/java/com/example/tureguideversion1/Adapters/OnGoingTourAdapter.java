package com.example.tureguideversion1.Adapters;

import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.request.RequestOptions;
import com.example.tureguideversion1.Activities.AdminChatBox;
import com.example.tureguideversion1.Activities.CommentsBox;
import com.example.tureguideversion1.Activities.GuideChatBox;
import com.example.tureguideversion1.Activities.JoinMemberDetails;
import com.example.tureguideversion1.Activities.LocationImage;
import com.example.tureguideversion1.Activities.NoInternetConnection;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.Model.EventLocationList;
import com.example.tureguideversion1.Notifications.APIService;
import com.example.tureguideversion1.Notifications.Client;
import com.example.tureguideversion1.Notifications.Data;
import com.example.tureguideversion1.Notifications.Response;
import com.example.tureguideversion1.Notifications.Sender;
import com.example.tureguideversion1.Notifications.Token;
import com.example.tureguideversion1.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;

public class OnGoingTourAdapter extends PagerAdapter {
    public static final String TAG = "OnGoingTourAdapter";
    private List<Event> data;
    private Context context;
    private String forView;
    private List<EventLocationList> locationLists;
    private EventLocationListAdapter locationAdapter;
    private View itemView;
    private FirebaseAuth auth;
    private APIService apiService;
    public OnPosition onPosition;
    public OnEvent onEvent;

    public OnGoingTourAdapter(List<Event> data, Context context, String forView, OnPosition onPosition, OnEvent onEvent) {
        this.data = data;
        this.context = context;
        this.forView = forView;
        this.onPosition = onPosition;
        this.onEvent = onEvent;
    }

    public interface OnPosition {
        void position(int position, String forView);
    }

    public interface OnEvent {
        void getEventID(int position, String ID, String admin);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
        Event event = data.get(position);
        auth = FirebaseAuth.getInstance();
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = mLayoutInflater.inflate(R.layout.ongoing_viewpager_layout, container, false);
        ArrayList<String> locationWillbeVisit = new ArrayList<>();
        BaseSliderView.OnSliderClickListener clickListener = new BaseSliderView.OnSliderClickListener() {
            @Override
            public void onSliderClick(BaseSliderView slider) {
                Intent i = new Intent(itemView.getContext(), LocationImage.class)
                        .putExtra("slide", slider.getBundle().getString("extra"))
                        .putExtra("location", event.getPlace().toLowerCase())
                        .putStringArrayListExtra("willVisit", (ArrayList<String>) locationWillbeVisit);
                itemView.getContext().startActivity(i);
            }
        };
        TextView event_placeTV, event_publish_dateTV, start_dateTV, return_dateTV, event_timeTV, meeting_placeTV, group_nameTV, event_descriptionTV,
                event_costTV, guide_nameTV, txt5, attending_memberTV, viewTV, commentCountTV, viewComment, tour_costTV, tour_guide_nameTV,
                meeting_place_guideTV, tourStatus, txt20;
        RecyclerView locationRecycleView;
        SliderLayout imageSlider;
        CircleImageView guide_imageIV, tour_guide_imageIV;
        RelativeLayout relative6, forTourLayout, forEvenLayout, relative20;
        Button cancel_joinBtn, cencelTourBTN, cencelEventBTN;

        event_placeTV = itemView.findViewById(R.id.event_placeTV);
        event_publish_dateTV = itemView.findViewById(R.id.event_publish_dateTV);
        start_dateTV = itemView.findViewById(R.id.start_dateTV);
        return_dateTV = itemView.findViewById(R.id.return_dateTV);
        event_timeTV = itemView.findViewById(R.id.event_timeTV);
        meeting_placeTV = itemView.findViewById(R.id.meeting_placeTV);
        group_nameTV = itemView.findViewById(R.id.group_nameTV);
        event_descriptionTV = itemView.findViewById(R.id.event_descriptionTV);
        event_costTV = itemView.findViewById(R.id.event_costTV);
        locationRecycleView = itemView.findViewById(R.id.eventLocationList_recyclerview);
        locationRecycleView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        locationRecycleView.setHasFixedSize(true);
        imageSlider = itemView.findViewById(R.id.sliderFromEventDetails);
        guide_nameTV = itemView.findViewById(R.id.guide_nameTV);
        txt5 = itemView.findViewById(R.id.txt5);
        guide_imageIV = itemView.findViewById(R.id.guide_imageIV);
        relative6 = itemView.findViewById(R.id.relative6);
        attending_memberTV = itemView.findViewById(R.id.attending_memberTV);
        viewTV = itemView.findViewById(R.id.viewTV);
        commentCountTV = itemView.findViewById(R.id.commentCount);
        viewComment = itemView.findViewById(R.id.viewComment);
        cancel_joinBtn = itemView.findViewById(R.id.cancel_joinBtn);
        relative20 = itemView.findViewById(R.id.relative20);
        forTourLayout = itemView.findViewById(R.id.forTourLayout);
        forEvenLayout = itemView.findViewById(R.id.forEvenLayout);
        tour_costTV = itemView.findViewById(R.id.tour_costTV);
        tour_guide_nameTV = itemView.findViewById(R.id.tour_guide_nameTV);
        tour_guide_imageIV = itemView.findViewById(R.id.tour_guide_imageIV);
        cencelTourBTN = itemView.findViewById(R.id.cencelTourBTN);
        cencelEventBTN = itemView.findViewById(R.id.cencelEventBTN);
        meeting_place_guideTV = itemView.findViewById(R.id.meeting_place_guideTV);
        tourStatus = itemView.findViewById(R.id.tourStatus);
        txt20 = itemView.findViewById(R.id.txt20);

        ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.loadAnimator(itemView.getContext(), R.animator.flipping);
        if(forView.matches("event")) {
            anim.setTarget(guide_imageIV);
            anim.setDuration(2000);
            anim.setRepeatCount(ValueAnimator.INFINITE);
        }else if(forView.matches("tour")) {
            anim.setTarget(tour_guide_imageIV);
            anim.setDuration(2000);
            anim.setRepeatCount(ValueAnimator.INFINITE);
        }

        if (forView.matches("event")) {
            onEvent.getEventID(position, event.getId(),event.getEventPublisherId());
            forTourLayout.setVisibility(View.GONE);
            cencelEventBTN.setText("Cancel Event");
            DatabaseReference locationListRef = FirebaseDatabase.getInstance().getReference().child("eventLocationList").child(event.getId());
            locationListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        locationLists = new ArrayList<>();
                        for (DataSnapshot childSnap : snapshot.getChildren()) {
                            locationWillbeVisit.add(childSnap.getKey());
                            EventLocationList location = new EventLocationList(childSnap.getKey());
                            locationLists.add(location);
                        }
                        locationAdapter = new EventLocationListAdapter(locationLists, itemView.getContext());
                        locationRecycleView.setAdapter(locationAdapter);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(event.getPlace().toLowerCase());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot childSnap2 : snapshot.getChildren()) {
                                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap2.getValue();
                                        if (locationWillbeVisit.contains(map.get("locationName").toString())) {
                                            RequestOptions requestOptions = new RequestOptions();
                                            requestOptions.centerCrop();
                                            TextSliderView sliderView = new TextSliderView(itemView.getContext());
                                            sliderView
                                                    .image((String) map.get("image"))
                                                    .description((String) map.get("locationName"))
                                                    .setRequestOption(requestOptions)
                                                    .setProgressBarVisible(false)
                                                    .setOnSliderClickListener(clickListener);

                                            sliderView.bundle(new Bundle());
                                            sliderView.getBundle().putString("extra", (String) map.get("locationName"));
                                            imageSlider.addSlider(sliderView);
                                            if (imageSlider.getSliderImageCount() < 2) {
                                                imageSlider.stopAutoCycle();
                                                imageSlider.setPagerTransformer(false, new BaseTransformer() {
                                                    @Override
                                                    protected void onTransform(View view, float v) {
                                                    }
                                                });
                                                imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                                            } else {

                                                imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                                imageSlider.startAutoCycle();
                                                imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                                                imageSlider.setCustomAnimation(new DescriptionAnimation());
                                                imageSlider.setDuration(4000);
                                                imageSlider.stopCyclingWhenTouch(true);

                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (event.getEventPublisherId().matches(auth.getUid())) {
                if (cancel_joinBtn.getVisibility() == View.VISIBLE) {
                    cancel_joinBtn.setVisibility(View.GONE);
                }
                if (cencelEventBTN.getVisibility() == View.GONE) {
                    cencelEventBTN.setVisibility(View.VISIBLE);
                }
                cencelEventBTN.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        cancelAlertmessage(event.getId(),forView,event.getGuideID(),position);
                    }
                });
            } else {
                if (cancel_joinBtn.getVisibility() == View.GONE) {
                    cancel_joinBtn.setVisibility(View.VISIBLE);
                }
                if (cencelEventBTN.getVisibility() == View.VISIBLE) {
                    cencelEventBTN.setVisibility(View.GONE);
                }
            }

            if (event.getEventPublisherId().matches(auth.getUid())) {
                DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(forView).child(event.getId()).child("status");
                eventRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.getValue().toString().matches("◦•●◉✿ ACTIVE ✿◉●•◦")) {
                                tourStatus.setText(snapshot.getValue().toString());
                                tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorBlack));
                                if (event.getGuideID() != null) {
                                    if (anim.isRunning()) {
                                        anim.end();
                                    }
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child(event.getGuideID());
                                    userRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                                                guide_nameTV.setText((String) map.get("name"));
                                                txt5.setText("Assigned Guide");
                                                if (!map.get("image").toString().matches("")) {
                                                    try {
                                                        GlideApp.with(itemView.getContext())
                                                                .load(map.get("image"))
                                                                .fitCenter()
                                                                .into(guide_imageIV);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    try {
                                                        GlideApp.with(itemView.getContext())
                                                                .load(getImageFromDrawable("man"))
                                                                .fitCenter()
                                                                .into(guide_imageIV);
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                                relative6.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        ShowProfilePopup((String) map.get("name"),
                                                                (String) map.get("phone"),
                                                                (String) map.get("email"),
                                                                (String) map.get("image"),
                                                                (String) map.get("sex"),
                                                                (Long) map.get("tour"),
                                                                (Long) map.get("event"),
                                                                (Long) map.get("ratingCounter"),
                                                                (Long) map.get("rating"),
                                                                event.getId(),
                                                                event.getGuideID(),
                                                                "guide");
                                                    }
                                                });
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            } else if (snapshot.getValue().toString().matches("◦•●◉✿ Pending ✿◉●•◦")) {
                                if (event.getGuideID() != null) {
                                    tourStatus.setText(snapshot.getValue().toString());
                                    tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
                                    guide_nameTV.setText("Not assaigned");
                                    txt5.setText("Tap to request a guide...");
                                    try {
                                        GlideApp.with(itemView.getContext())
                                                .load(getImageFromDrawable("man"))
                                                .fitCenter()
                                                .into(guide_imageIV);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    relative6.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            requestingGuide(event.getPlace(), event.getStartDate(), event.getReturnDate(), forView, event.getId(), txt5, position, event.getGuideID(), anim);
                                            txt5.setText("Finding guide...");
                                            anim.start();
                                            Handler handler = new Handler();
                                            Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                            .child(forView)
                                                            .child(event.getId())
                                                            .child("guideID");
                                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(!snapshot.exists()){
                                                                Toasty.info(context,"All guides are busy right now. Please try again later...!",Toasty.LENGTH_SHORT).show();
                                                                txt5.setText("Tap to request a guide...");
                                                                anim.end();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            };
                                            handler.postDelayed(runnable, 90000);
                                        }
                                    });
                                } else {
                                    tourStatus.setText(snapshot.getValue().toString());
                                    tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
                                    guide_nameTV.setText("Not assaigned");
                                    txt5.setText("Tap to request a guide...");
                                    try {
                                        GlideApp.with(itemView.getContext())
                                                .load(getImageFromDrawable("man"))
                                                .fitCenter()
                                                .into(guide_imageIV);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    relative6.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            requestingGuide(event.getPlace(), event.getStartDate(), event.getReturnDate(), forView, event.getId(), txt5, position, event.getGuideID(), anim);
                                            txt5.setText("Finding guide...");
                                            anim.start();
                                            Handler handler = new Handler();
                                            Runnable runnable = new Runnable() {
                                                @Override
                                                public void run() {
                                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                            .child(forView)
                                                            .child(event.getId())
                                                            .child("guideID");
                                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            if(!snapshot.exists()){
                                                                Toasty.info(context,"All guides are busy right now. Please try again later...!",Toasty.LENGTH_SHORT).show();
                                                                txt5.setText("Tap to request a guide...");
                                                                anim.end();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }
                                            };
                                            handler.postDelayed(runnable, 90000);
                                        }
                                    });
                                }
                            }
                        }else {
                            tourStatus.setText("◦•●◉✿ Pending ✿◉●•◦");
                            tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
                            guide_nameTV.setText("Not assaigned");
                            txt5.setText("Tap to request a guide...");
                            try {
                                GlideApp.with(itemView.getContext())
                                        .load(getImageFromDrawable("man"))
                                        .fitCenter()
                                        .into(guide_imageIV);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            relative6.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestingGuide(event.getPlace(), event.getStartDate(), event.getReturnDate(), forView, event.getId(), txt5, position, event.getGuideID(), anim);
                                    txt5.setText("Finding guide...");
                                    anim.start();
                                    Handler handler = new Handler();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                    .child(forView)
                                                    .child(event.getId())
                                                    .child("guideID");
                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(!snapshot.exists()){
                                                        Toasty.info(context,"All guides are busy right now. Please try again later...!",Toasty.LENGTH_SHORT).show();
                                                        txt5.setText("Tap to request a guide...");
                                                        anim.end();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    };
                                    handler.postDelayed(runnable, 90000);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            } else {
                Log.d(TAG, "instantiateItem: "+event.getId());
                DatabaseReference adminRef = FirebaseDatabase.getInstance().getReference().child("profile").child(event.getEventPublisherId());
                adminRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                        guide_nameTV.setText((String) map.get("name"));
                        txt5.setText("Event Admin");
                        if (!map.get("image").toString().matches("")) {
                            try {
                                GlideApp.with(itemView.getContext())
                                        .load(map.get("image"))
                                        .fitCenter()
                                        .into(guide_imageIV);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            if (map.get("sex").toString().matches("male")) {
                                GlideApp.with(itemView.getContext())
                                        .load(getImageFromDrawable("man"))
                                        .centerInside()
                                        .into(guide_imageIV);
                            } else if (map.get("sex").toString().matches("female")) {
                                GlideApp.with(itemView.getContext())
                                        .load(getImageFromDrawable("woman"))
                                        .centerInside()
                                        .into(guide_imageIV);
                            }
                        }
                        relative6.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ShowProfilePopup((String) map.get("name"),
                                        (String) map.get("phone"),
                                        (String) map.get("email"),
                                        (String) map.get("image"),
                                        (String) map.get("sex"),
                                        (Long) map.get("tour"),
                                        (Long) map.get("event"),
                                        (Long) map.get("ratingCounter"),
                                        (Long) map.get("rating"),
                                        event.getId(),
                                        event.getEventPublisherId(),
                                        "admin");
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            DatabaseReference comment = FirebaseDatabase.getInstance().getReference().child("eventComments").child(event.getId());
            comment.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    commentCountTV.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            event_placeTV.setText(event.getPlace());
            event_publish_dateTV.setText(event.getPublishDate());
            start_dateTV.setText(event.getStartDate());
            return_dateTV.setText(event.getReturnDate());
            meeting_place_guideTV.setText(event.getGuideMeetPlace());
            event_timeTV.setText(event.getTime());
            meeting_placeTV.setText(event.getMeetPlace());
            group_nameTV.setText(event.getGroupName());
            event_descriptionTV.setText(event.getDescription());
            event_costTV.setText(event.getCost());
            attending_memberTV.setText(String.valueOf(event.getJoinMemberCount()));

            viewTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), JoinMemberDetails.class);
                    intent.putExtra("event_id", event.getId());
                    context.startActivity(intent);
                    ((FragmentActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            viewComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), CommentsBox.class);
                    intent.putExtra("eventId", event.getId());
                    context.startActivity(intent);
                    ((FragmentActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });

            cancel_joinBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new InternetCheck(internet -> {
                        if (internet) {
                            cancelJoinAlertDialog(event.getId());
                        } else {
                            context.startActivity(new Intent(context, NoInternetConnection.class));
                        }
                    });
                }
            });
        } else if (forView.matches("tour")) {
            forEvenLayout.setVisibility(View.GONE);
            cencelTourBTN.setText("Cancel Tour");
            DatabaseReference locationListRef = FirebaseDatabase.getInstance().getReference().child("tourLocationList").child(event.getId());
            locationListRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        locationLists = new ArrayList<>();
                        for (DataSnapshot childSnap : snapshot.getChildren()) {
                            locationWillbeVisit.add(childSnap.getKey());
                            EventLocationList location = new EventLocationList(childSnap.getKey());
                            locationLists.add(location);
                        }
                        locationAdapter = new EventLocationListAdapter(locationLists, itemView.getContext());
                        locationRecycleView.setAdapter(locationAdapter);

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(event.getPlace().toLowerCase());
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    for (DataSnapshot childSnap2 : snapshot.getChildren()) {
                                        HashMap<String, Object> map = (HashMap<String, Object>) childSnap2.getValue();
                                        if (locationWillbeVisit.contains(map.get("locationName").toString())) {
                                            RequestOptions requestOptions = new RequestOptions();
                                            requestOptions.centerCrop();
                                            TextSliderView sliderView = new TextSliderView(itemView.getContext());
                                            sliderView
                                                    .image((String) map.get("image"))
                                                    .description((String) map.get("locationName"))
                                                    .setRequestOption(requestOptions)
                                                    .setProgressBarVisible(false)
                                                    .setOnSliderClickListener(clickListener);

                                            sliderView.bundle(new Bundle());
                                            sliderView.getBundle().putString("extra", (String) map.get("locationName"));
                                            imageSlider.addSlider(sliderView);
                                            if (imageSlider.getSliderImageCount() < 2) {
                                                imageSlider.stopAutoCycle();
                                                imageSlider.setPagerTransformer(false, new BaseTransformer() {
                                                    @Override
                                                    protected void onTransform(View view, float v) {
                                                    }
                                                });
                                                imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                                            } else {

                                                imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                                imageSlider.startAutoCycle();
                                                imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                                                imageSlider.setCustomAnimation(new DescriptionAnimation());
                                                imageSlider.setDuration(4000);
                                                imageSlider.stopCyclingWhenTouch(true);

                                            }
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            event_placeTV.setText(event.getPlace());
            event_publish_dateTV.setText(event.getPublishDate());
            start_dateTV.setText(event.getStartDate());
            return_dateTV.setText(event.getReturnDate());
            meeting_place_guideTV.setText(event.getGuideMeetPlace());
            tour_costTV.setText(event.getCost());
            cencelTourBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelAlertmessage(event.getId(),forView,event.getGuideID(),position);
                }
            });
            DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(forView).child(event.getId()).child("status");
            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().toString().matches("◦•●◉✿ ACTIVE ✿◉●•◦")) {
                            tourStatus.setText(snapshot.getValue().toString());
                            tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorBlack));
                            if (event.getGuideID() != null) {
                                if (anim.isRunning()) {
                                    anim.end();
                                }
                                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child(event.getGuideID());
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            HashMap<String, Object> map = (HashMap<String, Object>) snapshot.getValue();
                                            tour_guide_nameTV.setText((String) map.get("name"));
                                            txt5.setText("Assigned Guide");
                                            if (!map.get("image").toString().matches("")) {
                                                try {
                                                    GlideApp.with(itemView.getContext())
                                                            .load(map.get("image"))
                                                            .fitCenter()
                                                            .into(tour_guide_imageIV);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            } else {
                                                try {
                                                    GlideApp.with(itemView.getContext())
                                                            .load(getImageFromDrawable("man"))
                                                            .fitCenter()
                                                            .into(tour_guide_imageIV);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            relative20.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    ShowProfilePopup((String) map.get("name"),
                                                            (String) map.get("phone"),
                                                            (String) map.get("email"),
                                                            (String) map.get("image"),
                                                            (String) map.get("sex"),
                                                            (Long) map.get("tour"),
                                                            (Long) map.get("event"),
                                                            (Long) map.get("ratingCounter"),
                                                            (Long) map.get("rating"),
                                                            event.getId(),
                                                            event.getGuideID(),
                                                            "guide");
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        } else if (snapshot.getValue().toString().matches("◦•●◉✿ Pending ✿◉●•◦")) {
                            tourStatus.setText(snapshot.getValue().toString());
                            tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
                            tour_guide_nameTV.setText("Not assaigned");
                            txt20.setText("Tap to request a guide...");
                            try {
                                GlideApp.with(itemView.getContext())
                                        .load(getImageFromDrawable("man"))
                                        .fitCenter()
                                        .into(tour_guide_imageIV);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            relative20.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    requestingGuide(event.getPlace(), event.getStartDate(), event.getReturnDate(), forView, event.getId(), txt5, position, event.getGuideID(),anim);
                                    txt20.setText("Finding guide...");
                                    anim.start();
                                    Handler handler = new Handler();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                    .child(forView)
                                                    .child(event.getId())
                                                    .child("guideID");
                                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    if(!snapshot.exists()){
                                                        Toasty.info(context,"All guides are busy right now. Please try again later...!",Toasty.LENGTH_SHORT).show();
                                                        txt20.setText("Tap to request a guide...");
                                                        anim.end();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }
                                    };
                                    handler.postDelayed(runnable, 90000);
                                }
                            });
                        }
                    }else {
                        tourStatus.setText("◦•●◉✿ Pending ✿◉●•◦");
                        tourStatus.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
                        tour_guide_nameTV.setText("Not assaigned");
                        txt20.setText("Tap to request a guide...");
                        try {
                            GlideApp.with(itemView.getContext())
                                    .load(getImageFromDrawable("man"))
                                    .fitCenter()
                                    .into(tour_guide_imageIV);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        relative20.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                requestingGuide(event.getPlace(), event.getStartDate(), event.getReturnDate(), forView, event.getId(), txt5, position, event.getGuideID(), anim);
                                txt20.setText("Finding guide...");
                                anim.start();
                                Handler handler = new Handler();
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                                                .child(forView)
                                                .child(event.getId())
                                                .child("guideID");
                                        ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if(!snapshot.exists()){
                                                    Toasty.info(context,"All guides are busy right now. Please try again later...!",Toasty.LENGTH_SHORT).show();
                                                    txt20.setText("Tap to request a guide...");
                                                    anim.end();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }
                                };
                                handler.postDelayed(runnable, 90000);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        container.addView(itemView);

        return itemView;
    }

    private void cancelAlertmessage(String ID, String type, String guideID, int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Cancel..!!");
        dialog.setIcon(R.drawable.ic_delete_white);
        dialog.setMessage("Do you want to cancel this event?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (type.matches("event")) {
                    ArrayList<String> eventKeys = new ArrayList<>();
                    DatabaseReference eRef = FirebaseDatabase.getInstance().getReference().child("event").child(ID);
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("eventJoinMember").child(ID);
                    DatabaseReference lRef = FirebaseDatabase.getInstance().getReference().child("eventLocationList").child(ID);
                    DatabaseReference cRef = FirebaseDatabase.getInstance().getReference().child("eventComments").child(ID);
                    DatabaseReference commentsTokenRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(ID);
                    DatabaseReference replyRef = FirebaseDatabase.getInstance().getReference().child("eventCommentsReply").child(ID);
                    DatabaseReference notiRef = FirebaseDatabase.getInstance().getReference().child("notificationStatus").child("eventCommentNotifiaction").child(ID);
                    DatabaseReference guideChat = FirebaseDatabase.getInstance().getReference().child("chatWithGuide").child(ID);
                    DatabaseReference adminChat = FirebaseDatabase.getInstance().getReference().child("chatWithAdmin").child(ID);
                    adminChat.removeValue();
                    guideChat.removeValue();
                    notiRef.removeValue();
                    eRef.removeValue();
                    mRef.removeValue();
                    lRef.removeValue();
                    cRef.removeValue();
                    commentsTokenRef.removeValue();
                    replyRef.removeValue();
                    ArrayList<String> userIDs = new ArrayList<>();
                    DatabaseReference removeActivity = FirebaseDatabase.getInstance().getReference().child("userActivities");
                    removeActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    userIDs.add(childSnapshot.getKey());
                                    //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                }
                                for (int i = 0; i < userIDs.size(); i++) {
                                    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(userIDs.get(i)).child("events");
                                    int finalI = i;
                                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                    HashMap<String, Object> hashMap = (HashMap<String, Object>) childSnapshot.getValue();
                                                    if (hashMap.get("eventID").toString().equals(ID)) {
                                                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                                                                .child("userActivities")
                                                                .child(userIDs.get(finalI))
                                                                .child("events")
                                                                .child(childSnapshot.getKey());
                                                        eventRef.removeValue();
                                                        //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Toasty.success(context, "Delete Success", Toasty.LENGTH_SHORT).show();
                    onPosition.position(position, forView);
                }else if (type.matches("tour")) {
                    ArrayList<String> eventKeys = new ArrayList<>();
                    DatabaseReference eRef = FirebaseDatabase.getInstance().getReference().child("tour").child(ID);
                    DatabaseReference lRef = FirebaseDatabase.getInstance().getReference().child("tourLocationList").child(ID);
                    DatabaseReference guideChat = FirebaseDatabase.getInstance().getReference().child("chatWithGuide").child(ID);
                    guideChat.removeValue();
                    eRef.removeValue();
                    lRef.removeValue();
                    ArrayList<String> userIDs = new ArrayList<>();
                    DatabaseReference removeActivity = FirebaseDatabase.getInstance().getReference().child("userActivities");
                    removeActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                    userIDs.add(childSnapshot.getKey());
                                    //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                }
                                for (int i = 0; i < userIDs.size(); i++) {
                                    DatabaseReference eventsRef = FirebaseDatabase.getInstance().getReference().child("userActivities").child(userIDs.get(i)).child("tours");
                                    int finalI = i;
                                    eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                    HashMap<String, Object> hashMap = (HashMap<String, Object>) childSnapshot.getValue();
                                                    if (hashMap.get("tourID").toString().equals(ID)) {
                                                        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference()
                                                                .child("userActivities")
                                                                .child(userIDs.get(finalI))
                                                                .child("tours")
                                                                .child(childSnapshot.getKey());
                                                        eventRef.removeValue();
                                                        //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    Toasty.success(context, "Delete Success", Toasty.LENGTH_SHORT).show();
                    onPosition.position(position, forView);
                }
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();


    }

    private void guideMonitoring(String ID, int position, String guideID) {
        DatabaseReference eventRef = FirebaseDatabase.getInstance().getReference().child(forView).child(ID).child("guideID");
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (guideID == null) {
                        //Log.d(TAG, "onDataChange: accepted");
                        Toasty.success(context, "Assigned a guide in this tour.", Toasty.LENGTH_SHORT).show();
                        onPosition.position(position, forView);
                        eventRef.removeEventListener(this);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void cancelJoinAlertDialog(String eventID) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Alert..!!");
        dialog.setIcon(R.drawable.ic_leave_white);
        dialog.setMessage("Do you want to leave this event?");
        dialog.setCancelable(false);
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> eventKeys = new ArrayList<>();
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("eventJoinMember").child(eventID).child(auth.getUid());
                mRef.removeValue();
                DatabaseReference removeToken = FirebaseDatabase.getInstance().getReference().child("eventCommentsTokens").child(eventID).child(auth.getUid());
                removeToken.removeValue();
                DatabaseReference removeActivity = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events");
                removeActivity.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                //eventKeys.add(childSnapshot.getKey());
                                HashMap<String, Object> hashMap = (HashMap<String, Object>) childSnapshot.getValue();
                                if (hashMap.get("eventID").toString().equals(eventID)) {
                                    eventKeys.add(childSnapshot.getKey());
                                    //Log.d(TAG, "onDataChange: "+childSnapshot.getKey());
                                }
                            }
                            for (int i = 0; i < eventKeys.size(); i++) {
                                DatabaseReference remove = FirebaseDatabase.getInstance().getReference().child("userActivities").child(auth.getUid()).child("events").child(eventKeys.get(i));
                                remove.removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();


    }

    private void requestingGuide(String district, String startDate, String returnDate, String typeFor,
                                 String typeID, TextView textView, int position, String guideID, ObjectAnimator anim) {

        DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference().child("guidesAreOnline").child(district);
        onlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot childSnapShot : snapshot.getChildren()) {
                        HashMap<String, Object> onlineMap = (HashMap<String, Object>) childSnapShot.getValue();
                        DatabaseReference guideRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child((String) onlineMap.get("ID"));
                        guideRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    HashMap<String, Object> guideMap = (HashMap<String, Object>) snapshot.getValue();
                                    Token token = snapshot.getValue(Token.class);
                                    Data data = new Data((String) guideMap.get("Id"), auth.getUid(), startDate, returnDate, "request", typeFor, typeID, district);
                                    Sender sender = new Sender(data, token.getToken());
                                    apiService.sendNotification(sender)
                                            .enqueue(new Callback<Response>() {
                                                @Override
                                                public void onResponse(Call<Response> call, retrofit2.Response<com.example.tureguideversion1.Notifications.Response> response) {
                                                    if (response.code() == 200) {
                                                        if (response.body().success != 1) {
                                                            Toasty.error(context, "Guide requesting failed. Please try again later.", Toasty.LENGTH_SHORT).show();
                                                        } else if (response.body().success == 1) {
                                                            guideMonitoring(typeID, position, guideID);
                                                        }
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<com.example.tureguideversion1.Notifications.Response> call, Throwable t) {
                                                }
                                            });
                                } else {
                                    Log.d(TAG, "onDataChange: not exist");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    Toasty.info(context, "Guides are not online right now. Please try again later.", Toasty.LENGTH_SHORT).show();
                    textView.setText("Tap to request a guide...");
                    anim.end();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void ShowProfilePopup(String member_name, String member_phone, String member_email, String member_image, String member_gender,
                                 long tour, long event, long ratingCounter, long rating, String eventID, String memberID, String forChat) {
        TextView namePopUp, phonePopUp, emailPopUp, tourPopUp, eventPopUp, ratingPopUp;

        DecimalFormat df = new DecimalFormat("0.0");
        double averageRating = 0;
        if(rating!=0){
            averageRating = rating / ratingCounter;
        }
        ImageView pic;
        Button callBTN, textBTN;
        CircleImageView close;
        Dialog profileDialog = new Dialog(context);
        profileDialog.setContentView(R.layout.profile_popup);
        close = profileDialog.findViewById(R.id.close);
        namePopUp = profileDialog.findViewById(R.id.namePopUp);
        phonePopUp = profileDialog.findViewById(R.id.phonePopUp);
        emailPopUp = profileDialog.findViewById(R.id.emailPopUp);
        eventPopUp = profileDialog.findViewById(R.id.eventPopUp);
        tourPopUp = profileDialog.findViewById(R.id.tourPopUp);
        ratingPopUp = profileDialog.findViewById(R.id.ratingPopUp);
        pic = profileDialog.findViewById(R.id.pic);
        callBTN = profileDialog.findViewById(R.id.callBTN);
        textBTN = profileDialog.findViewById(R.id.textBTN);
        try {
            GlideApp.with(profileDialog.getContext())
                    .load(getImageFromDrawable("ic_close"))
                    .centerCrop()
                    .into(close);
        } catch (Exception e) {
            e.printStackTrace();
        }

        callBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse("tel:" + member_phone));
                        context.startActivity(callIntent);
                    }
                };
                handler.postDelayed(runnable, 200);
            }
        });

        textBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if(forChat.matches("guide")) {
                            Intent chatIntent = new Intent(context, GuideChatBox.class);
                            chatIntent.putExtra("chatPartnerID", memberID);
                            chatIntent.putExtra("eventId", eventID);
                            context.startActivity(chatIntent);
                            ((FragmentActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }else if(forChat.matches("admin")) {
                            Intent chatIntent = new Intent(context, AdminChatBox.class);
                            chatIntent.putExtra("chatPartnerID", memberID);
                            chatIntent.putExtra("eventId", eventID);
                            context.startActivity(chatIntent);
                            ((FragmentActivity) context).overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    }
                };
                handler.postDelayed(runnable, 200);
            }
        });

        namePopUp.setText(member_name);
        phonePopUp.setText(member_phone);
        emailPopUp.setText(member_email);
        eventPopUp.setText(String.valueOf(event));
        tourPopUp.setText(String.valueOf(tour));
        ratingPopUp.setText(df.format(averageRating));
        if (!member_image.matches("")) {
            GlideApp.with(profileDialog.getContext())
                    .load(member_image)
                    .fitCenter()
                    .into(pic);
        } else {
            if (member_gender != null) {
                if (member_gender.matches("male")) {
                    try {
                        GlideApp.with(profileDialog.getContext())
                                .load(getImageFromDrawable("man"))
                                .fitCenter()
                                .into(pic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (member_gender.matches("female")) {
                    try {
                        GlideApp.with(profileDialog.getContext())
                                .load(getImageFromDrawable("woman"))
                                .fitCenter()
                                .into(pic);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    GlideApp.with(profileDialog.getContext())
                            .load(getImageFromDrawable("man"))
                            .fitCenter()
                            .into(pic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileDialog.dismiss();
            }
        });
        profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        profileDialog.setCanceledOnTouchOutside(false);
        profileDialog.show();
    }

    public int getImageFromDrawable(String imageName) {

        int drawableResourceId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());

        return drawableResourceId;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void startUpdate(ViewGroup container) {
        super.startUpdate(container);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        super.finishUpdate(container);
    }

    static class InternetCheck extends AsyncTask<Void, Void, Boolean> {

        private Consumer mConsumer;

        public interface Consumer {
            void accept(Boolean internet);
        }

        public InternetCheck(Consumer consumer) {
            mConsumer = consumer;
            execute();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean internet) {
            mConsumer.accept(internet);
        }
    }

}
