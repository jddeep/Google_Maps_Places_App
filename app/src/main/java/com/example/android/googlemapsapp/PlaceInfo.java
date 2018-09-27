package com.example.android.googlemapsapp;

import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;

public class PlaceInfo {
    private String name;
    private String address;
    private String phone;
    private String id;
    private String attribytions;
    private Uri websiteUri;
    private LatLng latLng;
    private float rating;

    public PlaceInfo(String name, String address, String phone, String id, String attribytions, Uri websiteUri, LatLng latLng, float rating) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.id = id;
        this.attribytions = attribytions;
        this.websiteUri = websiteUri;
        this.latLng = latLng;
        this.rating = rating;
    }
    public PlaceInfo() {
}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttribytions() {
        return attribytions;
    }

    public void setAttribytions(String attribytions) {
        this.attribytions = attribytions;
    }

    public Uri getWebsiteUri() {
        return websiteUri;
    }

    public void setWebsiteUri(Uri websiteUri) {
        this.websiteUri = websiteUri;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "PlaceInfo{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                ", id='" + id + '\'' +
                ", attribytions='" + attribytions + '\'' +
                ", websiteUri=" + websiteUri +
                ", latLng=" + latLng +
                ", rating=" + rating +
                '}';
    }
}
