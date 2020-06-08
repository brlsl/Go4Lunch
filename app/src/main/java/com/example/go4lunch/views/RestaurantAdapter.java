package com.example.go4lunch.views;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.Prediction;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {


    private List<ResultSearchNearby> mRestaurantList;

    private HashMap<LatLng, String> mDictionary;
    private Context mContext;
    private  Location mDeviceLocation;

    private List<Prediction> mRestaurantListPrediction;

    public RestaurantAdapter(List<ResultSearchNearby> items, HashMap<LatLng, String> dictionary, Context context, Location deviceLocation){
        this.mRestaurantList = items;
        this.mDictionary = dictionary;
        this.mContext = context;
        this.mDeviceLocation = deviceLocation;
    }
    public RestaurantAdapter(List<Prediction> items){
        this.mRestaurantListPrediction = items;
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
        holder.displayData(mRestaurantList,mDictionary,mContext,mDeviceLocation, position);

    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
