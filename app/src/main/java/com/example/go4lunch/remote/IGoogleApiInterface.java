package com.example.go4lunch.remote;

import com.example.go4lunch.models.MyPlaces;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface IGoogleApiInterface {
    @GET
    Call<MyPlaces> getNearbyPlaces (@Url String url);

}
