package com.example.takeanote.model;

import com.google.android.gms.maps.model.LatLng;

public class MapsInfo {
    private String id;
    private String title;
    private LatLng latLng;
    private String address;

    public MapsInfo() {
    }

    public MapsInfo(String title, LatLng latLng) {
        this.title = title;
        this.latLng = latLng;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getTitle() {
        return title;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
