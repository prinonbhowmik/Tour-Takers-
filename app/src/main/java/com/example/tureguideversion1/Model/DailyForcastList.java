package com.example.tureguideversion1.Model;

public class DailyForcastList {
    private String daliyImage;
    private Float minTemp;
    private Float maxTemp;
    private String daliyDescription;
    private Long dailytime;

    public DailyForcastList(String daliyImage, Float minTemp, Float maxTemp, String daliyDescription, Long dailytime) {
        this.daliyImage = daliyImage;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.daliyDescription = daliyDescription;
        this.dailytime = dailytime;
    }

    public String getDaliyImage() {
        return daliyImage;
    }

    public Float getMinTemp() {
        return minTemp;
    }

    public Float getMaxTemp() {
        return maxTemp;
    }

    public String getDaliyDescription() {
        return daliyDescription;
    }

    public Long getDailytime() {
        return dailytime;
    }
}
