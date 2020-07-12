package com.example.go4lunch.api;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;


public class UserHelper {

    private static final String COLLECTION_USERS = "users";
    private static final String COLLECTION_RESTAURANTS_LIKED = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate, SetOptions.merge());
    }

    public static void createRestaurantLikedByUser(String uid, String restaurantId, Boolean isLiked){
        Restaurant restaurantLiked = new Restaurant(restaurantId, isLiked);
        UserHelper.getUsersCollection().document(uid)
                .collection(COLLECTION_RESTAURANTS_LIKED).document(restaurantId).set(restaurantLiked);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<DocumentSnapshot> getUserLikeRestaurant(String uid, String restaurantId){
        return UserHelper.getUsersCollection().document(uid)
                .collection(COLLECTION_RESTAURANTS_LIKED).document(restaurantId).get();
    }


    // --- UPDATE ---

    public static void updateUserRestaurantChoiceId(String uid, String userRestaurantChoiceId) {
        UserHelper.getUsersCollection().document(uid).update("restaurantChoiceId", userRestaurantChoiceId);
    }

    public static void updateUserRestaurantChoiceName(String uid, String userRestaurantChoiceName){
        UserHelper.getUsersCollection().document(uid).update("restaurantChoiceName", userRestaurantChoiceName);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

    public static void deleteRestaurantLikedByUser(String uid, String restaurantId){
        UserHelper.getUsersCollection().document(uid)
                .collection(COLLECTION_RESTAURANTS_LIKED).document(restaurantId).delete();
    }

}