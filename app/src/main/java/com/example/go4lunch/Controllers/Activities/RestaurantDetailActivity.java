package com.example.go4lunch.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.example.go4lunch.R;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.PlaceDetail;
import com.example.go4lunch.utils.GooglePlaceApiService;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE" ;

    private static final String DETAIL_FIELDS = "name,photos,rating,formatted_phone_number,formatted_address,opening_hours,website";

    Disposable mDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        Intent intent = getIntent();
        HashMap<LatLng,String> hashMap = (HashMap<LatLng, String>)intent.getSerializableExtra("DICTIONARY_KEY"); // récupère dictionnaire
        LatLng latLng = Objects.requireNonNull(intent.getExtras()).getParcelable("POSITION_KEY"); // recupere la position

        executeHttpRequestPlaceDetailsWithRetrofit(hashMap.get(latLng));

        //Log.v("HashMapTest", hashMap.get(0));
        //System.out.println("valeur 0" + hashMap.get(0));
        System.out.println("valeur dictionnaire" + hashMap);
        for(LatLng key : hashMap.keySet()){
            System.out.println("parcours clé: " + key);
        }

        for(String value : hashMap.values()){
            System.out.println("parcours valeur: " + value);
        }

        System.out.println("Restaurant detail valeur de position latlng:" +latLng);
        //Log.v("HashMapTest", hashMap.get(0));
        //mRestaurantName.setText(getIntent().getStringExtra("name"));

    }

    private void executeHttpRequestPlaceDetailsWithRetrofit(String placeID){
        this.mDisposable = GooglePlaceStreams.streamFetchPlaceDetails(placeID, API_KEY, DETAIL_FIELDS).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                TextView mRestaurantName = findViewById(R.id.restaurant_detail_activity_restaurant_name);
                mRestaurantName.setText(placeDetail.getResult().getName());

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });

    }

}
