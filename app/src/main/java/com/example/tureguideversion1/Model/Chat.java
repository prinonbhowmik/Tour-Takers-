package com.example.tureguideversion1.Model;

public class Chat {
    private String message;
    private String sender;
    private String senderImageURL;

    public Chat(String message, String sender, String senderImageURL) {
        this.message = message;
        this.sender = sender;
        this.senderImageURL = senderImageURL;
    }

    public Chat() {
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getSenderImageURL() {
        return senderImageURL;
    }
}
