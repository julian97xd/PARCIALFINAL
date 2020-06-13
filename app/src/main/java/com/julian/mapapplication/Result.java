package com.julian.mapapplication;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("geometry")
    public Geometry geometry;

    public Geometry getGeometry() {
        return geometry;
    }
}
