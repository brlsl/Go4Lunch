package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;

import io.reactivex.Observable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GooglePlaceApiService {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            .build();

    @GET("nearbysearch/json?")
    Observable<ResultSearchNearby> getNearbyPlaces (@Query("location")String location, @Query("radius") String radius,
                                                    @Query("type") String type, @Query("key") String apiKey);

    @GET("details/json?")
    Observable<ResultDetails> getPlaceDetails (@Query("place_id") String placeID, @Query("key") String apiKey,
                                               @Query("fields") String searchFields);

    @GET("autocomplete/json?")
    Observable<AutoComplete> getAutoCompletePlaceRequest(@Query("input") String input, @Query("types")String types,
                                                         @Query("location")String location, @Query("radius") String radius,
                                                         @Query("strictbounds")String strictbounds, @Query("sessiontoken")String sessionToken,
                                                         @Query("key")String apiKey);


}
