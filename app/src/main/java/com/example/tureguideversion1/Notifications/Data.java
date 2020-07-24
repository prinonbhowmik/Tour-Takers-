package com.example.tureguideversion1.Notifications;

public class Data {
    private String eventID;
    private int icon;
    private String body;
    private String title;
    private String sented;
    private String userID;
    private String fromActivity;
    private String commentID;
    private String userImage;
    private String userSex;
    private String startDate;
    private String returnDate;
    private String type;
    private String location;
    private String typeID;
    private String typeFor;

    public Data(String sented, String userID, String startDate, String returnDate, String type, String typeFor, String typeID, String location) {
        this.sented = sented;
        this.userID = userID;
        this.startDate = startDate;
        this.returnDate = returnDate;
        this.type = type;
        this.typeFor = typeFor;
        this.typeID = typeID;
        this.location = location;
    }

    public Data(String eventID, int icon, String body, String title, String sented, String userID, String fromActivity, String commentID, String userImage, String userSex) {
        this.eventID = eventID;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.userID = userID;
        this.fromActivity = fromActivity;
        this.commentID = commentID;
        this.userImage = userImage;
        this.userSex = userSex;
    }

    public Data(String eventID, int icon, String body, String title, String sented, String userID, String fromActivity, String userImage, String userSex) {
        this.eventID = eventID;
        this.icon = icon;
        this.body = body;
        this.title = title;
        this.sented = sented;
        this.userID = userID;
        this.fromActivity = fromActivity;
        this.userImage = userImage;
        this.userSex = userSex;
    }

    public Data() {
    }

    public String getTypeFor() {
        return typeFor;
    }

    public void setTypeFor(String typeFor) {
        this.typeFor = typeFor;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
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

    public String getFromActivity() {
        return fromActivity;
    }

    public void setFromActivity(String fromActivity) {
        this.fromActivity = fromActivity;
    }

    public String getCommentID() {
        return commentID;
    }

    public void setCommentID(String commentID) {
        this.commentID = commentID;
    }
}
