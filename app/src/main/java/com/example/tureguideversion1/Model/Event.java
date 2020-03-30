package com.example.tureguideversion1.Model;

public class Event {

    private String id;
    private String date;
    private String time;
    private String place;
    private String meetPlace;
    private String description;
    private String publishDate;
    private int joinMemberCount;
    private String eventPublisherId;


    public Event() {
    }

    public Event(String id, String date, String time, String place, String meetPlace, String description,
                 String publishDate, int joinMemberCount, String eventPublisherId) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.place = place;
        this.meetPlace = meetPlace;
        this.description = description;
        this.publishDate = publishDate;
        this.joinMemberCount = joinMemberCount;
        this.eventPublisherId = eventPublisherId;
    }

    public String getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getPlace() {
        return place;
    }

    public String getMeetPlace() {
        return meetPlace;
    }

    public String getDescription() {
        return description;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public int getJoinMemberCount() {
        return joinMemberCount;
    }

    public String getEventPublisherId() {
        return eventPublisherId;
    }
}
