package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class GooglePlaceStreams {

    private static final String PLACE_DETAILS_SEARCH_FIELDS = "name,photos,rating,formatted_phone_number,vicinity,opening_hours,website,geometry,place_id";
    private static final String PLACE_TYPE = "restaurant";
    private static final String STRICT_BOUNDS = "";


    public static Observable<ResultSearchNearby> streamFetchNearbySearch(String location, String radius, String apiKey){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getNearbyPlaces(location,radius,PLACE_TYPE, apiKey)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    public static Observable<ResultDetails> streamFetchPlaceDetails(String placeID, String apiKey){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getPlaceDetails(placeID, apiKey, PLACE_DETAILS_SEARCH_FIELDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    public static Observable<AutoComplete> streamFetchAutoComplete (String input, String types, String location, String radius,
                                                                    String sessionToken, String key){
        GooglePlaceApiService mService = GooglePlaceApiService.retrofit.create(GooglePlaceApiService.class);
        return mService.getAutoCompletePlaceRequest(input, types, location, radius, STRICT_BOUNDS, sessionToken, key)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .timeout(20, TimeUnit.SECONDS);
    }

    public static Observable<List<ResultDetails>> streamNearbyThenFetchPlaceDetails(String location, String radius, String apiKey){
        return streamFetchNearbySearch(location, radius, apiKey)
                .flatMapIterable(ResultSearchNearby::getResults)
                .flatMap(resultSearchNearby -> streamFetchPlaceDetails(resultSearchNearby.getPlaceId(), apiKey))
                .toList()
                .toObservable()
                .subscribeOn(AndroidSchedulers.mainThread()).timeout(10, TimeUnit.SECONDS);
    }
}
