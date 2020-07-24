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
    private double latForMeetingPlace;
    private double lonForMeetingPlace;
    private String subLocalityForMeetingPlace;
    private String guideID;
    private String guideMeetPlace;
    private double latForGuideMeetingPlace;
    private double lonForGuideMeetingPlace;
    private String status;


    public Event() {
    }

    public Event(String id, String startDate, String returnDate, String place, String publishDate,
                 String eventPublisherId, String cost, String guideMeetPlace, double latForGuideMeetingPlace,
                 double lonForGuideMeetingPlace) {
        this.id = id;
        this.startDate = startDate;
        this.returnDate = returnDate;
        this.place = place;
        this.publishDate = publishDate;
        this.eventPublisherId = eventPublisherId;
        this.cost = cost;
        this.guideMeetPlace = guideMeetPlace;
        this.latForGuideMeetingPlace = latForGuideMeetingPlace;
        this.lonForGuideMeetingPlace = lonForGuideMeetingPlace;
    }

    public Event(String id, String startDate, String returnDate, String time, String place, String meetPlace,
                 String description, String publishDate, int joinMemberCount, String eventPublisherId,
                 String groupName, String cost, double latForMeetingPlace, double lonForMeetingPlace,
                 String subLocalityForMeetingPlace, String guideMeetPlace, double latForGuideMeetingPlace, double lonForGuideMeetingPlace) {
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
        this.latForMeetingPlace = latForMeetingPlace;
        this.lonForMeetingPlace = lonForMeetingPlace;
        this.subLocalityForMeetingPlace = subLocalityForMeetingPlace;
        this.guideMeetPlace = guideMeetPlace;
        this.latForGuideMeetingPlace = latForGuideMeetingPlace;
        this.lonForGuideMeetingPlace = lonForGuideMeetingPlace;
    }

    public String getStatus() {
        return status;
    }

    public String getGuideMeetPlace() {
        return guideMeetPlace;
    }

    public double getLatForGuideMeetingPlace() {
        return latForGuideMeetingPlace;
    }

    public double getLonForGuideMeetingPlace() {
        return lonForGuideMeetingPlace;
    }

    public String getGuideID() {
        return guideID;
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

    public double getLatForMeetingPlace() {
        return latForMeetingPlace;
    }

    public double getLonForMeetingPlace() {
        return lonForMeetingPlace;
    }

    public String getSubLocalityForMeetingPlace() {
        return subLocalityForMeetingPlace;
    }
}
