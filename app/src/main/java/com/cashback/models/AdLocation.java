package com.cashback.models;

import com.google.gson.annotations.SerializedName;

public class AdLocation {

    @SerializedName("fiLocationId")
    long locationID;

    @SerializedName("ffLatitude")
    double latitude;

    @SerializedName("ffLogitude")
    double longitude;

    @SerializedName("location_distance")
    int distance;

    @SerializedName("fsLandmark")
    String landmark;

    public long getLocationID() {
        return locationID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getDistance() {
        return distance;
    }

    public String getLandmark() {
        return landmark;
    }
}
