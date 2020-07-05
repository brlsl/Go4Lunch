package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;


import java.util.Collections;

import java.util.List;

public class SortUtils {
    public static void sortRestaurantByNameAZ(List<ResultDetails> resultDetailsList){
        Collections.sort(resultDetailsList, (o1, o2) ->
                o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
    }

    public static void sortHighRatingFirst(List<ResultDetails> resultDetailsList){
        Collections.sort(resultDetailsList,
                (o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));
    }
}
