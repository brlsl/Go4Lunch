package com.example.go4lunch.views;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=" ;
    private static final String API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE";

    public TextView mRestaurantName, mRestaurantAddress, mRestaurantOpeningHours, mRestaurantDistance;
    public ImageView mRestaurantPhoto, mStar1, mStar2, mStar3;
    String idRestaurant;
   // private HashMap<LatLng, String> mDictionary;
    //private Context mContext;
    //private  Location mDeviceLocation;


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        mRestaurantName = itemView.findViewById(R.id.rv_item_restaurant_name);
        mRestaurantAddress = itemView.findViewById(R.id.rv_item_restaurant_address);
        mRestaurantOpeningHours = itemView.findViewById(R.id.rv_item_opening_hours);
        mRestaurantDistance = itemView.findViewById(R.id.rv_item_restaurant_distance_from_current_position);
        mRestaurantPhoto = itemView.findViewById(R.id.rv_item_workmate_avatar);
        mStar1 = itemView.findViewById(R.id.rv_star_1);
        mStar2 = itemView.findViewById(R.id.rv_star_2);
        mStar3 = itemView.findViewById(R.id.rv_star_3);
    }

    public void displayData(List<ResultSearchNearby> mRestaurantList,
                            Context mContext, Location mDeviceLocation, int position){

        System.out.println("Valeur de mon dictionnaire de RestaurantListFragment dans RestaurantAdapter:" + idRestaurant);

        ResultSearchNearby resultsNearby = mRestaurantList.get(position);
        idRestaurant = resultsNearby.getPlaceId();

        // for opening restaurant detail activity
        double lat = resultsNearby.getGeometry().getLocation().getLat();
        double lng = resultsNearby.getGeometry().getLocation().getLng();
        LatLng restaurantLatLng = new LatLng(lat, lng);

        mRestaurantName.setText(resultsNearby.getName());
        mRestaurantAddress.setText(resultsNearby.getVicinity());


        //TODO: récupérer détail des heures avec placeDetail et place_id
        resultsNearby.getPlaceId();
        if(resultsNearby.getOpeningHours() != null){
            if(resultsNearby.getOpeningHours().getOpenNow())
                mRestaurantOpeningHours.setText("Open");
            else
                mRestaurantOpeningHours.setText("Closed");

        }
        else  {
            mRestaurantOpeningHours.setText("Opening hours not available");
        }

        if(resultsNearby.getPhotos() == null) {
            Glide.with(mContext)
                    .load(R.drawable.connect_activity_food)
                    .centerCrop()
                    .into(mRestaurantPhoto);

        }
        else {
            Glide.with(mContext)
                    .load(BASE_GOOGLE_PHOTO_URL + resultsNearby.getPhotos().get(0).getPhotoReference() + "&key=" + API_KEY)
                    .centerCrop()
                    .into(mRestaurantPhoto);
        }

        if (resultsNearby.getRating() != null) {
            if (resultsNearby.getRating() > 2.3F && resultsNearby.getRating() <= 3.3F) {
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.GONE);
                mStar3.setVisibility(View.GONE);
            }
            else if (resultsNearby.getRating() > 3.3F && resultsNearby.getRating() < 4.3F){
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.GONE);
            }
            else if(resultsNearby.getRating() >= 4.3F) {
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
            }
        } else {
            mStar1.setVisibility(View.GONE);
            mStar2.setVisibility(View.GONE);
            mStar3.setVisibility(View.GONE);
        }

        // initialize location and set latitude/longitude
        Location restaurantLocation = new Location(LocationManager.NETWORK_PROVIDER);
        restaurantLocation.setLatitude(resultsNearby.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(resultsNearby.getGeometry().getLocation().getLng());

        mRestaurantDistance.setText((int) mDeviceLocation.distanceTo(restaurantLocation) +" meters");

        // click on item
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // on ouvre les détails
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                //intent.putExtra("POSITION_KEY",restaurantLatLng);
                intent.putExtra("ID_KEY", idRestaurant);

                //System.out.println("RestaurantAdapter valeur de position latlng: " +latLng);
                System.out.println("RestaurantAdapter valeur de position dictionnaire: " + idRestaurant);

                v.getContext().startActivity(intent);
            }
        });

    }
}
