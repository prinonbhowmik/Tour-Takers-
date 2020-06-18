package com.example.tureguideversion1.Notifications;

public class Data {
    private String eventID;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String userID;

    public Data(String eventID, int icon, String body, String title, String sented, String userID) {
        this.eventID = eventID;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.userID = userID;
    }

    public Data() {
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSented() {
        return sented;
    }

    public void setSented(String sented) {
        this.sented = sented;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}