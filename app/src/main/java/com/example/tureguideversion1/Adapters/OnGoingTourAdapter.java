package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestOptions;
import com.example.tureguideversion1.Activities.LocationImage;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.Model.EventLocationList;
import com.example.tureguideversion1.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OnGoingTourAdapter extends PagerAdapter {
    public static final String TAG = "OnGoingTourAdapter";
    private List<Event> data;
    private Context context;
    private List<EventLocationList> locationLists;
    private EventLocationListAdapter locationAdapter;
    private View itemView;
    public OnGoingTourAdapter(List<Event> data, Context context) {
        this.data = data;
        this.context = context;
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
        Event event = data.get(position);
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        itemView = mLayoutInflater.inflate(R.layout.fragment_event_dashboard,container,false);
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
        TextView event_placeTV,event_publish_dateTV,start_dateTV,return_dateTV,event_timeTV,meeting_placeTV,group_nameTV,event_descriptionTV,
                event_costTV,event_publish_nameTV;
        RecyclerView locationRecycleView;
        SliderLayout imageSlider;

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
        event_publish_nameTV = itemView.findViewById(R.id.event_publish_nameTV);

        DatabaseReference locationListRef = FirebaseDatabase.getInstance().getReference().child("eventLocationList").child(event.getId());
        locationListRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    locationLists = new ArrayList<>();
                    for(DataSnapshot childSnap:snapshot.getChildren()) {
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
                            if(snapshot.exists()){
                                for(DataSnapshot childSnap2: snapshot.getChildren()){
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

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("profile").child(event.getEventPublisherId());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<String,Object> map = (HashMap<String, Object>) snapshot.getValue();
                    event_publish_nameTV.setText((String) map.get("name"));
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
        event_timeTV.setText(event.getTime());
        meeting_placeTV.setText(event.getMeetPlace());
        group_nameTV.setText(event.getGroupName());
        event_descriptionTV.setText(event.getDescription());
        event_costTV.setText(event.getCost());


        container.addView(itemView);

        return itemView;
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
}
