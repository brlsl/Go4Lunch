package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.PlaceDetail;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.SearchNearby;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceStreams {

    public static Observable<SearchNearby> streamFetchNearbySearch(String location, int radius, String type, String apiKey){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getNearbyPlaces(location,radius,type, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<PlaceDetail> streamFetchPlaceDetails(String placeID, String apiKey, String searchFields){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getPlaceDetails(placeID, apiKey, searchFields)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }

    public static Observable<AutoComplete> streamFetchAutoComplete (String input, String types, String location, int radius,
                                                                    String strictbounds ,String sessionToken, String key){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getAutoCompletePlaceRequest(input, types, location, radius, strictbounds, sessionToken, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(10, TimeUnit.SECONDS);
    }
}
