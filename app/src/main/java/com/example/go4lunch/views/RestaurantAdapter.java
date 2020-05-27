package com.example.go4lunch.views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.apiGooglePlace.SearchResult;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    private List<SearchResult> mRestaurantList;


    public RestaurantAdapter(List<SearchResult> items){
        mRestaurantList = items;
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
        SearchResult results = mRestaurantList.get(position);
        holder.mRestaurantName.setText(results.getName());
        holder.mRestaurantAddress.setText(results.getVicinity());
        //holder.mRestaurantDistance.setText(results);
        // holder.mRestaurantOpeningHours.setText(results.getOpeningHours());
        //holder.mRestaurantPhoto.setImageURI(results.get);

        // click on item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
