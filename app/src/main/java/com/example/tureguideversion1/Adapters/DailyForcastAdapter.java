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
        holder.minTemp.setText(Math.round(forcastList.getMinTemp())+"°C");
        holder.maxTemp.setText(Math.round(forcastList.getMaxTemp())+"°C");
        holder.dailyDescription.setText(forcastList.getDaliyDescription());
        holder.dWind.setText(Math.round(forcastList.getWind_speed())+"ms");
        holder.dhumidity.setText(Math.round(forcastList.getHumidity())+"%");
        holder.dcloude.setText(forcastList.getClouds()+"%");
        holder.dDewPoint.setText(Math.round(forcastList.getdDewPoint())+"°C");
        String riseTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(forcastList.getSunrise()*1000));
        holder.dSunrise.setText(riseTime);
        String setTime = new SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(new Date(forcastList.getSunset()*1000));
        holder.dSunset.setText(setTime);
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
        int month = calendar.get(Calendar.MONTH);
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
        String m = "";
        switch (month) {
            case Calendar.JANUARY:
                m = "January";
                break;
            case Calendar.FEBRUARY:
                m = "February";
                break;
            case Calendar.MARCH:
                m = "March";
                break;
            case Calendar.APRIL:
                m = "April";
                break;
            case Calendar.MAY:
                m = "May";
                break;
            case Calendar.JUNE:
                m = "June";
                break;
            case Calendar.JULY:
                m = "July";
                break;
            case Calendar.AUGUST:
                m = "August";
                break;
            case Calendar.SEPTEMBER:
                m = "September";
                break;
            case Calendar.OCTOBER:
                m = "October";
                break;
            case Calendar.NOVEMBER:
                m = "November";
                break;
            case Calendar.DECEMBER:
                m = "December";
                break;
        }
        holder.date.setText(dailyDate.substring(0,2)+" "+m);
        //holder.dayName.append(" "+dailyDate.substring(0,2)+" "+m);
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
        private TextView minTemp, maxTemp, dailyDescription, date, dayName,dWind, dhumidity, dcloude, dDewPoint, dSunrise, dSunset;;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            dailyForcastImage = itemView.findViewById(R.id.dailyImage);
            minTemp = itemView.findViewById(R.id.minTemp);
            maxTemp = itemView.findViewById(R.id.maxTemp);
            dailyDescription = itemView.findViewById(R.id.dailyDescription);
            date = itemView.findViewById(R.id.date);
            dayName = itemView.findViewById(R.id.dayName);
            dWind = itemView.findViewById(R.id.dWind);
            dhumidity = itemView.findViewById(R.id.dhumidity);
            dcloude = itemView.findViewById(R.id.dcloude);
            dDewPoint = itemView.findViewById(R.id.dDewPoint);
            dSunrise = itemView.findViewById(R.id.dSunrise);
            dSunset = itemView.findViewById(R.id.dSunset);
        }
    }
}
