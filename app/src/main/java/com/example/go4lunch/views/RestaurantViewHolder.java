package com.example.go4lunch.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    public TextView mRestaurantName;
    public TextView mRestaurantAddress;
    public TextView mRestaurantOpeningHours;
    public TextView mRestaurantDistance;
    public ImageView mRestaurantPhoto;


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        mRestaurantName = itemView.findViewById(R.id.rv_item_restaurant_name);
        mRestaurantAddress = itemView.findViewById(R.id.rv_item_restaurant_address);
        mRestaurantOpeningHours = itemView.findViewById(R.id.rv_item_opening_hours);
        mRestaurantDistance = itemView.findViewById(R.id.rv_item_restaurant_distance_from_current_position);
        //mRestaurantPhoto = itemView.findViewById(R.id.restaurant_photo);

    }
}
