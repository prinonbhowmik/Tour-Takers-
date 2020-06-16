package com.example.tureguideversion1.Model;

public class Chat {
    private String message;
    private String senderName;
    private String senderImage;
    private String senderSex;
    private String senderID;
    private String ID;
    private String eventID;

    public Chat() {
    }

    public Chat(String message, String senderName, String senderImage, String senderSex, String senderID, String ID, String eventID) {
        this.message = message;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.senderSex = senderSex;
        this.senderID = senderID;
        this.ID = ID;
        this.eventID = eventID;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public String getSenderSex() {
        return senderSex;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getID() {
        return ID;
    }

    public String getEventID() {
        return eventID;
    }
}
