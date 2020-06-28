package com.example.go4lunch.utils;

import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
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

    //----------------------------------
    private Observable<String> getObservable(){
        return Observable.just("Cool");
    }

    private DisposableObserver<String> getSubscriber(){
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
/*
    private void streamShowString(){
        this.disposable = this.getObservable()
                .subscribeWith(getSubscriber());
    }
//------------------------
    private void streamShowString(){
        this.disposable = this.getObservable()
                .map(getFunctionUpperCase()) // 2 - Apply function
                .subscribeWith(getSubscriber());
    }

    // 1 - Create function to Uppercase a string
    private Function<String, String> getFunctionUpperCase(){
        return new Function<String, String>() {
            @Override
            public String apply(String s) throws Exception {
                return s.toUpperCase();
            }
        };
    }
//---------------------------------

    public static void combineNearbyAndPlaceDetail(){
        streamFetchNearbySearch()
                .flatMap(streamFetchPlaceDetails())
                .subscribeWith(new DisposableObserver<PlaceDetail>() {
                    @Override
                    public void onNext(PlaceDetail placeDetail) {
                        placeDetail.getResult().getOpeningHours().getWeekdayText();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void streamShowString(){
        this.disposable = this.getObservable()
                .map(getFunctionUpperCase())
                .flatMap(getSecondObservable()) // 2 - Adding Observable
                .subscribeWith(getSubscriber());
    }

    // 1 - Create a function that will calling a new observable
    private Function<String, Observable<String>> getSecondObservable(){
        return new Function<String, Observable<String>>() {
            @Override
            public Observable<String> apply(String previousString) throws Exception {
                return Observable.just(previousString+" I love Openclassrooms !");
            }
        };
    }
*/

}
