package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.MyPlaces;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceStreams {

    public static Observable<MyPlaces> streamFetchNearbySearch(String location, int radius, String type, String apiKey){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getNearbyPlaces(location,radius,type, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
