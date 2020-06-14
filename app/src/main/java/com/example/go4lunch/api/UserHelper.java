package com.example.go4lunch.api;


import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserHelper {

    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String username, String urlPicture, String restaurantChoiceId) {
        User userToCreate = new User(uid, username, urlPicture, restaurantChoiceId);
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

    public static Task<QuerySnapshot> getUserRestaurantChoice(String userRestaurantChoice){
        return UserHelper.getUsersCollection().whereEqualTo("userRestaurantChoice",userRestaurantChoice).get();
    }

    // --- UPDATE ---

    public static Task<Void> updateUserRestaurantChoice(String uid, String userRestaurantChoiceId) {
        return UserHelper.getUsersCollection().document(uid).update("userRestaurantChoiceId", userRestaurantChoiceId);
    }


    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

}