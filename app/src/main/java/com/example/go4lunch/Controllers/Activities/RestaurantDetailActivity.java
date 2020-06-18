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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.PlaceDetail;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.example.go4lunch.views.workmates_list_rv_restaurant_detail_activity.JoiningWorkmateAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

public class RestaurantDetailActivity extends BaseActivity {

    private static final String DETAIL_FIELDS = "name,photos,rating,formatted_phone_number,vicinity,opening_hours,website";
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=1600&photoreference=";

    private Disposable mDisposable;
    private RecyclerView mRecyclerView;
    private JoiningWorkmateAdapter mAdapter;
    private Context mContext;

    private Boolean isLiked;


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_restaurant_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
        setContentView(R.layout.activity_restaurant_detail);
        mRecyclerView = findViewById(R.id.workmates_joining_list_rv_restaurant_detail_activity);

        Query query = UserHelper.getAllJoiningWorkmate(restaurantId).orderBy("username", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        mAdapter = new JoiningWorkmateAdapter(options,restaurantId, mContext);
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
        this.mDisposable = GooglePlaceStreams.streamFetchPlaceDetails(placeID, PLACE_API_KEY, DETAIL_FIELDS).subscribeWith(new DisposableObserver<PlaceDetail>() {
            @Override
            public void onNext(PlaceDetail placeDetail) {
                TextView mRestaurantName = findViewById(R.id.restaurant_detail_activity_restaurant_name);
                TextView mRestaurantAddress = findViewById(R.id.restaurant_detail_activity_restaurant_address);
                Button mRestaurantButtonPhoneCall = findViewById(R.id.restaurant_activity_detail_call_button);
                Button mRestaurantButtonWebsiteURL  = findViewById(R.id.restaurant_activity_detail_website_button);
                ImageView mRestaurantPhoto = findViewById(R.id.restaurant_detail_activity_restaurant_photo);
                Button mRestaurantLikeButton = findViewById(R.id.restaurant_activity_detail_like_button);
                FloatingActionButton mFabUserRestaurantChoice = findViewById(R.id.restaurant_activity_detail_fab_user_choice);

                if(getCurrentUser() != null) {

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


                    // manage view and user restaurant choice  in firestore
                    UserHelper.getAllUser(getCurrentUser().getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot userDocumentSnapshot) {
                            User databaseUser = userDocumentSnapshot.toObject(User.class);
                            if (databaseUser.getRestaurantChoiceId() != null && databaseUser.getRestaurantChoiceId().equals(placeID) ){
                                mFabUserRestaurantChoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_green_24dp));
                                mFabUserRestaurantChoice.setClickable(false);
                            }
                            mFabUserRestaurantChoice.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    UserHelper.updateUserRestaurantChoiceId(getCurrentUser().getUid(), placeID);
                                    UserHelper.updateUserRestaurantChoiceName(getCurrentUser().getUid(), placeDetail.getResult().getName());
                                    mFabUserRestaurantChoice.setImageDrawable(getResources().getDrawable(R.drawable.ic_check_circle_green_24dp));
                                }
                            });

                        }
                    });
/*
                    UserHelper.getRestaurantCollection(getCurrentUser().getUid())
                            .whereEqualTo("restaurantId", placeID)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                                    if (e != null){
                                        Log.w("Restaurant Detail", "listen:error", e);
                                    }
                                    for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                        switch (dc.getType()) {
                                            case ADDED:
                                                Log.d("Restaurant Detail", "New city: " + dc.getDocument().getData());
                                                break;
                                            case MODIFIED:
                                                Log.d("Restaurant Detail", "Modified city: " + dc.getDocument().getData());
                                                break;
                                            case REMOVED:
                                                Log.d("Restaurant Detail", "Removed city: " + dc.getDocument().getData());
                                                break;
                                        }
                                    }
                                }
                            });
*/
                    // Manage like button view and data in firestore
                    UserHelper.getLikedRestaurant(getCurrentUser().getUid(), placeID).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Restaurant restaurantDb = documentSnapshot.toObject(Restaurant.class);

                            if(restaurantDb != null && restaurantDb.getRestaurantIsLiked()){
                                mRestaurantLikeButton.setBackgroundColor(Color.GRAY);
                                isLiked = true;
                            }
                            mRestaurantLikeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (isLiked == null || !isLiked){
                                        UserHelper.createUserLikeRestaurant(getCurrentUser().getUid(),placeID,true);
                                        isLiked = true;
                                        v.setBackgroundColor(Color.GRAY);
                                        Toast.makeText(getApplicationContext(), "You like this restaurant", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        UserHelper.deleteLikeRestaurant(getCurrentUser().getUid(),placeID);
                                        isLiked = false;
                                        mRestaurantLikeButton.setBackgroundColor(Color.WHITE);
                                        Toast.makeText(getApplicationContext(), "You dislike this restaurant", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
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
