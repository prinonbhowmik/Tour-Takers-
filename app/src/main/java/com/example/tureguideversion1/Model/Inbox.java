package com.example.tureguideversion1.Model;

public class Inbox {
    private String eventID;
    private String memberID;
    private String memberSex;
    private String image;

    public Inbox() {
    }

    public Inbox(String eventID, String memberID, String image, String memberSex) {
        this.eventID = eventID;
        this.memberID = memberID;
        this.image = image;
        this.memberSex = memberSex;
    }

    public String getMemberSex() {
        return memberSex;
    }

    public String getImage() {
        return image;
    }

    public String getEventID() {
        return eventID;
    }

    public String getMemberID() {
        return memberID;
    }
}
