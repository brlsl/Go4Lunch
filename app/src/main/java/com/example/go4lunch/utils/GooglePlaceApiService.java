package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.MyPlaces;

import io.reactivex.Observable;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GooglePlaceApiService {
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/place/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    @GET("nearbysearch/json?")
    Observable<MyPlaces> getNearbyPlaces (@Query("location")String location, @Query("radius") int radius,
                                          @Query("type") String type, @Query("key") String apiKey);

}
