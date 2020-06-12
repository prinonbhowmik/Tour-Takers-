package com.example.tureguideversion1.Model;

public class Chat {
    private String message;
    private String sender;
    private String id;

    public Chat() {
    }

    public Chat(String message, String sender, String id) {
        this.message = message;
        this.sender = sender;
        this.sender = id;
    }


    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getId() {
        return id;
    }

}
