package com.example.go4lunch.models.apiGooglePlace.placeDetails;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultDetails {

    @SerializedName("formatted_phone_number")
    @Expose
    private String formattedPhoneNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("rating")
    @Expose
    private Double rating;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;
    @SerializedName("website")
    @Expose
    private String website;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("result")
    @Expose
    private ResultDetails result;

    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("weekday_text")
    @Expose
    private List<String> weekdayText = null;


    public ResultDetails getResult() {
        return result;
    }

    public String getFormattedPhoneNumber() {
        return formattedPhoneNumber;
    }

    public String getName() {
        return name;
    }

    public Geometry getGeometry() {
        return geometry;
    }


    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }


    public Double getRating() {
        return rating;
    }


    public String getVicinity() {
        return vicinity;
    }


    public String getWebsite() {
        return website;
    }

    public String getPlaceId() {
        return placeId;
    }

    public List<String> getWeekdayText() {
        return weekdayText;
    }
}