package com.example.tureguideversion1.Model;

public class LocationItem {

    private String locationName;
    private int icon;

    public LocationItem(String locationName, int icon) {
        this.locationName = locationName;
        this.icon = icon;
    }

    public String getlocationName() {
        return locationName;
    }

    public int getIcon() {
        return icon;
    }
}
