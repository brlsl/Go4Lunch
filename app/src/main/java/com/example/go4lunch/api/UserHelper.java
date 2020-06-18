package com.example.go4lunch.api;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
    public static CollectionReference getRestaurantCollection(String uid){
        return FirebaseFirestore.getInstance().collection(COLLECTION_USERS)
                .document(uid).collection(COLLECTION_RESTAURANTS_LIKED);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate, SetOptions.merge());
    }

    public static Task<Void> createUserLikeRestaurant(String uid, String restaurantId, Boolean isLiked){
        Restaurant restaurantLiked = new Restaurant(restaurantId, isLiked);
        return UserHelper.getUsersCollection().document(uid)
                .collection(COLLECTION_RESTAURANTS_LIKED).document(restaurantId).set(restaurantLiked);
    }

    // --- GET ---

    public static Query getExistingUser(String uid){
        return UserHelper.getUsersCollection().whereEqualTo("uid", uid);
    }

    public static Task<DocumentSnapshot> getAllUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Task<DocumentSnapshot> getLikedRestaurant(String uid,String restaurantId){
        return UserHelper.getUsersCollection().document(uid)
                .collection(COLLECTION_RESTAURANTS_LIKED).document(restaurantId).get();
    }

    public static Query getAllJoiningWorkmate(String placeId){
        return UserHelper.getUsersCollection().whereEqualTo("restaurantChoiceId",placeId);
    }

    // --- UPDATE ---

    public static Task<Void> updateUserRestaurantChoiceId(String uid, String userRestaurantChoiceId) {
        return UserHelper.getUsersCollection().document(uid).update("restaurantChoiceId", userRestaurantChoiceId);
    }

    public static Task<Void> updateUserRestaurantChoiceName(String uid, String userRestaurantChoiceName){
        return UserHelper.getUsersCollection().document(uid).update("restaurantChoiceName",userRestaurantChoiceName);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

    public static Task<Void> deleteLikeRestaurant(String uid, String restaurantId){
        return UserHelper.getUsersCollection().document(uid)
                .collection(COLLECTION_RESTAURANTS_LIKED).document(restaurantId).delete();
    }

}