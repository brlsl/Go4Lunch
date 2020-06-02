package com.example.go4lunch.views;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=" ;
    private static final String API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE";

    private List<ResultSearchNearby> mRestaurantList;
    private HashMap<LatLng, String> mDictionary;
    private Context mContext;


    public RestaurantAdapter(List<ResultSearchNearby> items, HashMap<LatLng, String> myDictionaryTest, Context context){
        mRestaurantList = items;
        mDictionary = myDictionaryTest;
        this.mContext = context;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant,parent,false);

        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {


        System.out.println("Valeur de mon dictionnaire de RestaurantListFragment dans RestaurantAdapter:" + mDictionary);

        ResultSearchNearby resultsNearby = mRestaurantList.get(position);

        // for opening restaurant detail activity
        double lat = resultsNearby.getGeometry().getLocation().getLat();
        double lng = resultsNearby.getGeometry().getLocation().getLng();
        LatLng latLng = new LatLng(lat, lng);

        holder.mRestaurantName.setText(resultsNearby.getName());
        holder.mRestaurantAddress.setText(resultsNearby.getVicinity());


        if(resultsNearby.getOpeningHours() != null){
            if(resultsNearby.getOpeningHours().getOpenNow())
                holder.mRestaurantOpeningHours.setText("Open");
            else
                holder.mRestaurantOpeningHours.setText("Closed");

        }
        else  {
            holder.mRestaurantOpeningHours.setText("Opening hours not available");
        }

        if(resultsNearby.getPhotos() == null) {
            Glide.with(this.mContext)
                    .load(R.drawable.connect_activity_food)
                    .centerCrop()
                    .into(holder.mRestaurantPhoto);

        }
        else {
        Glide.with(mContext)
                .load(BASE_GOOGLE_PHOTO_URL + resultsNearby.getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                .centerCrop()
                .into(holder.mRestaurantPhoto);
    }

        if (resultsNearby.getRating() != null ) {
            if (resultsNearby.getRating() > 2.3F && resultsNearby.getRating() <= 3.3F) {
                holder.mStar1.setVisibility(View.VISIBLE);
                holder.mStar2.setVisibility(View.GONE);
                holder.mStar3.setVisibility(View.GONE);
            }
            else if (resultsNearby.getRating() > 3.3F && resultsNearby.getRating() < 4.3F){
                holder.mStar1.setVisibility(View.VISIBLE);
                holder.mStar2.setVisibility(View.VISIBLE);
                holder.mStar3.setVisibility(View.GONE);
            }
            else if(resultsNearby.getRating() >= 4.3F) {
                holder.mStar1.setVisibility(View.VISIBLE);
                holder.mStar2.setVisibility(View.VISIBLE);
                holder.mStar3.setVisibility(View.VISIBLE);
            }
        } else {
            holder.mStar1.setVisibility(View.GONE);
            holder.mStar2.setVisibility(View.GONE);
            holder.mStar3.setVisibility(View.GONE);
        }


        // click on item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on ouvre les détails
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                intent.putExtra("POSITION_KEY",latLng);
                intent.putExtra("DICTIONARY_KEY", mDictionary);

                //System.out.println("RestaurantAdapter valeur de position latlng: " +latLng);
                System.out.println("RestaurantAdapter valeur de position dictionnaire: " + mDictionary);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
