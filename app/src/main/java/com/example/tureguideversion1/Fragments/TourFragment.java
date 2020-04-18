package com.example.tureguideversion1.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.RequestOptions;
import com.example.tureguideversion1.Activities.LocationImage;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.Activities.NoInternetConnection;
import com.example.tureguideversion1.Adapters.AutoCompleteLocationAdapter;
import com.example.tureguideversion1.LocationSelection_bottomSheet;
import com.example.tureguideversion1.Model.Event;
import com.example.tureguideversion1.Model.LocationItem;
import com.example.tureguideversion1.R;
import com.glide.slider.library.SliderLayout;
import com.glide.slider.library.animations.DescriptionAnimation;
import com.glide.slider.library.slidertypes.BaseSliderView;
import com.glide.slider.library.slidertypes.TextSliderView;
import com.glide.slider.library.transformers.BaseTransformer;
import com.glide.slider.library.tricks.ViewPagerEx;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polyak.iconswitch.IconSwitch;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class TourFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private EditText startDateET, endDateET, eventTime, groupName_ET, eventCost_ET, meetingPlace_ET, eventDescription_ET;
    private SliderLayout imageSlider;
    private ImageView logo;
    private LottieAnimationView loading;
    private List<LocationItem> locationList;
    private List<String> selectedLocation;
    private AutoCompleteTextView locationEt;
    private ArrayList<String> location;
    private String locationForViewPage, districtForLocationImage, districtFromLocationSelection, format,
            userID, publishDate, s_date, r_date, time, place, description, meetPlace, group_name, cost;
    private Button locationSelection, createEvent;
    private int slide;
    private View view;
    private IconSwitch iconSwitch;
    private Animation anim;
    private TextView tourTitle;
    private LinearLayout eventLayout;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private static String eventId;
    private int joinMemberCount;
    private navDrawerCheck check;

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
        loading.setVisibility(View.INVISIBLE);
        eventLayout.setVisibility(View.GONE);
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
                                eventLayout.setVisibility(View.GONE);
                            }
                        });
                        tourTitle.startAnimation(anim);
                        eventLayout.startAnimation(anim);
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
                                eventLayout.setVisibility(View.VISIBLE);
                            }
                        });
                        tourTitle.startAnimation(anim);

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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("locationList");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot

                        //loading.setVisibility(View.VISIBLE);
                        fillLocationList((Map<String, Object>) dataSnapshot.getValue());
                        //collectImageNInfo((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

        locationEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (selectedLocation.isEmpty()) {
                    if (locationEt.getText().toString().isEmpty()) {
                        locationSelection.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        locationEt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                selectedLocation.clear();
                locationSelection.setText("Select tourism places");
                locationSelection.setTextColor(getResources().getColor(R.color.colorYellow));
                hideKeyboardFrom(view.getContext(), view);
                logo.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                String location = locationEt.getText().toString();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(location.toLowerCase());
                ref.addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                //Get map of users in datasnapshot
                                locationForViewPage = locationEt.getText().toString().toLowerCase();
                                //loading.setVisibility(View.VISIBLE);
                                collectImageNInfo((Map<String, Object>) dataSnapshot.getValue());
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                //handle databaseError
                            }
                        });
            }
        });

        locationEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH ||
                        i == EditorInfo.IME_ACTION_DONE ||
                        keyEvent != null &&
                                keyEvent.getAction() == KeyEvent.ACTION_DOWN &&
                                keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (keyEvent == null || !keyEvent.isShiftPressed()) {
                        // the user is done typing.
                        if (!locationEt.getText().toString().matches("")) {
                            hideKeyboardFrom(view.getContext(), view);
                            locationEt.dismissDropDown();
                            logo.setVisibility(View.INVISIBLE);
                            loading.setVisibility(View.VISIBLE);
                            String location = locationEt.getText().toString();
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(location.toLowerCase());
                            ref.addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            //Get map of users in datasnapshot
                                            //loading.setVisibility(View.VISIBLE);
                                            collectImageNInfo((Map<String, Object>) dataSnapshot.getValue());
                                            locationForViewPage = districtForLocationImage;
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                            //handle databaseError
                                        }
                                    });
                            return true; // consume.
                        }
                    }
                }
                return false; // pass on to other listeners.
            }

        });

        locationSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LocationSelection_bottomSheet bottom_sheet = new LocationSelection_bottomSheet();
                Bundle args = new Bundle();
                if (locationEt.getText().toString().isEmpty() && !selectedLocation.isEmpty() || !selectedLocation.isEmpty()) {
                    locationEt.setFocusable(false);
                    locationEt.setFocusableInTouchMode(false);
                    locationEt.setText(districtFromLocationSelection);
                    int pos = locationEt.getText().length();
                    locationEt.setSelection(pos);
                    locationEt.setFocusable(true);
                    locationEt.setFocusableInTouchMode(true);
                }
                args.putString("location", locationEt.getText().toString());
                if (selectedLocation != null) {
                    args.putStringArrayList("selectedLocation", (ArrayList<String>) selectedLocation);
                }
                bottom_sheet.setArguments(args);
                bottom_sheet.show(getParentFragmentManager(), "locationSelection");
            }
        });

        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatabaseReference eventRef = databaseReference.child("event");
                eventId = eventRef.push().getKey();

                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd-MMM-yyyy hh:mm a");
                publishDate = simpleDateFormat.format(calendar.getTime());
                s_date = startDateET.getText().toString();
                r_date = endDateET.getText().toString();
                time = eventTime.getText().toString();
                place = locationEt.getText().toString();
                description = eventDescription_ET.getText().toString();
                meetPlace = meetingPlace_ET.getText().toString();
                group_name = groupName_ET.getText().toString();
                cost = eventCost_ET.getText().toString();
                joinMemberCount = 1;

                if (TextUtils.isEmpty(place)) {
                    if (!selectedLocation.isEmpty()) {
                        if (!TextUtils.isEmpty(s_date) &&
                                !TextUtils.isEmpty(r_date) &&
                                !TextUtils.isEmpty(group_name) &&
                                !TextUtils.isEmpty(cost) &&
                                !TextUtils.isEmpty(time) &&
                                !TextUtils.isEmpty(meetPlace) &&
                                !TextUtils.isEmpty(description)) {
                            locationEt.setFocusable(false);
                            locationEt.setFocusableInTouchMode(false);
                            locationEt.setText(districtFromLocationSelection);
                            int pos = locationEt.getText().length();
                            locationEt.setSelection(pos);
                            locationEt.setFocusable(true);
                            locationEt.setFocusableInTouchMode(true);
                            Toasty.info(view.getContext(), "Press again to create Event!", Toasty.LENGTH_SHORT).show();
                        } else {
                            locationEt.setFocusable(false);
                            locationEt.setFocusableInTouchMode(false);
                            locationEt.setText(districtFromLocationSelection);
                            int pos = locationEt.getText().length();
                            locationEt.setSelection(pos);
                            locationEt.setFocusable(true);
                            locationEt.setFocusableInTouchMode(true);
                        }
                    }else {
                        Toasty.error(view.getContext(), "Enter District!", Toasty.LENGTH_SHORT).show();
                        locationEt.requestFocus();
                    }
                } else if (locationSelection.getVisibility() == View.GONE) {
                    locationSelection.setTextColor(Color.RED);
                    Toasty.error(view.getContext(), "Select district from suggestion or press done from keyboard!", Toasty.LENGTH_SHORT).show();
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
                } else {
                    if (!place.matches(districtFromLocationSelection)) {
                        place = districtFromLocationSelection;
                        addEventInDB(eventId, s_date, r_date, time, place, meetPlace, description, publishDate, joinMemberCount,
                                userID, group_name, cost);
                    } else if (place.matches(districtFromLocationSelection)) {
                        addEventInDB(eventId, s_date, r_date, time, place, meetPlace, description, publishDate, joinMemberCount,
                                userID, group_name, cost);
                    } else {
                        Toasty.error(view.getContext(), "Location mismatching!", Toasty.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
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
        districtFromLocationSelection = d;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                slide = data.getIntExtra("slide", 0);

                loading.setVisibility(View.INVISIBLE);
                logo.setVisibility(View.INVISIBLE);
                imageSlider.setVisibility(View.VISIBLE);
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
            }
        }
    }

    private void fillLocationList(Map<String, Object> users) {
        locationList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get district field and append to list
            String location = (String) singleUser.get("district");
            String[] strArray = location.split(" ");
            StringBuilder uppercaseWord = new StringBuilder();
            for (String s : strArray) {
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                uppercaseWord.append(cap + " ");
            }
            String pureString = uppercaseWord.substring(0, uppercaseWord.length() - 1);
            locationList.add(new LocationItem(pureString, R.drawable.travel_icon));
        }
        AutoCompleteLocationAdapter adapter = new AutoCompleteLocationAdapter(view.getContext(), locationList);
        locationEt.setAdapter(adapter);
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
            locationSelection.setVisibility(View.VISIBLE);
            // set Slider Transition Animation
            districtForLocationImage = locationEt.getText().toString().toLowerCase();
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
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String currentDate = year + "/" + month + "/" + day;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                endDateET.setText(dateFormat.format(date));
                long milisec = date.getTime();
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void addEventInDB(String id, String startDate, String returnDate, String time, String place, String meetPlace, String description,
                              String publishDate, int joinMemberCount, String eventPublisherId, String group_name, String cost) {
        final DatabaseReference eventRef = databaseReference.child("event");

        final Event event = new Event(id, startDate, returnDate, time, place, meetPlace, description, publishDate, joinMemberCount,
                eventPublisherId, group_name, cost);

        eventRef.child(eventId).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toasty.success(getContext(), "Your Event Successfully Added", Toasty.LENGTH_SHORT).show();
                    addJoinMember();
                    addEventLoacationList();
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
        endDateET = view.findViewById(R.id.endDate_Et);
        imageSlider = view.findViewById(R.id.slider);
        logo = view.findViewById(R.id.logoT);
        loading = view.findViewById(R.id.loading);
        locationSelection = view.findViewById(R.id.locationSelection);
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
        meetingPlace_ET = view.findViewById(R.id.meetingPlace_ET);
        eventDescription_ET = view.findViewById(R.id.eventDescription_ET);
    }

    private void getDate() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String currentDate = year + "/" + month + "/" + day;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = null;

                try {
                    date = dateFormat.parse(currentDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startDateET.setText(dateFormat.format(date));
                long milisec = date.getTime();
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    @Override
    public void onResume() {
        imageSlider.startAutoCycle();
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
            Intent i = new Intent(view.getContext(), LocationImage.class).putExtra("slide", slider.getBundle().getString("extra")).putExtra("location", locationForViewPage);
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
                eventTime.setText(selectedHour + ":" + selectedMinute + " " + format);
            }
        }, hour, minute, true);//Yes 24 hour time
        mTimePicker.show();

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

}
