package com.example.go4lunch.models.apiGooglePlace.placeSearchNearby;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultSearchNearby {

    // results of search nearby request
    @SerializedName("results")
    @Expose
    private List<ResultSearchNearby> results = null;

    @SerializedName("geometry")
    @Expose
    private Geometry geometry;

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private OpeningHours openingHours;
    @SerializedName("photos")
    @Expose
    private List<Photo> photos = null;
    @SerializedName("place_id")
    @Expose
    private String placeId;

    @SerializedName("rating")
    @Expose
    private Double rating;

    @SerializedName("scope")
    @Expose
    private String scope;
    @SerializedName("types")
    @Expose
    private List<String> types = null;
    @SerializedName("user_ratings_total")
    @Expose
    private Integer userRatingsTotal;
    @SerializedName("vicinity")
    @Expose
    private String vicinity;


    public List<ResultSearchNearby> getResults() {
        return results;
    }

    public ResultSearchNearby(Geometry geometry) {
        this.geometry = geometry;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public String getName() {
        return name;
    }

    public OpeningHours getOpeningHours() {
        return openingHours;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public String getPlaceId() {
        return placeId;
    }

    public Double getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

}