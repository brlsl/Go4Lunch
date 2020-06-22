package com.example.go4lunch.models;

public class User {

    private String uid;
    private String username;
    private String urlPicture;
    private String restaurantChoiceName;
    private String restaurantChoiceId;

    public User(){} // Empty constructor required for Firestore's automatic data mapping.


    public User(String uid, String username, String urlPicture) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantChoiceName() { return restaurantChoiceName;}
    public String getRestaurantChoiceId() {return restaurantChoiceId;}




        // --- SETTERS ---
   // public void setUsername(String username) { this.username = username; }
  //  public void setUid(String uid) { this.uid = uid; }
    //public void setUrlPicture(String urlPicture) { this.urlPicture = urlPicture; }
   // public void setRestaurantChoiceName(String restaurantChoiceName) { this.restaurantChoiceName = restaurantChoiceName;}
   // public void setRestaurantChoiceId(String restaurantChoiceId) {this.restaurantChoiceId = restaurantChoiceId;}
}