package com.example.dagna.meetapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by dagna on 18.11.2017.
 */

public class EventObject {
    private String name;
    private String date;
    private String time;
    private String description;
    private LatLng location;
    private String owner;
    private Category category;
    private boolean onMap=false;

    public boolean isOnMap() {
        return onMap;
    }

    public void setOnMap(boolean onMap) {
        this.onMap = onMap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public EventObject(String name, String date, String time, String description, LatLng location, String owner, Category category) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
        this.location = location;
        this.owner = owner;
        this.category = category;
    }
}
