package com.example.go4lunch.controllers.activities;


import android.content.Context;
import android.content.Intent;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.example.go4lunch.views.workmates_list_rv_restaurant_detail_activity.JoiningWorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.Query;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailActivity extends BaseActivity {

    // FOR UI
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=";
    private ConstraintLayout mConstraintLayout;

    // FOR DATA
    private Disposable mDisposable;
    private JoiningWorkmateAdapter mAdapter;
    private Context mContext;
    private Boolean mRestaurantIsLiked;
    private Boolean mUserRestaurantIsChosen;


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_restaurant_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConstraintLayout = findViewById(R.id.constraint_layout_restaurant_activity_detail);
        Intent intent = getIntent();
        String restaurantId = Objects.requireNonNull(intent.getStringExtra("PLACE_ID_KEY")); // recupere id

        executeHttpRequestPlaceDetailsWithRetrofit(restaurantId);
        configureRecyclerView(restaurantId);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        mContext = context;
        return super.onCreateView(name, context, attrs);
    }

    private void configureRecyclerView(String restaurantId) {
        RecyclerView mRecyclerView = findViewById(R.id.workmates_joining_list_rv_restaurant_detail_activity);

        Query query = UserHelper.getUsersCollection()
                .whereEqualTo("restaurantChoiceId",restaurantId)
                .orderBy("username", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        mAdapter = new JoiningWorkmateAdapter(options, mContext);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
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

    public void executeHttpRequestPlaceDetailsWithRetrofit(String placeID){
        this.mDisposable = GooglePlaceStreams.streamFetchPlaceDetails(placeID, PLACE_API_KEY).subscribeWith(new DisposableObserver<ResultDetails>() {
            @Override
            public void onNext(ResultDetails resultDetails) {
                TextView mRestaurantName = findViewById(R.id.restaurant_detail_activity_restaurant_name);
                TextView mRestaurantAddress = findViewById(R.id.restaurant_detail_activity_restaurant_address);
                Button mRestaurantButtonPhoneCall = findViewById(R.id.restaurant_activity_detail_call_button);
                Button mRestaurantButtonWebsiteURL  = findViewById(R.id.restaurant_activity_detail_website_button);
                ImageView mRestaurantPhoto = findViewById(R.id.restaurant_detail_activity_restaurant_photo);
                Button mRestaurantLikeButton = findViewById(R.id.restaurant_activity_detail_like_button);
                FloatingActionButton mFabUserRestaurantChoice = findViewById(R.id.restaurant_activity_detail_fab_user_choice);

                if(getCurrentUser() != null) {

                    mRestaurantName.setText(resultDetails.getResult().getName());
                    mRestaurantAddress.setText(resultDetails.getResult().getVicinity());

                    // photo
                    if(resultDetails.getResult().getPhotos() == null || resultDetails.getResult().getPhotos().size() < 10) {
                        Glide.with(getApplicationContext())
                                .load(R.drawable.connect_activity_food)
                                .into(mRestaurantPhoto);
                    }else{
                        int randomNum = (int) (Math.random() * (10));
                        Glide.with(getApplicationContext())
                                .load(BASE_GOOGLE_PHOTO_URL + resultDetails.getResult().getPhotos().get(randomNum).getPhotoReference() + "&key=" +PLACE_API_KEY)
                                .into(mRestaurantPhoto);
                    }

                    // call button
                    mRestaurantButtonPhoneCall.setOnClickListener(v -> {
                        String mRestaurantPhoneNumber = resultDetails.getResult().getFormattedPhoneNumber();
                        if (mRestaurantPhoneNumber != null) {
                        Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", mRestaurantPhoneNumber, null));
                        startActivity(callIntent);
                        } else{
                            Snackbar.make(mConstraintLayout, R.string.no_phone_number_available, Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    // website button
                    mRestaurantButtonWebsiteURL.setOnClickListener(v -> {
                        String mRestaurantWebsite = resultDetails.getResult().getWebsite();
                        if (mRestaurantWebsite != null) {
                            Intent intentURL = new Intent(Intent.ACTION_VIEW);
                            intentURL.setData(Uri.parse(mRestaurantWebsite));
                            startActivity(intentURL);
                        } else{
                            Snackbar.make(mConstraintLayout, R.string.no_website_available, Snackbar.LENGTH_SHORT).show();
                        }
                    });


                    // manage fab view and user restaurant choice in Fire Store
                    UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(userDocumentSnapshot -> {
                        User databaseUser = userDocumentSnapshot.toObject(User.class);
                        assert databaseUser != null;
                        if (databaseUser.getRestaurantChoiceId() != null && databaseUser.getRestaurantChoiceId().equals(placeID) ){
                            mFabUserRestaurantChoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_green_24dp));
                            mUserRestaurantIsChosen = true;
                        }
                        mFabUserRestaurantChoice.setOnClickListener(v -> {
                            if (mUserRestaurantIsChosen == null || !mUserRestaurantIsChosen ) {
                                mUserRestaurantIsChosen = true;
                                UserHelper.updateUserRestaurantChoiceId(getCurrentUser().getUid(), placeID);
                                UserHelper.updateUserRestaurantChoiceName(getCurrentUser().getUid(), resultDetails.getResult().getName());
                                mFabUserRestaurantChoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_green_24dp));
                                Snackbar.make(mConstraintLayout, mContext.getString(R.string.you_eat_at,
                                        resultDetails.getResult().getName()), Snackbar.LENGTH_SHORT).show();
                            }
                            else{
                                mUserRestaurantIsChosen = false;
                                UserHelper.updateUserRestaurantChoiceId(getCurrentUser().getUid(), null);
                                UserHelper.updateUserRestaurantChoiceName(getCurrentUser().getUid(), null);
                                mFabUserRestaurantChoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_restaurant_24));
                                Snackbar.make(mConstraintLayout, mContext.getString(R.string.you_canceled), Snackbar.LENGTH_SHORT).show();
                            }
                        });

                    });

                    // Manage like button view and data in Fire Store
                    UserHelper.getUserLikeRestaurant(getCurrentUser().getUid(), placeID).addOnSuccessListener(documentSnapshot -> {
                        Restaurant restaurantDb = documentSnapshot.toObject(Restaurant.class);

                        if(restaurantDb != null && restaurantDb.getRestaurantIsLiked()){
                            mRestaurantLikeButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            mRestaurantIsLiked = true;
                        }
                        mRestaurantLikeButton.setOnClickListener(v -> {
                            if (mRestaurantIsLiked == null || !mRestaurantIsLiked){
                                UserHelper.createRestaurantLikedByUser(getCurrentUser().getUid(),placeID,true);
                                mRestaurantIsLiked = true;
                                v.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                                Snackbar.make(mConstraintLayout, R.string.you_like_this_restaurant, Snackbar.LENGTH_SHORT).show();
                            }
                            else {
                                UserHelper.deleteRestaurantLikedByUser(getCurrentUser().getUid(),placeID);
                                mRestaurantIsLiked = false;
                                mRestaurantLikeButton.setBackgroundColor(Color.WHITE);
                                Snackbar.make(mConstraintLayout, R.string.you_dislike_this_restaurant, Snackbar.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            }
            @Override
            public void onError(Throwable e) {
                Log.e("TAG","Restaurant Detail Activity on Error:", e);
            }
            @Override
            public void onComplete() {
            }
        });
    }
}
