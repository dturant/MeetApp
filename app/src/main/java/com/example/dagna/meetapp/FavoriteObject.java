package com.example.dagna.meetapp;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.storage.StorageReference;


public class FavoriteObject {
    private String id;
    private String name;
    private String description;
    private LatLng location;
    private String owner;
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

    public StorageReference getImageRef() {
        return imageRef;
    }

    public void setImageRef(StorageReference imageRef) {
        this.imageRef = imageRef;
    }

    public FavoriteObject(String id, String name, String description, LatLng location, String owner, StorageReference imageRef) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.owner = owner;
        this.imageRef = imageRef;
    }

    @Override
    public String toString() {
        return "EventObject{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", location=" + location +
                ", owner='" + owner + '\'' +
                ", imageRef=" + imageRef +
                '}';
    }
}
