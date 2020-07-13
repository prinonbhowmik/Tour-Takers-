package com.example.tureguideversion1.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.request.RequestOptions;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.dd.processbutton.iml.ActionProcessButton;
import com.example.tureguideversion1.Activities.LocationImage;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.Activities.NoInternetConnection;
import com.example.tureguideversion1.Adapters.AutoCompleteLocationAdapter;
import com.example.tureguideversion1.Adapters.DailyForcastAdapter;
import com.example.tureguideversion1.ForApi.ApiInterFace;
import com.example.tureguideversion1.ForApi.ApiUtils;
import com.example.tureguideversion1.Internet.ConnectivityReceiver;
import com.example.tureguideversion1.LocationSelection_bottomSheet;
import com.example.tureguideversion1.Model.DailyForcastList;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.Model.LocationItem;
import com.example.tureguideversion1.Notifications.APIService;
import com.example.tureguideversion1.Notifications.Client;
import com.example.tureguideversion1.Notifications.Data;
import com.example.tureguideversion1.Notifications.Sender;
import com.example.tureguideversion1.Notifications.Token;
import com.example.tureguideversion1.R;
import com.example.tureguideversion1.Weather.WeatherResponse;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.polyak.iconswitch.IconSwitch;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TourFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    public static final String TAG = "TourFragment";
    private EditText startDateET, endDateET, eventTime, groupName_ET, eventCost_ET, meetingPlace_ET, eventDescription_ET, meetingPlaceWithGuide_ET;
    private SmartMaterialSpinner locationEt;
    private SliderLayout imageSlider;
    private ImageView logo, tour_nav_icon;
    private LottieAnimationView loading;
    private List<String> locationList;
    private List<String> selectedLocation, locationListForWeather;
    private ArrayList<String> location;
    private String locationForViewPage, districtForLocationImage, districtFromLocationSelection, format,
            userID, publishDate, s_date, r_date, time, place, description, meetPlace, meetingWithGuide, group_name, cost, locationDistrict;
    private Button locationSelection, createEvent;
    private int slide;
    View view;
    private IconSwitch iconSwitch;
    private Animation anim;
    private TextView tourTitle, weatherLocation;
    private LinearLayout eventLayout, dailyForcastLayout;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private static String eventId;
    private int joinMemberCount, scrollPosition;
    private navDrawerCheck check;
    private ScrollView tScrollView;
    private DrawerLayout tDrawerLayout;
    private ApiInterFace api;
    private String weatherAPI = "618e3a096dcd96b86ffa64b35ef140e1";
    private RecyclerView dailyRecycleView;
    private DailyForcastAdapter dailyForcastAdapter;
    private List<DailyForcastList> dailyForcastLists;
    private double latForMeetingPlace;
    private double lonForMeetingPlace;
    private double latForGuidePlace;
    private double lonForGuidePlace;
    private String subLocalityForMeetingPlace;
    private ActionProcessButton makeTour;
    private TextInputLayout meetingPlace, meetingPlaceWithGuide, startDate, returnDate, timeLayout;
    private APIService apiService;

    public TourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_tour, container, false);
        init(view);
        userID = auth.getUid();
        tDrawerLayout = getActivity().findViewById(R.id.drawer_layout);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tScrollView.post(new Runnable() {
                    public void run() {
                        if (eventLayout.getVisibility() == View.GONE) {
                            eventLayout.setTranslationY(view.getHeight());
                        } else if (eventLayout.getVisibility() == View.VISIBLE) {
                            eventLayout.setTranslationY(0);
                        }
                    }
                });
            }
        });
        tour_nav_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tDrawerLayout.openDrawer(GravityCompat.START);
                hideKeyboardFrom(view.getContext());
            }
        });

        initDistrictSpinner();

        startDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate();
            }
        });
        endDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEndDate();
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                    if (i1 > 0){
                        if(makeTour.getVisibility() == View.GONE) {
                            if(dailyForcastLayout.getVisibility() == View.GONE) {
                                makeTour.setTranslationY(-view.getHeight());
                            }
                        }
                    }
                }
            });
        }
        loading.setVisibility(View.INVISIBLE);
        iconSwitch.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (iconSwitch.getChecked()) {
                    case LEFT:
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                tourTitle.setText("Tour");
                            }
                        });
                        tourTitle.startAnimation(anim);
                        if (makeTour.getVisibility() == View.GONE) {
                            makeTour.setVisibility(View.VISIBLE);
                            makeTour.setAlpha(0.0f);
                            makeTour.animate()
                                    .translationY(0)
                                    .alpha(1.0f)
                                    .setDuration(300)
                                    .setListener(null);
                        }
                        if (eventLayout.getVisibility() == View.VISIBLE) {
                            eventLayout.animate()
                                    .translationY(view.getHeight())
                                    .alpha(0.0f)
                                    .setDuration(300)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            eventLayout.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        break;
                    case RIGHT:
                        anim.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {
                                tourTitle.setText("Event");
                            }
                        });
                        tourTitle.startAnimation(anim);
                        if (makeTour.getVisibility() == View.VISIBLE) {
                            //makeTour.setVisibility(View.GONE);
                            makeTour.setAlpha(0.0f);
                            makeTour.animate()
                                    .translationY(view.getHeight())
                                    .alpha(0.0f)
                                    .setDuration(300)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            super.onAnimationEnd(animation);
                                            makeTour.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        if (eventLayout.getVisibility() == View.GONE) {
                            eventLayout.setVisibility(View.VISIBLE);
                            eventLayout.setAlpha(0.0f);

                            // Start the animation
                            eventLayout.animate()
                                    .translationY(0)
                                    .alpha(1.0f)
                                    .setDuration(300)
                                    .setListener(null);
                        }
                        break;
                }
            }
        });

        eventTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickers();
            }
        });

        locationSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    LocationSelection_bottomSheet bottom_sheet = new LocationSelection_bottomSheet();
                    Bundle args = new Bundle();
                    args.putString("location", locationDistrict);
                    if (selectedLocation != null) {
                        args.putStringArrayList("selectedLocation", (ArrayList<String>) selectedLocation);
                    }
                    bottom_sheet.setArguments(args);
                    bottom_sheet.show(getParentFragmentManager(), "locationSelection");
                } else {
                    startActivity(new Intent(view.getContext(), NoInternetConnection.class));
                }
            }
        });

        makeTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s_date = startDateET.getText().toString();
                r_date = endDateET.getText().toString();
                meetingWithGuide = meetingPlaceWithGuide_ET.getText().toString();

                if(TextUtils.isEmpty(locationDistrict)){
                    Toasty.error(view.getContext(), "Select District!", Toasty.LENGTH_SHORT).show();
                    locationEt.setErrorText("Select District!");
                    locationEt.setErrorTextColor(Color.RED);
                }else if (selectedLocation.isEmpty()) {
                    locationSelection.setTextColor(Color.RED);
                    Toasty.error(view.getContext(), "Select Tourism Places!", Toasty.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(s_date)){
                    //Toasty.error(view.getContext(), "Pick Start Date!", Toasty.LENGTH_SHORT).show();
                    startDate.setError("Tap for pick date!");
                }else if(TextUtils.isEmpty(r_date)){
                    //Toasty.error(view.getContext(), "Pick Return Date!", Toasty.LENGTH_SHORT).show();
                    returnDate.setError("Tap for pick date!");
                }else if(TextUtils.isEmpty(meetingWithGuide)){
                    //Toasty.error(view.getContext(), "Pick location for meetup with guide!", Toasty.LENGTH_SHORT).show();
                    meetingPlaceWithGuide.setError(" Tap to pick location for meetup with guide!");
                }else {
                    Log.d(TAG, "onClick: "+s_date+r_date);
                    makeTour.setProgress(50);
                    DatabaseReference onlineRef = FirebaseDatabase.getInstance().getReference().child("guidesAreOnline").child(locationDistrict);
                    onlineRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                for(DataSnapshot childSnapShot: snapshot.getChildren()){
                                    HashMap<String, Object> onlineMap = (HashMap<String, Object>) childSnapShot.getValue();
                                    DatabaseReference guideRef = FirebaseDatabase.getInstance().getReference().child("GuideProfile").child((String) onlineMap.get("ID"));
                                    guideRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(snapshot.exists()) {
                                                HashMap<String, Object> guideMap = (HashMap<String, Object>) snapshot.getValue();
                                                Token token = snapshot.getValue(Token.class);
                                                Data data = new Data((String) guideMap.get("Id"),auth.getUid(),s_date,r_date,"request");
                                                Sender sender = new Sender(data, token.getToken());
                                                apiService.sendNotification(sender)
                                                        .enqueue(new Callback<com.example.tureguideversion1.Notifications.Response>() {
                                                            @Override
                                                            public void onResponse(Call<com.example.tureguideversion1.Notifications.Response> call, retrofit2.Response<com.example.tureguideversion1.Notifications.Response> response) {
                                                                if (response.code() == 200) {
                                                                    if (response.body().success != 1) {
                                                                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<com.example.tureguideversion1.Notifications.Response> call, Throwable t) {}
                                                        });
                                                //send(s_date,r_date,(String) guideMap.get("Id"),auth.getUid(),token.getToken());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkConnection()) {
                    DatabaseReference eventRef = databaseReference.child("event");
                    eventId = eventRef.push().getKey();

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh:mm a");
                    publishDate = simpleDateFormat.format(calendar.getTime());
                    s_date = startDateET.getText().toString();
                    r_date = endDateET.getText().toString();
                    time = eventTime.getText().toString();
                    if (locationDistrict != null) {
                        place = locationDistrict.substring(0, 1).toUpperCase() + locationDistrict.substring(1);
                    }
                    description = eventDescription_ET.getText().toString();
                    meetPlace = meetingPlace_ET.getText().toString();
                    group_name = groupName_ET.getText().toString();
                    cost = eventCost_ET.getText().toString();
                    joinMemberCount = 1;

                    if (TextUtils.isEmpty(place)) {
                        Toasty.error(view.getContext(), "Select District!", Toasty.LENGTH_SHORT).show();
                        locationEt.setErrorText("Select District!");
                        locationEt.setErrorTextColor(Color.RED);
                    } else if (selectedLocation.isEmpty()) {
                        locationSelection.setTextColor(Color.RED);
                        Toasty.error(view.getContext(), "Select Tourism Places!", Toasty.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(s_date)) {
                        Toasty.error(view.getContext(), "Pick Start Date!", Toasty.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(r_date)) {
                        Toasty.error(view.getContext(), "Pick Return Date!", Toasty.LENGTH_SHORT).show();
                    } else if (TextUtils.isEmpty(group_name)) {
                        Toasty.error(view.getContext(), "Enter Group Name!", Toasty.LENGTH_SHORT).show();
                        groupName_ET.requestFocus();
                    } else if (TextUtils.isEmpty(cost)) {
                        Toasty.error(view.getContext(), "Enter Total Cost!", Toasty.LENGTH_SHORT).show();
                        eventCost_ET.requestFocus();
                    } else if (TextUtils.isEmpty(time)) {
                        Toasty.error(view.getContext(), "Pick Journey Time!", Toasty.LENGTH_SHORT).show();
                        eventTime.requestFocus();
                    } else if (TextUtils.isEmpty(meetPlace)) {
                        Toasty.error(view.getContext(), "Enter Meeting Places!", Toasty.LENGTH_SHORT).show();
                        meetingPlace_ET.requestFocus();
                    } else if (TextUtils.isEmpty(description)) {
                        Toasty.error(view.getContext(), "Enter Description about tour!", Toasty.LENGTH_SHORT).show();
                        eventDescription_ET.requestFocus();
                    } else if ((latForMeetingPlace == 0) && (lonForMeetingPlace == 0) && (subLocalityForMeetingPlace.trim().length() == 0)) {
                        Toasty.error(view.getContext(), "Can't get your meeting location point! Please pick location again.", Toasty.LENGTH_SHORT).show();
                    } else {
                        if (!place.matches(districtFromLocationSelection)) {
                            place = districtFromLocationSelection;
                            addEventInDB(eventId, s_date, r_date, time, place, meetPlace, description, publishDate, joinMemberCount,
                                    userID, group_name, cost, latForMeetingPlace, lonForMeetingPlace, subLocalityForMeetingPlace);
                        } else if (place.matches(districtFromLocationSelection)) {
                            addEventInDB(eventId, s_date, r_date, time, place, meetPlace, description, publishDate, joinMemberCount,
                                    userID, group_name, cost, latForMeetingPlace, lonForMeetingPlace, subLocalityForMeetingPlace);
                        } else {
                            Toasty.error(view.getContext(), "Location mismatching!", Toasty.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    startActivity(new Intent(view.getContext(), NoInternetConnection.class));
                }
            }
        });

        meetingPlaceWithGuide_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(locationDistrict == null) {
                    Toasty.error(getContext(),"Select district first!",Toasty.LENGTH_SHORT).show();
                }else {
                    meetingPlaceWithGuide.setHint("Opening map...");
                    Intent intent = new Intent(view.getContext(), com.example.tureguideversion1.Activities.Map.class)
                            .putExtra("from", "tour")
                            .putExtra("for", "guidePlace")
                            .putExtra("latForMeetingPlace", latForGuidePlace)
                            .putExtra("lonForMeetingPlace", lonForGuidePlace)
                            .putExtra("guideLocation", locationDistrict);
                    startActivityForResult(intent, 1);
                }
            }
        });

        meetingPlace_ET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                meetingPlace.setHint("Opening map...");
                Intent intent = new Intent(view.getContext(), com.example.tureguideversion1.Activities.Map.class)
                        .putExtra("from", "tour")
                        .putExtra("for", "meetingPlace")
                        .putExtra("meetingPlace",meetingPlace_ET.getText().toString())
                        .putExtra("latForMeetingPlace",latForMeetingPlace)
                        .putExtra("lonForMeetingPlace",lonForMeetingPlace);
                startActivityForResult(intent,1);
            }
        });

        return view;
    }

    private void send(String startDate, String returnDate, String sented, String userID, String token) {

        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;
        JSONObject android = null;
        JSONObject apns = null;
        JSONObject apnsh = null;
        JSONObject webPush = null;
        JSONObject webPushH = null;

        try {

            obj = new JSONObject();
            objData = new JSONObject();
            dataobjData = new JSONObject();
            android = new JSONObject();
            apns = new JSONObject();
            apnsh = new JSONObject();
            webPush = new JSONObject();
            webPushH = new JSONObject();
            dataobjData.put("startDate", startDate);
            dataobjData.put("returnDate", returnDate);
            dataobjData.put("sented", sented);
            dataobjData.put("userID", userID);

            objData.put("content_available","true");
            objData.put("priority", "high");

            android.put("priority","high");
            apnsh.put("apns-priority","10");
            apns.put("headers",apnsh);
            webPushH.put("Urgency","high");
            webPush.put("headers",webPushH);

            obj.put("to", token);
            obj.put("notification", objData);
            obj.put("data", dataobjData);
            obj.put("android", android);

            Log.e("MYOBJs", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SUCCESS", response + "");
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("ERRORS", error + "");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAgbNQHYY:APA91bGdhyJtQep7JFxDZYxDu3VF0-bV1H_lSLX4Lyvek8QViU0Y0N_x42ftIf85luF0UEQEidMJj0vJBD7MqniuDWMOZzK2SjgQswp4RmFCse38wbx3nbJ0WguBbQ-SYXmi-O1r1bcY");
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);
    }

    private void initDistrictSpinner() {
        DatabaseReference districtRef = FirebaseDatabase.getInstance().getReference().child("location");
        districtRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                locationList = new ArrayList<>();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        //Log.d(TAG, "onDataChange: "+snapshot.getKey());
                        locationList.add(snapshot.getKey().substring(0,1).toUpperCase()+snapshot.getKey().substring(1));
                    }
                    locationEt.setItem(locationList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        locationEt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                locationDistrict = locationList.get(position);
                locationEt.setErrorText("District is Selected");
                locationEt.setErrorTextColor(getResources().getColor(R.color.colorGreen));

                if (checkConnection()) {
                    // the user is done typing.
                    if (locationDistrict.trim().length() != 0) {
                        selectedLocation.clear();
                        latForGuidePlace = 0;
                        lonForGuidePlace = 0;
                        meetingPlaceWithGuide_ET.setText("");
                        getForcast();
                        logo.setVisibility(View.INVISIBLE);
                        loading.setVisibility(View.VISIBLE);
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(locationDistrict.toLowerCase());
                        ref.addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        //Get map of users in datasnapshot
                                        //loading.setVisibility(View.VISIBLE);
                                        //locationForViewPage = locationDistrict;
                                        collectImageNInfo((Map<String, Object>) dataSnapshot.getValue());
                                        locationForViewPage = districtForLocationImage;
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        //handle databaseError
                                    }
                                });
                    }
                } else {
                    startActivity(new Intent(view.getContext(), NoInternetConnection.class));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void receivedLocationData(String pickedLocation) {
        if (pickedLocation.substring(0, 2).matches("un")) {
            String s = pickedLocation.substring(2);
            selectedLocation.remove(s);

        } else {
            if (!selectedLocation.contains(pickedLocation)) {
                selectedLocation.add(pickedLocation);
            }
        }
        String count = Integer.toString(selectedLocation.size());
        if (selectedLocation.size() == 1) {
            locationSelection.setText(count + " tourism place is selected");
            locationSelection.setTextColor(getResources().getColor(R.color.colorGreen));
        } else if (selectedLocation.size() > 1) {
            locationSelection.setTextColor(getResources().getColor(R.color.colorGreen));
            locationSelection.setText(count + " tourism places is selected");
        } else {
            locationSelection.setText("Select tourism places");
            locationSelection.setTextColor(getResources().getColor(R.color.colorYellow));
        }
    }

    public void district(String d) {
        districtFromLocationSelection = d.substring(0, 1).toUpperCase() + d.substring(1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            meetingPlace.setHint("Meetup with Members");
            meetingPlaceWithGuide.setHint("Meetup with Guide");
            meetingPlaceWithGuide.setErrorEnabled(false);
            if (resultCode == RESULT_OK) {
                slide = data.getIntExtra("slide", 0);
                loading.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.INVISIBLE);
                imageSlider.setVisibility(View.VISIBLE);
                try {
                    if (imageSlider.getCurrentPosition() < slide) {
                        if (slide - imageSlider.getCurrentPosition() > 1) {

                        } else {
                            imageSlider.setCurrentPosition(slide, false);
                        }
                    } else if (imageSlider.getCurrentPosition() > slide) {
                        if (imageSlider.getCurrentPosition() - slide > 1) {

                        } else {
                            imageSlider.setCurrentPosition(slide, false);
                        }
                    }
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                }
            }else if(resultCode == 2){
                if(data.getStringExtra("for").matches("meetingPlace")) {
                    String location = data.getStringExtra("location");
                    latForMeetingPlace = data.getDoubleExtra("latForMeetingPlace", 0);
                    lonForMeetingPlace = data.getDoubleExtra("lonForMeetingPlace", 0);
                    subLocalityForMeetingPlace = data.getStringExtra("subLocality");
                    meetingPlace_ET.setText(location);
                }else if(data.getStringExtra("for").matches("guidePlace")) {
                    String location = data.getStringExtra("location");
                    latForGuidePlace = data.getDoubleExtra("latForMeetingPlace", 0);
                    lonForGuidePlace = data.getDoubleExtra("lonForMeetingPlace", 0);
                    meetingPlaceWithGuide_ET.setText(location);
                }
            }
        }
    }

    private void collectImageNInfo(Map<String, Object> locations) {

        location = new ArrayList<>();
        ArrayList<String> image = new ArrayList<>();
        //ArrayList<String> description = new ArrayList<>();

        //iterate through each user, ignoring their UID
        if (locations != null) {
            for (Map.Entry<String, Object> entry : locations.entrySet()) {

                //Get user map
                Map singleUser = (Map) entry.getValue();
                //Get phone field and append to list
                location.add((String) singleUser.get("locationName"));
                image.add((String) singleUser.get("image"));
                //description.add((String) singleUser.get("description"));
            }

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.centerCrop();
            //.diskCacheStrategy(DiskCacheStrategy.NONE)
            //.placeholder(R.drawable.placeholder)
            //.error(R.drawable.placeholder);
            imageSlider.removeAllSliders();

            for (int i = 0; i < image.size(); i++) {
                TextSliderView sliderView = new TextSliderView(view.getContext());
                // if you want show image only / without description text use DefaultSliderView instead

                // initialize SliderLayout
                sliderView
                        .image(image.get(i))
                        .description(location.get(i))
                        .setRequestOption(requestOptions)
                        .setProgressBarVisible(false)
                        .setOnSliderClickListener(this);

                //add your extra information
                sliderView.bundle(new Bundle());
                sliderView.getBundle().putString("extra", location.get(i));
                imageSlider.addSlider(sliderView);
                //loading.setVisibility(View.INVISIBLE);
            }
            //locationSelection.setVisibility(View.VISIBLE);
            if (locationSelection.getVisibility() == View.GONE) {
                locationSelection.setVisibility(View.VISIBLE);
                locationSelection.setAlpha(0.0f);

                // Start the animation
                locationSelection.animate()
                        .translationY(0)
                        .alpha(1.0f)
                        .setDuration(200)
                        .setListener(null);
            }
            // set Slider Transition Animation
            districtForLocationImage = locationDistrict;
            selectedLocation.clear();
            locationSelection.setText("Select tourism places");
            locationSelection.setTextColor(getResources().getColor(R.color.colorYellow));
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
                imageSlider.addOnPageChangeListener(this);
                imageSlider.stopCyclingWhenTouch(true);

            }
        } else {
            logo.setVisibility(View.VISIBLE);
            loading.setVisibility(View.INVISIBLE);
            Toasty.warning(getContext(), "Tour not available on this location!", Toasty.LENGTH_SHORT).show();
        }

    }

    private void getEndDate() {
        Toasty.info(getContext(), "Currently we are support tour within 8 days!", Toasty.LENGTH_SHORT).show();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String currentDate = day + "/" + month + "/" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                returnDate.setErrorEnabled(false);
                endDateET.setText(dateFormat.format(date));
                long milisec = date.getTime();
                getForcast();

            }
        };

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), dateSetListener, year, month, day);
        try {
            Date stDate2 = sdf2.parse(startDateET.getText().toString());
            //calendar.setTime(stDate);
            datePickerDialog.getDatePicker().setMinDate(stDate2.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (tourTitle.getText().toString().matches("Tour")) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        }
        datePickerDialog.show();
    }

    private void addEventInDB(String id, String startDate, String returnDate, String time, String place, String meetPlace, String description,
                              String publishDate, int joinMemberCount, String eventPublisherId, String group_name, String cost, double latForMeetingPlace, double lonForMeetingPlace, String subLocalityForMeetingPlace) {
        final DatabaseReference eventRef = databaseReference.child("event");

        final Event event = new Event(id, startDate, returnDate, time, place, meetPlace, description, publishDate, joinMemberCount,
                eventPublisherId, group_name, cost, latForMeetingPlace, lonForMeetingPlace, subLocalityForMeetingPlace);

        eventRef.child(eventId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toasty.success(getContext(), "Your Event Successfully Added", Toasty.LENGTH_SHORT).show();
                    addJoinMember();
                    addEventLoacationList();
                    setUserActivity(eventId);
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                        @Override
                        public void onSuccess(InstanceIdResult instanceIdResult) {
                            setToken(instanceIdResult.getToken(),eventId);
                            enableNotification(eventId);
                        }
                    });
                    FragmentTransaction event = getParentFragmentManager().beginTransaction();
                    event.replace(R.id.fragment_container, new EventFragment());
                    event.commit();

                    check.checked(1);
                    //navigationView.getMenu().getItem(2).setChecked(true);
                } else {
                    Toasty.error(getContext(), "Unsuccessful", Toasty.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setToken(String token, String eventID) {
        DatabaseReference ref = databaseReference.child("eventCommentsTokens");
        Token token1 = new Token(token);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userID", auth.getUid());
        hashMap.put("token", token1.getToken());
        ref.child(eventID).child(auth.getUid()).setValue(hashMap);
    }

    private void enableNotification(String eventID){
        DatabaseReference ref = databaseReference.child("notificationStatus").child("eventCommentNotifiaction").child(eventID);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("ID", auth.getUid());
        hashMap.put("status", "enabled");
        ref.child(auth.getUid()).setValue(hashMap);
    }

    private void addJoinMember() {
        DatabaseReference memberRef = databaseReference.child("eventJoinMember").child(eventId).child(userID);
        memberRef.child("id").setValue(userID);
    }



    private void addEventLoacationList() {
        for (int i = 0; i < selectedLocation.size(); i++) {
            DatabaseReference memberRef = databaseReference.child("eventLocationList").child(eventId).child(selectedLocation.get(i));
            memberRef.child("locationName").setValue(selectedLocation.get(i));
        }

    }

    private void init(View view) {
        locationEt = view.findViewById(R.id.location_Et);
        startDateET = view.findViewById(R.id.startDate_Et);
        startDate = view.findViewById(R.id.date1_TIL);
        endDateET = view.findViewById(R.id.endDate_Et);
        returnDate = view.findViewById(R.id.date2_TIL);
        timeLayout = view.findViewById(R.id.eventTime);
        imageSlider = view.findViewById(R.id.slider);
        logo = view.findViewById(R.id.logoT);
        loading = view.findViewById(R.id.loading);
        locationSelection = view.findViewById(R.id.locationSelection);
        locationSelection.setTranslationY(-150);
        selectedLocation = new ArrayList<>();
        iconSwitch = view.findViewById(R.id.tourSwitch);
        tourTitle = view.findViewById(R.id.tourTitle);
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(200);
        anim.setRepeatCount(1);
        anim.setRepeatMode(Animation.REVERSE);
        eventLayout = view.findViewById(R.id.eventLayout);
        eventTime = view.findViewById(R.id.eventTime_ET);
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        createEvent = view.findViewById(R.id.createEvent);
        groupName_ET = view.findViewById(R.id.groupName_ET);
        eventCost_ET = view.findViewById(R.id.eventCost_ET);
        meetingPlaceWithGuide_ET = view.findViewById(R.id.meetingPlaceWithGuide_ET);
        meetingPlace_ET = view.findViewById(R.id.meetingPlace_ET);
        eventDescription_ET = view.findViewById(R.id.eventDescription_ET);
        tScrollView = view.findViewById(R.id.tScrollView);
        tour_nav_icon = view.findViewById(R.id.nav_icon);
        api = ApiUtils.getUserService();
        dailyForcastLists = new ArrayList<>();
        dailyRecycleView = view.findViewById(R.id.dailyForcastRecycleView);
        dailyForcastAdapter = new DailyForcastAdapter(dailyForcastLists, getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dailyRecycleView.setLayoutManager(layoutManager);
        dailyRecycleView.setAdapter(dailyForcastAdapter);
        weatherLocation = view.findViewById(R.id.weatherLocation);
        dailyForcastLayout = view.findViewById(R.id.dailyForcastLayout);
        locationListForWeather = new ArrayList<>();
        makeTour = view.findViewById(R.id.makeTourBTN);
        makeTour.setMode(ActionProcessButton.Mode.ENDLESS);
        meetingPlace = view.findViewById(R.id.meetingPlace);
        meetingPlaceWithGuide = view.findViewById(R.id.meetingPlaceWithGuide);
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    private void getDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String currentDate = day + "/" + month + "/" + year;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startDate.setErrorEnabled(false);
                startDateET.setText(dateFormat.format(date));
                long milisec = date.getTime();
                getForcast();
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        if (tourTitle.getText().toString().matches("Tour")) {
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        }
        datePickerDialog.show();
    }

    @Override
    public void onResume() {
        imageSlider.startAutoCycle();
//        Bundle mArgs = this.getArguments();
//        if(mArgs != null) {
//            String strtext = mArgs.getString("locationPoint");
//            meetingPlace_ET.setText(strtext);
//            Toast.makeText(getContext(), strtext, Toast.LENGTH_SHORT).show();
//        }
        super.onResume();
    }

    @Override
    public void onStop() {
        // To prevent a memory leak on rotation, make sure to call stopAutoCycle() on the slider before activity or fragment is destroyed
        imageSlider.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        MainActivity main = new MainActivity();
        if (main.checkConnection()) {
            //Toast.makeText(getActivity(), slider.getBundle().getString("extra") + "", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(view.getContext(), LocationImage.class)
                    .putExtra("slide", slider.getBundle().getString("extra"))
                    .putExtra("location", locationForViewPage);
            startActivityForResult(i, 1);
            //startActivity(new Intent(getContext(), LocationImage.class).putExtra("slide",slider.getBundle().getString("extra")));
        } else {
            startActivity(new Intent(getContext(), NoInternetConnection.class));
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private void timePickers() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                if (selectedHour == 0) {
                    selectedHour += 12;
                    format = "AM";
                } else if (selectedHour == 12) {
                    format = "PM";
                } else if (selectedHour > 12) {
                    selectedHour -= 12;
                    format = "PM";
                } else {
                    format = "AM";
                }

                if (selectedMinute < 10) {
                    eventTime.setText(selectedHour + ":0" + selectedMinute + " " + format);
                } else {
                    eventTime.setText(selectedHour + ":" + selectedMinute + " " + format);
                }
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.show();

    }

    public boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }

    private static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public interface navDrawerCheck {
        void checked(int value);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            check = (navDrawerCheck) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement BottomSheetListener");
        }
    }

    private void hideKeyboardFrom(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getRootView().getWindowToken(), 0);
    }

    private void getForcast() {
        if (startDateET.getText().toString().trim().length() != 0 && endDateET.getText().toString().trim().length() != 0) {
            try {
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                String c1 = sdf1.format(c);
                Date currentDate = sdf1.parse(c1);
                Date endDate1 = sdf1.parse(endDateET.getText().toString());
                long diff = endDate1.getTime() - currentDate.getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) + 1;
                if (days <= 8) {
                    if (locationDistrict != null) {
                            dailyForcastLayout.setVisibility(View.VISIBLE);
                            dailyForcastLists.clear();
                            Geocoder coder = new Geocoder(getContext());
                            List<Address> latlonaddress = null;
                            List<Address> address = null;
                            try {
                                latlonaddress = coder.getFromLocationName(locationDistrict, 5);
                                if (latlonaddress != null) {
                                    Address location = latlonaddress.get(0);
                                    Call<WeatherResponse> call = api.getWeather(location.getLatitude(), location.getLongitude(), "metric", weatherAPI);
                                    call.enqueue(new Callback<WeatherResponse>() {
                                        @Override
                                        public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                                            if (response.isSuccessful()) {
                                                if (response.body() != null) {
                                                    WeatherResponse weatherResponse = response.body();
                                                    String address = "";
                                                    Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());

                                                    List<Address> addresses;

                                                    try {
                                                        addresses = geocoder.getFromLocation(weatherResponse.lat, weatherResponse.lon, 1);
                                                        if (addresses.size() > 0) {
                                                            address = addresses.get(0).getLocality();
                                                            if (address == null) {
                                                                weatherLocation.setText(locationDistrict);
                                                            } else {
                                                                weatherLocation.setText(address);
                                                            }
                                                        }


                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    for (int i = 0; i < 8; i++) {
                                                        String dailyDate = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(weatherResponse.dailyForcasts.get(i).dt * 1000));
                                                        try {
                                                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                                            Date forcastDate = sdf.parse(dailyDate);
                                                            Date startDate = sdf.parse(startDateET.getText().toString());
                                                            Date endDate = sdf.parse(endDateET.getText().toString());

                                                            if (forcastDate.compareTo(startDate) >= 0 && forcastDate.compareTo(endDate) <= 0) {
                                                                DailyForcastList forcastList = new DailyForcastList(weatherResponse.dailyForcasts.get(i).dailyWeatherForcasts.get(0).icon,
                                                                        weatherResponse.dailyForcasts.get(i).dailyTemps.min,
                                                                        weatherResponse.dailyForcasts.get(i).dailyTemps.max,
                                                                        weatherResponse.dailyForcasts.get(i).dailyWeatherForcasts.get(0).description,
                                                                        weatherResponse.dailyForcasts.get(i).dt,
                                                                        weatherResponse.dailyForcasts.get(i).sunrise,
                                                                        weatherResponse.dailyForcasts.get(i).sunset,
                                                                        weatherResponse.dailyForcasts.get(i).humidity,
                                                                        weatherResponse.dailyForcasts.get(i).wind_speed,
                                                                        weatherResponse.dailyForcasts.get(i).clouds,
                                                                        weatherResponse.dailyForcasts.get(i).dew_point);
                                                                dailyForcastLists.add(forcastList);
                                                                dailyForcastAdapter.notifyDataSetChanged();
                                                            }

                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<WeatherResponse> call, Throwable t) {

                                        }
                                    });
                                }
                            } catch (Exception e) {
                                dailyForcastLayout.setVisibility(View.GONE);
                                e.printStackTrace();
                                Log.e("Error", "grpc failed: " + e.getMessage(), e);
                            }
                    }
                }
            } catch (ParseException e) {
                dailyForcastLayout.setVisibility(View.GONE);
                e.printStackTrace();
                Log.e("Error", "grpc failed: " + e.getMessage(), e);
            }
        }
    }

    private void setUserActivity(String eventID) {
        DatabaseReference userActivityRef = databaseReference
                .child("userActivities")
                .child(auth.getUid())
                .child("events");
        String id = userActivityRef.push().getKey();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("eventID", eventID);
        userActivityRef.child(id).setValue(hashMap);
    }

}
