package com.julian.mapapplication;

import com.google.gson.annotations.SerializedName;

public class Location {

    public Location(double lat, double lng){
        this.lat = lat;
        this.lng = lng;
    }

    @SerializedName("lat")
    public double lat;
    @SerializedName("lng")
    public double lng;

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
