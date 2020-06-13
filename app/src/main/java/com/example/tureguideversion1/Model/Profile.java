package com.example.tureguideversion1.Model;

public class Profile {

    String name;
    String email;
    String password;
    String address;
    String phone;
    String image;
    String sex;

    public Profile() {
    }


    public Profile(String name, String email, String password, String address, String phone, String image, String sex) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.phone = phone;
        this.image = image;
        this.sex = sex;
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
}
