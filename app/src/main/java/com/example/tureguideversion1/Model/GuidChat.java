package com.example.tureguideversion1.Model;

public class GuidChat {
    private String message;
    private String imageMessage;
    private String senderName;
    private String senderImage;
    private String senderSex;
    private String senderID;
    private String ID;
    private String eventID;
    private String commentTime;

    public GuidChat() {
    }

    public GuidChat(String message, String imageMessage, String senderName, String senderImage, String senderSex, String senderID, String ID, String eventID, String commentTime) {
        this.message = message;
        this.imageMessage = imageMessage;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.senderSex = senderSex;
        this.senderID = senderID;
        this.ID = ID;
        this.eventID = eventID;
        this.commentTime = commentTime;
    }

    public String getImageMessage() {
        return imageMessage;
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

    public String getCommentTime() {
        return commentTime;
    }
}
