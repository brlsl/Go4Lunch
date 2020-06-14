package com.example.go4lunch.controllers.activities;


import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.PlaceDetail;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailActivity extends BaseActivity {

    private static final String DETAIL_FIELDS = "name,photos,rating,formatted_phone_number,vicinity,opening_hours,website";
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=";

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String id = Objects.requireNonNull(intent.getStringExtra("PLACE_ID_KEY")); // recupere id

        executeHttpRequestPlaceDetailsWithRetrofit(id);

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_restaurant_detail;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeWhenDestroy();
    }
    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed())
            this.mDisposable.dispose();

    }

    private void executeHttpRequestPlaceDetailsWithRetrofit(String placeID){
        this.mDisposable = GooglePlaceStreams.streamFetchPlaceDetails(placeID, PLACE_API_KEY, DETAIL_FIELDS).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                TextView mRestaurantName = findViewById(R.id.restaurant_detail_activity_restaurant_name);
                TextView mRestaurantAddress = findViewById(R.id.restaurant_detail_activity_restaurant_address);
                Button mRestaurantButtonPhoneCall = findViewById(R.id.restaurant_activity_detail_call_button);
                Button mRestaurantButtonWebsiteURL  = findViewById(R.id.restaurant_activity_detail_website_button);
                ImageView mRestaurantPhoto = findViewById(R.id.restaurant_detail_activity_restaurant_photo);
                Button mRestaurantLikeButton = findViewById(R.id.restaurant_activity_detail_like_button);
                FloatingActionButton mUserRestaurantChoice = findViewById(R.id.restaurant_activity_detail_fab_user_choice);

                mRestaurantName.setText(placeDetail.getResult().getName());
                mRestaurantAddress.setText(placeDetail.getResult().getVicinity());

                if(placeDetail.getResult().getPhotos() == null || placeDetail.getResult().getPhotos().size() < 10) {
                    Glide.with(getApplicationContext())
                            .load(R.drawable.connect_activity_food)
                            .into(mRestaurantPhoto);
                }else{
                    int randomNum = (int) (Math.random() * (10));
                    Glide.with(getApplicationContext())
                            .load(BASE_GOOGLE_PHOTO_URL + placeDetail.getResult().getPhotos().get(randomNum).getPhotoReference() + "&key=" +PLACE_API_KEY)
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
                            Toast.makeText(RestaurantDetailActivity.this, R.string.no_phone_number_available, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(RestaurantDetailActivity.this, R.string.no_website_available, Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mRestaurantLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (getCurrentUser() != null){
                            //placeDetail.getResult().getOpeningHours().getPeriods();
                        }
                    }
                });

                mUserRestaurantChoice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getCurrentUser() != null ){
                            UserHelper.updateUserRestaurantChoice(getCurrentUser().getUid(), placeID);
                            Toast.makeText(RestaurantDetailActivity.this, "Id du restaurant " +placeID, Toast.LENGTH_SHORT).show();
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
