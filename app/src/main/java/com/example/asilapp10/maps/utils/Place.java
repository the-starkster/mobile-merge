package com.example.asilapp10.maps.utils;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

public class Place {
    private final String name;
    private final PlaceType type;
    private final LatLng coordinates;
    private final String address;
    private final String openNow;
    private final String currentOpeningHoursList;
    private final float rating;
    private final String ratingText;

    public enum PlaceType {
        PHARMACY, RECEPTION_CENTER
    }

    public Place(String name, PlaceType type, LatLng coordinates, String address, String openNow, String currentOpeningHours, float rating, String ratingText) {
        this.name = name;
        this.type = type;
        this.coordinates = coordinates;
        this.address = address;
        this.openNow = openNow;
        this.currentOpeningHoursList = currentOpeningHours;
        this.rating = rating;
        this.ratingText = ratingText;
    }

    public String getName() {
        return name;
    }

    public PlaceType getType() {
        return type;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public String getAddress() {
        return address;
    }

    public String isOpenNow() {
        return openNow;
    }

    public String getCurrentOpeningHours() {
        return currentOpeningHoursList;
    }

    public float getRating() {
        return rating;
    }

    public String getRatingText() {
        return ratingText;
    }

    @NonNull
    @Override
    public String toString() {
        return  "\nname: " + getName() +
                "\ntype: " + getType() +
                "\ncoordinates: " + getCoordinates() +
                "\naddress: " + getAddress() +
                "\nopenNow: " + isOpenNow() +
                "\ncurrentOpeningHours: " + getCurrentOpeningHours();
    }
}
