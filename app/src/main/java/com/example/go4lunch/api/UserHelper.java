package com.example.go4lunch.api;

import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture) {
        User userToCreate = new User(uid, username, urlPicture, null, null);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    /*
    public static Task<DocumentSnapshot> getUserRestaurantChoice(String uid, String restaurantChoiceId){
        return UserHelper.getUsersCollection().document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

            }
        });
    }
    */


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

}