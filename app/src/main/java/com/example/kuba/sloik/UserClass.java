package com.example.kuba.sloik;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jan on 08.04.18.
 */

public class UserClass {
    private String username;
    private String email;
    private String password;
    private ArrayList<String> userJars;
    private Integer rating;
    private String photoID;

    public UserClass() {}

    public UserClass(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userJars = new ArrayList<>();
        this.rating = null;
        this.photoID = null;
    }

    public UserClass(String username, String email, String password, ArrayList<String> userJars, Integer rating, String photoID) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.userJars = userJars;
        this.rating = rating;
        this.photoID = photoID;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<String> getUserJars() {
        return userJars;
    }

    public void setUserJars(ArrayList<String> userJars) {
        this.userJars = userJars;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getPhotoID() {
        return photoID;
    }

    public void setPhotoID(String photoID) {
        this.photoID = photoID;
    }

    @Override
    public String toString() {
        return "UserClass{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
