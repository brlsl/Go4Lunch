package com.example.go4lunch.views.restaurant_list_fragment_rv;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<ResultDetails> resultDetailsList;
    private Context mContext;
    private Location mDeviceLocation;

    // constructor with a initialized list / 0 restaurant around
    public RestaurantAdapter(List<ResultDetails> items){
        this.resultDetailsList = items;
    }

    public RestaurantAdapter(List<ResultDetails> items, Context context, Location deviceLocation){
        this.resultDetailsList = items;
        this.mContext = context;
        this.mDeviceLocation = deviceLocation;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_fragment_restaurant_list,parent,false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.displayData(resultDetailsList,mContext,mDeviceLocation, position);
    }

    @Override
    public int getItemCount() {
        return resultDetailsList.size();
    }
}
