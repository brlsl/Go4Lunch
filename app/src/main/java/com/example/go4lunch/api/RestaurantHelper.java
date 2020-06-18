package com.example.go4lunch.api;

import com.example.go4lunch.models.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RestaurantHelper {
    private static final String COLLECTION_RESTAURANTS = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_RESTAURANTS);
    }

    // --- CREATE ---
  /*
    public static Task<Void> createRestaurant(String restaurantId) {
        Restaurant restaurantToCreate = new Restaurant(restaurantId);
        return RestaurantHelper.getRestaurantsCollection().document(restaurantId).set(restaurantToCreate);
    }
*/
    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String restaurantId){
        return RestaurantHelper.getRestaurantsCollection().document(restaurantId).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateRestaurantNumberOfLike(String restaurantId, int like){
        return getRestaurantsCollection().document(restaurantId).update("numberOfLike", like );
    }

}
