package com.example.tureguideversion1.Fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.request.RequestOptions;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.tureguideversion1.Activities.CreateEvent;
import com.example.tureguideversion1.Activities.LocationImage;
import com.example.tureguideversion1.Activities.MainActivity;
import com.example.tureguideversion1.Activities.NoInternetConnection;
import com.example.tureguideversion1.Adapters.AutoCompleteLocationAdapter;
import com.example.tureguideversion1.Model.LocationItem;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static android.app.Activity.RESULT_OK;
import static androidx.constraintlayout.widget.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class TourFragment extends Fragment implements BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {

    private EditText startDate, endDate;
    private SliderLayout imageSlider;
    private ImageView logo;
    private LottieAnimationView loading;
    private List<LocationItem> locationList;
    private AutoCompleteTextView locationEt;
    private ArrayList<String> location;
    private String locationForViewPage;
    private int slide;

    public TourFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tour, container, false);

        init(view);
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDate();
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEndDate();
            }
        });
        loading.setVisibility(View.INVISIBLE);


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

        locationEt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                hideKeyboardFrom(getContext(), getView());
                logo.setVisibility(View.INVISIBLE);
                loading.setVisibility(View.VISIBLE);
                String location = locationEt.getText().toString().toLowerCase();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(location);
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
                        hideKeyboardFrom(getContext(), getView());
                        locationEt.dismissDropDown();
                        logo.setVisibility(View.INVISIBLE);
                        loading.setVisibility(View.VISIBLE);
                        String location = locationEt.getText().toString().toLowerCase();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("location").child(location);
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
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }

        });


        return view;
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
        AutoCompleteLocationAdapter adapter = new AutoCompleteLocationAdapter(getContext(), locationList);
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
                TextSliderView sliderView = new TextSliderView(getActivity());
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

            // set Slider Transition Animation
            // mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Default);

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
                endDate.setText(dateFormat.format(date));
                long milisec = date.getTime();
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
        datePickerDialog.show();
    }

    private void init(View view) {
        locationEt = view.findViewById(R.id.location_Et);
        startDate = view.findViewById(R.id.startDate_Et);
        endDate = view.findViewById(R.id.endDate_Et);
        imageSlider = view.findViewById(R.id.slider);
        logo = view.findViewById(R.id.logoT);
        loading = view.findViewById(R.id.loading);
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
                startDate.setText(dateFormat.format(date));
                long milisec = date.getTime();
            }
        };

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), dateSetListener, year, month, day);
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
            Intent i = new Intent(getContext(), LocationImage.class).putExtra("slide", slider.getBundle().getString("extra")).putExtra("location", locationForViewPage);
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

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
