package com.example.tureguideversion1.Model;

public class Profile {
    String Id;
    String name;
    String email;
    String password;
    String address;
    String phone;
    String image;
    String sex;
    float rating;
    String token;
    int ratingCounter;
    int event;
    int tour;

    public Profile() {
    }


    public Profile(String id, String name, String email, String password, String address, String phone,
                   String image, String sex, float rating, String token, int ratingCounter, int event, int tour) {
        Id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.image = image;
        this.sex = sex;
        this.rating = rating;
        this.token = token;
        this.ratingCounter = ratingCounter;
        this.event = event;
        this.tour = tour;
    }

    public String getId() {
        return Id;
    }


    public String getToken() {
        return token;
    }

    public String getSex() {
        return sex;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getImage() {
        return image;
    }

    public int getRatingCounter() {
        return ratingCounter;
    }

    public int getEvent() {
        return event;
    }

    public int getTour() {
        return tour;
    }

    public float getRating() {
        return rating;
    }
}
