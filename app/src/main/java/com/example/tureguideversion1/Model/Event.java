package com.example.tureguideversion1.Model;

public class Event {

    private String id;
    private String startDate;
    private String returnDate;
    private String time;
    private String place;
    private String meetPlace;
    private String description;
    private String publishDate;
    private int joinMemberCount;
    private String eventPublisherId;
    private String groupName;
    private String cost;


    public Event() {
    }

    public Event(String id, String startDate, String returnDate, String time, String place, String meetPlace,
                 String description, String publishDate, int joinMemberCount, String eventPublisherId,
                 String groupName, String cost) {
        this.id = id;
        this.startDate = startDate;
        this.returnDate = returnDate;
        this.time = time;
        this.place = place;
        this.meetPlace = meetPlace;
        this.description = description;
        this.publishDate = publishDate;
        this.joinMemberCount = joinMemberCount;
        this.eventPublisherId = eventPublisherId;
        this.groupName = groupName;
        this.cost = cost;
    }

    public String getId() {
        return id;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getReturnDate() {
        return returnDate;
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

    public String getGroupName() {
        return groupName;
    }

    public String getCost() {
        return cost;
    }
}
