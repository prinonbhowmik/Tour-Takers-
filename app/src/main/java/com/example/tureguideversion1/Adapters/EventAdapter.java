package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.example.tureguideversion1.Activities.EventDetails;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    private List<Event> eventList;
    private Context context;
    private String member_id, member_name, member_image, member_phone;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    public EventAdapter(List<Event> eventList, Context context) {
        this.eventList = eventList;
        this.context = context;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_layout_design, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Event event = eventList.get(position);
        //Toast.makeText(context,event.getId(),Toast.LENGTH_SHORT).show();
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("eventLocationList").child(event.getId());
        ref1.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Map<String, Object> locationList = (Map<String, Object>) dataSnapshot.getValue();
                        if (locationList != null) {
                            //holder.locationWillBeVisit.clear();
                            for (Map.Entry<String, Object> entry : locationList.entrySet()) {
                                //Get user map
                                Map singleUser = (Map) entry.getValue();
                                //Get location field and append to list
                                holder.locationWillBeVisit.add((String) singleUser.get("locationName"));
                            }
                        }

                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(event.getPlace().toLowerCase());
                        ref.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Get map of users in datasnapshot
                                        Map<String, Object> collectImageNInfo = (Map<String, Object>) dataSnapshot.getValue();
                                        ArrayList<String> location = new ArrayList<>();
                                        ArrayList<String> image = new ArrayList<>();
                                        if (collectImageNInfo != null) {
                                            for (Map.Entry<String, Object> entry : collectImageNInfo.entrySet()) {
                                                //Get user map
                                                Map singleUser = (Map) entry.getValue();
                                                //Get phone field and append to list
                                                if (holder.locationWillBeVisit.contains(singleUser.get("locationName").toString())) {
                                                    location.add((String) singleUser.get("locationName"));
                                                    image.add((String) singleUser.get("image"));
                                                }
                                            }

                                            RequestOptions requestOptions = new RequestOptions();
                                            requestOptions.centerCrop();
                                            //.diskCacheStrategy(DiskCacheStrategy.NONE);
                                            //.placeholder(R.drawable.placeholder)
                                            //.error(R.drawable.placeholder);
                                            holder.imageSlider.removeAllSliders();

                                            for (int i = 0; i < image.size(); i++) {
                                                TextSliderView sliderView = new TextSliderView(context);
                                                // initialize SliderLayout
                                                sliderView
                                                        .image(image.get(i))
                                                        .description(location.get(i))
                                                        .setRequestOption(requestOptions)
                                                        .setProgressBarVisible(false);

                                                holder.imageSlider.addSlider(sliderView);
                                            }
                                            // set Slider Transition Animation
                                            if (holder.imageSlider.getSliderImageCount() < 2) {
                                                holder.imageSlider.stopAutoCycle();
                                                holder.imageSlider.setPagerTransformer(false, new BaseTransformer() {
                                                    @Override
                                                    protected void onTransform(View view, float v) {
                                                    }
                                                });
                                                holder.imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                                            } else {
                                                holder.imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
                                                holder.imageSlider.startAutoCycle();
                                                holder.imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Top);
                                                holder.imageSlider.setCustomAnimation(new DescriptionAnimation());
                                                holder.imageSlider.setDuration(4000);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        //handle databaseError
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        holder.eTitle.setText(event.getPlace());
        holder.eDate.setText(event.getStartDate());
        holder.rDate.setText(event.getReturnDate());
        holder.eTime.setText(event.getTime());
        holder.ePlace.setText(event.getMeetPlace());
        holder.eMembers.setText(String.valueOf(event.getJoinMemberCount()));
        holder.eCost.setText(event.getCost());
        holder.eGroupName.setText(event.getGroupName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EventDetails.class);
                intent.putExtra("event_place", event.getPlace());
                intent.putExtra("event_start_date", event.getStartDate());
                intent.putExtra("event_return_date", event.getReturnDate());
                intent.putExtra("event_time", event.getTime());
                intent.putExtra("event_description", event.getDescription());
                intent.putExtra("event_publish_date", event.getPublishDate());
                intent.putExtra("event_join_member_count", String.valueOf(event.getJoinMemberCount()));
                intent.putExtra("event_meeting_place", event.getMeetPlace());
                intent.putExtra("group_name", event.getGroupName());
                intent.putExtra("cost", event.getCost());
                intent.putExtra("event_id", event.getId());
                intent.putExtra("member_id", event.getEventPublisherId());
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView eTitle, eDate, rDate, eTime, ePlace, eMembers, eGroupName, eCost, eMemberid;
        private SliderLayout imageSlider;
        private List<String> locationWillBeVisit;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eTitle = itemView.findViewById(R.id.e_title);
            eDate = itemView.findViewById(R.id.e_start_date);
            rDate = itemView.findViewById(R.id.e_return_date);
            eTime = itemView.findViewById(R.id.e_time);
            ePlace = itemView.findViewById(R.id.e_place);
            eMembers = itemView.findViewById(R.id.e_member);
            eMemberid = itemView.findViewById(R.id.member_id);
            eGroupName = itemView.findViewById(R.id.e_group);
            eCost = itemView.findViewById(R.id.e_cost);
            imageSlider = itemView.findViewById(R.id.sliderFromEvent);
            locationWillBeVisit = new ArrayList<>();

        }
    }
}
