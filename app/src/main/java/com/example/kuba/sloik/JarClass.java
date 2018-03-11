package com.example.kuba.sloik;

/**
 * Created by Grove on 10.03.2018.
 */

public class JarClass {
    public String size;
    public String name;
    public String description;
    public String date;
    public String latitude;
    public String longitude;

    public JarClass(){
        this("null","null","null","null","-10","-10");
    }
    public JarClass(String size, String name, String description, String date, String latitude, String longitude){
        this.size = size;
        this.name = name;
        this.description = description;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
