package com.example.go4lunch.models.apiGooglePlace.placeDetails;

import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.Location;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Geometry {

        @SerializedName("location")
        @Expose
        private Location location;


        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

}
