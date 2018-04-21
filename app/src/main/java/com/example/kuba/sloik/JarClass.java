package com.example.kuba.sloik;

import java.io.Serializable;

/**
 * Created by Grove on 10.03.2018.
 */

public class JarClass implements Serializable {
    private String ownerId;
    public String size;
    public String name;
    public String description;
    public String date;
    public String latitude;
    public String longitude;

    public JarClass(){
        this.size = "5";
        this.name = "not_working";
        this.description = "neither";
        this.date = "03.26";
        this.latitude="10";
        this.longitude="20";
    }

    public JarClass(String ownerId, String size, String name, String description, String date, String latitude, String longitude){
        this.ownerId = ownerId;
        this.size = size;
        this.name = name;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getSize() {
        return size;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDate() {
        return date;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
