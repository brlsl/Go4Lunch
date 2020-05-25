package com.example.go4lunch.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    public TextView mRestaurantName;


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        mRestaurantName = itemView.findViewById(R.id.restaurant_name);

    }
}
