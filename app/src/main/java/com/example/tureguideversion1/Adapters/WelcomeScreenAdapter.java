package com.example.tureguideversion1.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Model.WelcomeScreenModel;
import com.example.tureguideversion1.R;

import java.util.List;

public class WelcomeScreenAdapter extends RecyclerView.Adapter<WelcomeScreenAdapter.WelcomeViewHolder> {

    private List<WelcomeScreenModel> welcomeScreenModelList;

    public WelcomeScreenAdapter(List<WelcomeScreenModel> welcomeScreenModelList) {
        this.welcomeScreenModelList = welcomeScreenModelList;
    }

    @NonNull
    @Override
    public WelcomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_onboarding, parent, false);
        return new WelcomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WelcomeViewHolder holder, int position) {
        holder.setOnBoardingData(welcomeScreenModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return welcomeScreenModelList.size();
    }

    class WelcomeViewHolder extends RecyclerView.ViewHolder{
        private TextView textTitle, textDescription;
        private ImageView imageOnBoarding;

        public WelcomeViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textDescription = itemView.findViewById(R.id.textDescription);
            imageOnBoarding = itemView.findViewById(R.id.imageOnboarding);

        }
        public void setOnBoardingData(WelcomeScreenModel welcomeScreenModel){
            textTitle.setText(welcomeScreenModel.getTitle());
            textDescription.setText(welcomeScreenModel.getDescription());
            imageOnBoarding.setImageResource(welcomeScreenModel.getImage());
        }
    }
}
