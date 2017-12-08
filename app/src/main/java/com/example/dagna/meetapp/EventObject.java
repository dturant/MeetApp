package com.example.dagna.meetapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.StorageReference;

/**
 * Created by dagna on 18.11.2017.
 */

public class EventObject {
    private String id;
    private String name;
    private String date;
    private String time;
    private String description;
    private LatLng location;
    private String owner;
    private Category category;
    private StorageReference imageRef;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public StorageReference getImageRef() {
        return imageRef;
    }

    public void setImageRef(StorageReference imageRef) {
        this.imageRef = imageRef;
    }

    public EventObject(String id, String name, String date, String time, String description, LatLng location, String owner, Category category, StorageReference imageRef) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.time = time;
        this.description = description;
        this.location = location;
        this.owner = owner;
        this.category = category;
        this.imageRef = imageRef;
    }

    @Override
    public String toString() {
        return "EventObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", owner='" + owner + '\'' +
                ", category=" + category +
                ", imageRef=" + imageRef +
                '}';
    }
}
