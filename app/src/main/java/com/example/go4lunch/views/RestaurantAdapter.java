package com.example.go4lunch.views;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {
    private List<ResultSearchNearby> mRestaurantList;
    private HashMap<LatLng, String> mDictionnary;


    public RestaurantAdapter(List<ResultSearchNearby> items, HashMap<LatLng, String> myDictionaryTest){
        mRestaurantList = items;
        mDictionnary = myDictionaryTest;

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
        System.out.println("Valeur de mon dictionnaire de RestaurantListFragment dans RestaurantAdapter:" + mDictionnary);

        ResultSearchNearby results = mRestaurantList.get(position);
        double lat =results.getGeometry().getLocation().getLat();
        double lng = results.getGeometry().getLocation().getLng();

        LatLng latLng= new LatLng(lat,lng);

        holder.mRestaurantName.setText(results.getName());
        holder.mRestaurantAddress.setText(results.getVicinity());
        //holder.mRestaurantDistance.setText(results);
        // holder.mRestaurantOpeningHours.setText(results.getOpeningHours());
        //holder.mRestaurantPhoto.setImageURI(results.get);

        // click on item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // récupère dictionnaire et position
                //HashMap<LatLng,String> hashMap = (HashMap<LatLng, String>)intent.getSerializableExtra("test"); // intent pas bon ici
                //LatLng latLng = Objects.requireNonNull(intent.getExtras()).getParcelable("POSITION_KEY2"); // // intent pas bon ici

                // on ouvre les détails
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                intent.putExtra("POSITION_KEY",latLng);
                intent.putExtra("DICTIONARY_KEY", mDictionnary);

                //System.out.println("RestaurantAdapter valeur de position latlng: " +latLng);
                System.out.println("RestaurantAdapter valeur de position dictionnaire: " +mDictionnary);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }
}
