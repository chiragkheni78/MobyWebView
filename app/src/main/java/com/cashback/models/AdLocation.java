package com.cashback.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AdLocation implements Serializable {

    @SerializedName("fiLocationId")
    long locationID;

    @SerializedName("ffLatitude")
    double latitude;

    @SerializedName("ffLongitude")
    double longitude;

    @SerializedName("fsLandmark")
    String landmark;

    @SerializedName("fsAddress")
    String address;

    @SerializedName("fsContact")
    String phone;

    @SerializedName("fsEmail")
    String email;

    @SerializedName("fsCity")
    String city;
    @SerializedName("fsState")
    String state;

    @SerializedName("ffDistanceToUser")
    double distance;


    public long getLocationID() {
        return locationID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getDistance() {
        return distance;
    }

    public String getLandmark() {
        return landmark;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }
}
