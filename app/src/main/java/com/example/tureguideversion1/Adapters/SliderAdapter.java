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
import com.example.tureguideversion1.Model.ImageSliderCardView;
import com.example.tureguideversion1.R;
import com.squareup.picasso.Picasso;


import java.util.List;

public class SliderAdapter extends PagerAdapter {

    private List<ImageSliderCardView> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public SliderAdapter(List<ImageSliderCardView> models, Context context) {
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

        //imageView.set(models.get(position).getImage());
        Picasso.get().load(models.get(position).getImage()).into(imageView);
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
