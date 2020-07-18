package com.example.go4lunch.models;

public class Restaurant {
    private String restaurantId;
    private Boolean restaurantIsLiked;

    public Restaurant(){} // Empty constructor required for Firestore's automatic data mapping.

    public Restaurant(String restaurantId, Boolean restaurantIsLiked){
        this.restaurantId = restaurantId;
        this.restaurantIsLiked = restaurantIsLiked;
    }


    // --- GETTERS ---
    public String getRestaurantId() { return restaurantId; }
    public Boolean getRestaurantIsLiked() { return restaurantIsLiked; }

    // --- SETTERS ---
    public void setRestaurantId(String restaurantId) { this.restaurantId = restaurantId; }
    public void setRestaurantIsLiked(Boolean restaurantIsLiked) {
        this.restaurantIsLiked = restaurantIsLiked;}
}
