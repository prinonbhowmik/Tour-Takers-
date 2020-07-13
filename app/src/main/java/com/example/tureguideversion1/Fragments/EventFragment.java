package com.example.tureguideversion1.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Adapters.EventAdapter;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private EditText eventSearch;
    private TextView moreTv;
    private DatabaseReference databaseReference;
    private RecyclerView eventRecyclerview;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    private String userId;
    private FirebaseAuth auth;
    private DrawerLayout mdrawrelayout;
    private ImageView event_nav_icon, searchAction;
    private int d,g;
    private RadioRealButtonGroup radioGroup;
    private LinearLayout radioLayout;
    private Animation anim;
    View view;

    public EventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_event, container, false);
        mdrawrelayout = getActivity().findViewById(R.id.drawer_layout);
        //registerForContextMenu(moreTv);
        init(view);
        userId = auth.getCurrentUser().getUid();
        moreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });

        event_nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdrawrelayout.openDrawer(GravityCompat.START);
                hideKeyboardFrom(view.getContext());
            }
        });

        eventSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventSearch.setCursorVisible(true);
                if(radioLayout.getVisibility() == View.GONE) {
                    radioLayout.setVisibility(View.VISIBLE);
                    radioLayout.setAlpha(0.0f);

                    // Start the animation
                    radioLayout.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(200)
                            .setListener(null);
                }
            }
        });

        getData(view);

        searchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(radioLayout.getVisibility() == View.GONE) {
                radioLayout.setVisibility(View.VISIBLE);
                radioLayout.setAlpha(0.0f);

                // Start the animation
                radioLayout.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(null);
            }else if(radioLayout.getVisibility() == View.VISIBLE) {
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

        eventSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(radioLayout.getVisibility() == View.GONE) {
                    radioLayout.setVisibility(View.VISIBLE);
                    radioLayout.setAlpha(0.0f);

                    // Start the animation
                    radioLayout.animate()
                            .translationY(0)
                            .alpha(1.0f)
                            .setDuration(200)
                            .setListener(null);
                }
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!eventSearch.getText().toString().isEmpty()) {
                    search(eventSearch.getText().toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(eventSearch.getText().toString().isEmpty()) {
                    if(radioLayout.getVisibility() == View.VISIBLE) {
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
                    getData(view);
                }
            }
        });

        radioGroup.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                if(position == 0){
                    d = 1;
                    g = 0;
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            eventSearch.setHint("District");
                        }
                    });
                    eventSearch.startAnimation(anim);
                    if(!eventSearch.getText().toString().isEmpty()) {
                        search(eventSearch.getText().toString());
                    }
                }else if(position == 1){
                    d = 0;
                    g = 1;
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                            eventSearch.setHint("Group Name");
                        }
                    });
                    eventSearch.startAnimation(anim);
                    if(!eventSearch.getText().toString().isEmpty()) {
                        search(eventSearch.getText().toString());
                    }
                }
                //Toast.makeText(getContext(), "Clicked! Position: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showPopupMenu() {
        PopupMenu popup = new PopupMenu(getContext(), moreTv);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.event_menu);
        popup.show();

    }

    private void getData(View view) {
        DatabaseReference eventInfoRef = databaseReference.child("event");
        eventInfoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {


                    eventList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String date1 = (String) data.child("returnDate").getValue().toString();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        Date eDate = null;
                        try {
                            eDate = dateFormat.parse(date1);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (System.currentTimeMillis() <= eDate.getTime()) {
                            Event event = data.getValue(Event.class);
                            eventList.add(event);
                            
                        }

                    }
                    Collections.reverse(eventList);
                    eventAdapter.notifyDataSetChanged();

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });


    }

    private void search(final String value){
        if(!value.isEmpty()) {
            if(d == 1){
                String s = value.substring(0, 1).toUpperCase() + value.substring(1);
                DatabaseReference eventInfoRef = databaseReference.child("event");
                eventInfoRef.orderByChild("place").startAt(s).endAt(s+"\uf8ff").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            eventList.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                Event event = data.getValue(Event.class);
                                eventList.add(event);

                            }
                            Collections.reverse(eventList);
                            eventAdapter.notifyDataSetChanged();
                        }else {
                            eventList.clear();
                            eventAdapter.notifyDataSetChanged();
                            //Toasty.info(view.getContext(),"No events found!",Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else if(g == 1){
                DatabaseReference eventInfoRef = databaseReference.child("event");
                eventInfoRef.orderByChild("groupName").startAt(value).endAt(value+"\uf8ff").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            eventList.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {

                                Event event = data.getValue(Event.class);
                                eventList.add(event);

                            }
                            Collections.reverse(eventList);
                            eventAdapter.notifyDataSetChanged();
                        }else {
                            eventList.clear();
                            eventAdapter.notifyDataSetChanged();
                            //Toasty.info(view.getContext(),"No events found!",Toasty.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }else {
                Toasty.info(view.getContext(),"Select search option!",Toasty.LENGTH_SHORT).show();
            }



        }
    }

    private void init(View view) {
        eventSearch=view.findViewById(R.id.event_searchET);
        moreTv = view.findViewById(R.id.moreTV);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        eventList = new ArrayList<>();
        eventRecyclerview = view.findViewById(R.id.event_recyclerview);
        eventRecyclerview.setLayoutManager(new LinearLayoutManager(view.getContext()));
        eventAdapter = new EventAdapter(eventList, getContext());
        eventRecyclerview.setAdapter(eventAdapter);
        auth = FirebaseAuth.getInstance();
        event_nav_icon = view.findViewById(R.id.event_nav_icon);
        //mdrawrelayout=view.findViewById(R.id.nav_view);
        searchAction = view.findViewById(R.id.searchIV);
        radioGroup = view.findViewById(R.id.radioGroup);
        radioLayout = view.findViewById(R.id.radioLayout);
        d = 1;
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        radioLayout.setTranslationY(-150);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.my_event:
                DatabaseReference eventInfoRef = databaseReference.child("event");
                eventInfoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            eventList.clear();
                            for (DataSnapshot data : dataSnapshot.getChildren()) {
                                Map values = (Map) data.getValue();
                                if(values.get("eventPublisherId").toString().matches(userId)) {
                                    Event event = data.getValue(Event.class);
                                    eventList.add(event);
                                    //eventAdapter.notifyDataSetChanged();
                                }
                            }
                            Collections.reverse(eventList);
                            eventAdapter.notifyDataSetChanged();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                Toasty.info(view.getContext(), "My Event", Toasty.LENGTH_SHORT).show();
                return false;
            case R.id.all_event:
                getData(view);
                Toasty.info(view.getContext(), "All Event", Toasty.LENGTH_SHORT).show();

            default:
                return false;
        }
    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }
}
