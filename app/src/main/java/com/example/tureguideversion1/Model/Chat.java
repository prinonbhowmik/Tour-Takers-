package com.example.tureguideversion1.Model;

public class Chat {
    private String message;
    private String sender;

    public Chat() {
    }

    public Chat(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

}
