package com.example.go4lunch.models;

public class User {

    private String uid;
    private String username;
    private String urlPicture;
    private String restaurantChoiceId;

    public User(){} // Empty constructor required for Firestore's automatic data mapping.

    public User(String uid, String username, String urlPicture, String restaurantChoiceId) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantChoiceId = restaurantChoiceId;

    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getUserRestaurantChoiceId() { return restaurantChoiceId;}

    // --- SETTERS ---
    public void setUsername(String username) { this.username = username; }
    public void setUid(String uid) { this.uid = uid; }
    public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
    public void setUserRestaurantChoiceId(String restaurantChoiceId){this.restaurantChoiceId = restaurantChoiceId;}


}