package com.example.go4lunch.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.PlaceDetail;
import com.example.go4lunch.utils.GooglePlaceApiService;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailActivity extends AppCompatActivity {

    private static final String API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE" ;

    private static final String DETAIL_FIELDS = "name,photos,rating,formatted_phone_number,vicinity,opening_hours,website";
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=";


    Disposable mDisposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        Intent intent = getIntent();
        HashMap<LatLng,String> hashMap = (HashMap<LatLng, String>)intent.getSerializableExtra("DICTIONARY_KEY"); // récupère dictionnaire
        LatLng latLng = Objects.requireNonNull(intent.getExtras()).getParcelable("POSITION_KEY"); // recupere la position

        executeHttpRequestPlaceDetailsWithRetrofit(hashMap.get(latLng));

    }

    private void executeHttpRequestPlaceDetailsWithRetrofit(String placeID){
        this.mDisposable = GooglePlaceStreams.streamFetchPlaceDetails(placeID, API_KEY, DETAIL_FIELDS).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                TextView mRestaurantName = findViewById(R.id.restaurant_detail_activity_restaurant_name);
                TextView mRestaurantAddress = findViewById(R.id.restaurant_detail_activity_restaurant_address);
                Button mRestaurantButtonPhoneCall = findViewById(R.id.restaurant_activity_detail_call_button);
                Button mRestaurantButtonWebsiteURL  = findViewById(R.id.restaurant_activity_detail_website_button);
                ImageView mRestaurantPhoto = findViewById(R.id.restaurant_detail_activity_restaurant_photo);

                mRestaurantName.setText(placeDetail.getResult().getName());
                mRestaurantAddress.setText(placeDetail.getResult().getVicinity());

                if(placeDetail.getResult().getPhotos() == null || placeDetail.getResult().getPhotos().size() < 10) {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.connect_activity_food)
                            .into(mRestaurantPhoto);
                }else{
                    int randomNum = (int) (Math.random() * (10));
                    Glide.with(getApplicationContext())
                            .load(BASE_GOOGLE_PHOTO_URL + placeDetail.getResult().getPhotos().get(randomNum).getPhotoReference() + "&key=" +API_KEY)
                            .into(mRestaurantPhoto);
                    System.out.println("Value of random integer:" +randomNum);
                }



                mRestaurantButtonPhoneCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mRestaurantPhoneNumber = placeDetail.getResult().getFormattedPhoneNumber();
                        if (mRestaurantPhoneNumber != null) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mRestaurantPhoneNumber, null));
                        startActivity(callIntent);
                        } else{
                            Toast.makeText(RestaurantDetailActivity.this, "Restaurant has no phone number :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mRestaurantButtonWebsiteURL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String mRestaurantWebsite = placeDetail.getResult().getWebsite();
                        if (mRestaurantWebsite != null) {
                            Intent intentURL = new Intent(Intent.ACTION_VIEW);
                            intentURL.setData(Uri.parse(mRestaurantWebsite));
                            startActivity(intentURL);
                        } else{
                            Toast.makeText(RestaurantDetailActivity.this, "Restaurant has no website :(", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

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
