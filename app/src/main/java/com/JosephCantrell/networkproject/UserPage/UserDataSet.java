package com.JosephCantrell.networkproject.UserPage;

import org.json.JSONObject;

public class UserDataSet {
    String userID;
    String name;
    String username;
    String email;
    JSONObject address;
    String phone;
    String website;
    String TAG = "UserDataSet";

    public UserDataSet(String ID, String name,  String username, String email, JSONObject address, String phone, String website) {
        this.userID = ID;
        this.name = name;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.website = website;

    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public JSONObject getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }
}
