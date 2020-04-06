package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.CardView;
import com.example.tureguideversion1.R;


import java.util.List;

public class Adapter extends PagerAdapter {

    private List<CardView> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<CardView> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.items_for_cardview, container, false);

        ImageView imageView;
        TextView title, desc;

        imageView = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        desc = view.findViewById(R.id.desc);

        GlideApp.with(view.getContext())
                .load(models.get(position).getImage())
                .fitCenter()
                .into(imageView);
        title.setText(models.get(position).getTitle());
        desc.setText(models.get(position).getDesc());
        desc.setMovementMethod(new ScrollingMovementMethod());
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
