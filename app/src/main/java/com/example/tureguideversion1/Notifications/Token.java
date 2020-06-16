package com.example.tureguideversion1.Notifications;

public class Token {
    private String token;
    private String userID;

    public Token(String token, String userID) {
        this.token = token;
        this.userID = userID;
    }

    public Token(String token) {
        this.token = token;
    }

    public Token() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
}
