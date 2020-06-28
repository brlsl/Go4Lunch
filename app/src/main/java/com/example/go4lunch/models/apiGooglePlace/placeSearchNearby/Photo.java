package com.example.go4lunch.models.apiGooglePlace.placeSearchNearby;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {


    @SerializedName("photo_reference")
    @Expose
    private String photoReference;

    public String getPhotoReference() {
        return photoReference;
    }

}