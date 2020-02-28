package com.example.tureguideversion1.Model;

public class Profile {

    String name;
    String email;
    String password;
    String address;
    String phone;
    String image;

    public Profile() {
    }

    public Profile(String name, String email, String password, String address, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
    }

    public Profile(String name, String email, String password, String address, String phone, String image) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.image = image;
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
}
