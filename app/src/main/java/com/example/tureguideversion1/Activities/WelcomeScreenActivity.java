package com.example.tureguideversion1.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.tureguideversion1.Adapters.WelcomeScreenAdapter;
import com.example.tureguideversion1.Model.WelcomeScreenModel;
import com.example.tureguideversion1.R;

import java.util.ArrayList;
import java.util.List;

public class WelcomeScreenActivity extends AppCompatActivity {
    private WelcomeScreenAdapter welcomeScreenAdapter;
    private ViewPager2 onboardingViewPager;
    private LinearLayout layoutOnboardingIndicators;
    private Button buttonOnboardingAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);



        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        buttonOnboardingAction = findViewById(R.id.buttonOnboardingAction);


        onboardingViewPager = findViewById(R.id.onboardingViewPager);
        setupOnboardingItems();
        setupOnboardingIndicator();
        onboardingViewPager.setAdapter(welcomeScreenAdapter);
        setCurrentOnboardingIndicator(0);

        if (restorePrefdata()) {
            startActivity( new Intent(WelcomeScreenActivity.this, SignIn.class));
            finish();

        }
        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
            }
        });

        buttonOnboardingAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onboardingViewPager.getCurrentItem() + 1 < welcomeScreenAdapter.getItemCount()){
                    onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem()+1);
                }else{
                    startActivity( new Intent(WelcomeScreenActivity.this, SignIn.class));
                    savePrefData();
                    finish();
                }
            }
        });

    }//onCreate ends

    private boolean restorePrefdata() {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        Boolean isIntroActivityOpened=sharedPreferences.getBoolean("isIntroOpen",false);
        return isIntroActivityOpened;
    }

    private void savePrefData() {
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("myPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putBoolean("isIntroOpen",true);
        editor.commit();
    }

    private void setupOnboardingItems(){
        List<WelcomeScreenModel> welcomeItems = new ArrayList<>();
        WelcomeScreenModel itemPayOnline = new WelcomeScreenModel();
        itemPayOnline.setImage(R.drawable.undraw_credit_card_payment_12va);
        itemPayOnline.setTitle("Pay Your Bill Online");
        itemPayOnline.setDescription("Electric bill payment is a feature of online, mobile and telephone banking.");

        WelcomeScreenModel itemOnTheWay = new WelcomeScreenModel();
        itemOnTheWay.setImage(R.drawable.undraw_on_the_way_ldaq);
        itemOnTheWay.setTitle("Your Food Is On The Way");
        itemOnTheWay.setDescription("Our delivery rider is on the way to deliver your order. ");


        WelcomeScreenModel itemEatTogether = new WelcomeScreenModel();
        itemEatTogether.setImage(R.drawable.undraw_eating_together_tjhx);
        itemEatTogether.setTitle("Eat Together");
        itemEatTogether.setDescription("Enjoy your meal and have a great day. Don't forget to rate us.");

        welcomeItems.add(itemPayOnline);
        welcomeItems.add(itemOnTheWay);
        welcomeItems.add(itemEatTogether);

        welcomeScreenAdapter = new WelcomeScreenAdapter(welcomeItems);


    }

    private void setupOnboardingIndicator(){
        ImageView[] indicators = new ImageView[welcomeScreenAdapter.getItemCount()];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8,0,8,0);

        for (int i=0; i<indicators.length;i++){
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_inactive));
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index){
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i=0; i<childCount; i++){
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index){
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.onboarding_indicator_active));
            }else {
                imageView.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive));
            }
        }

        if (index == welcomeScreenAdapter.getItemCount()-1){
            buttonOnboardingAction.setText("Start");
        }else{
            buttonOnboardingAction.setText("Next");
        }
    }
}