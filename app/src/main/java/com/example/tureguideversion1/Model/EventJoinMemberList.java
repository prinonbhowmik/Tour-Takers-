package com.example.tureguideversion1.Model;

public class EventJoinMemberList {

    private String Id;
    private String name;
    private String phone;
    private String email;
    private String password;
    private String image;
    private String rating;

    public EventJoinMemberList(String name, String email, String image) {
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public EventJoinMemberList(String id, String name, String phone, String email, String password,
                               String image, String rating) {
        this.Id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.image = image;
        this.rating = rating;
    }

    public EventJoinMemberList() {
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getImage() {
        return image;
    }

    public String getRating() {
        return rating;
    }
}
