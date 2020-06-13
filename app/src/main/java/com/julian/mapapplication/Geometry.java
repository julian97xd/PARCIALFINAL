package com.julian.mapapplication;

import com.google.gson.annotations.SerializedName;

public class Geometry {
    @SerializedName("locatioón")
    public Location location;

    public Location getLocation() {
        return location;
    }
}
