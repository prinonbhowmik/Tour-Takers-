package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.GlideApp;
import com.example.tureguideversion1.Model.DailyForcastList;
import com.example.tureguideversion1.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class DailyForcastAdapter extends RecyclerView.Adapter<DailyForcastAdapter.ViewHolder> {
    private List<DailyForcastList> dailyForcastLists;
    private Context context;

    public DailyForcastAdapter(List<DailyForcastList> dailyForcastLists, Context context) {
        this.dailyForcastLists = dailyForcastLists;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_forcast_design,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DailyForcastList forcastList = dailyForcastLists.get(position);
        holder.minTemp.setText("min: "+Math.round(forcastList.getMinTemp())+"°C");
        holder.maxTemp.setText("max: "+Math.round(forcastList.getMaxTemp())+"°C");
        holder.dailyDescription.setText(forcastList.getDaliyDescription());
        String dailyDate =new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).format(new Date(forcastList.getDailytime() * 1000));
        Date date = new Date();
        SimpleDateFormat date_format = new SimpleDateFormat("dd/MM/yyyy");
        try {
            date = date_format.parse(dailyDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        int result = calendar.get(Calendar.DAY_OF_WEEK);
        switch (result) {
            case Calendar.SATURDAY:
                holder.dayName.setText("Saturday");
                break;
            case Calendar.SUNDAY:
                holder.dayName.setText("Sunday");
                break;
            case Calendar.MONDAY:
                holder.dayName.setText("Monday");
                break;
            case Calendar.TUESDAY:
                holder.dayName.setText("Tuesday");
                break;
            case Calendar.WEDNESDAY:
                holder.dayName.setText("Wednesday");
                break;
            case Calendar.THURSDAY:
                holder.dayName.setText("Thursday");
                break;
            case Calendar.FRIDAY:
                holder.dayName.setText("Friday");
                break;
        }
        holder.date.setText(dailyDate);
        String imageURL = "https://openweathermap.org/img/wn/"+forcastList.getDaliyImage()+"@2x.png";
        GlideApp.with(context)
                .load(imageURL)
                .fitCenter()
                .into(holder.dailyForcastImage);
    }

    @Override
    public int getItemCount() {
        return dailyForcastLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView dailyForcastImage;
        private TextView minTemp, maxTemp, dailyDescription, date, dayName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dailyForcastImage = itemView.findViewById(R.id.dailyImage);
            minTemp = itemView.findViewById(R.id.minTemp);
            maxTemp = itemView.findViewById(R.id.maxTemp);
            dailyDescription = itemView.findViewById(R.id.dailyDescription);
            date = itemView.findViewById(R.id.date);
            dayName = itemView.findViewById(R.id.dayName);
        }
    }
}
