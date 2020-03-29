package com.example.tureguideversion1.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tureguideversion1.Model.LocationSelectionItem;
import com.example.tureguideversion1.R;
import com.polyak.iconswitch.IconSwitch;

import java.util.List;

public class LocationSelection_bottomSheet_adapter extends RecyclerView.Adapter<LocationSelection_bottomSheet_adapter.ViewHolder> {

    private List<LocationSelectionItem> locationList;
    private Context context;

    public LocationSelection_bottomSheet_adapter(List<LocationSelectionItem> locationList, Context context) {
        this.locationList = locationList;
        this.context = context;
    }

    @NonNull
    @Override
    public LocationSelection_bottomSheet_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_selection_layout, parent, false);
        return new LocationSelection_bottomSheet_adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final LocationSelection_bottomSheet_adapter.ViewHolder holder, int position) {
        final LocationSelectionItem location = locationList.get(position);
        holder.location.setText(location.getLocation());
        holder.swt.setCheckedChangeListener(new IconSwitch.CheckedChangeListener() {
            @Override
            public void onCheckChanged(IconSwitch.Checked current) {
                switch (holder.swt.getChecked()) {
                    case LEFT:
                        holder.locationLayout.setBackground(context.getDrawable(R.drawable.edit_text_design));
                        break;
                    case RIGHT:
                        holder.locationLayout.setBackground(context.getDrawable(R.drawable.edit_text_design2));
                        break;
                }
            }
        });

        holder.locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (holder.swt.getChecked()) {
                    case LEFT:
                        holder.swt.setChecked(IconSwitch.Checked.RIGHT);
                        holder.locationLayout.setBackground(context.getDrawable(R.drawable.edit_text_design2));
                        break;
                    case RIGHT:
                        holder.swt.setChecked(IconSwitch.Checked.LEFT);
                        holder.locationLayout.setBackground(context.getDrawable(R.drawable.edit_text_design));
                        break;
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView location;
        private IconSwitch swt;
        private LinearLayout locationLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            location = itemView.findViewById(R.id.locationBtms);
            swt = itemView.findViewById(R.id.icon_switch);
            locationLayout = itemView.findViewById(R.id.locationLayout);
        }
    }
}
