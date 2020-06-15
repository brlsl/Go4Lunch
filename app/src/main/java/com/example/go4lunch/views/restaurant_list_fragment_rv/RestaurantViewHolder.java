package com.example.go4lunch.views.restaurant_list_fragment_rv;

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

import java.util.List;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=" ;
    private static final String API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE";

    private TextView mRestaurantName, mRestaurantAddress, mRestaurantOpeningHours, mRestaurantDistance;
    private ImageView mRestaurantPhoto, mStar1, mStar2, mStar3;
    private String restaurantId;
   // private HashMap<LatLng, String> mDictionary;
    //private Context mContext;
    //private  Location mDeviceLocation;


    public RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        mRestaurantName = itemView.findViewById(R.id.rv_item_restaurant_name);
        mRestaurantAddress = itemView.findViewById(R.id.rv_item_restaurant_address);
        mRestaurantOpeningHours = itemView.findViewById(R.id.rv_item_opening_hours);
        mRestaurantDistance = itemView.findViewById(R.id.rv_item_restaurant_distance_from_current_position);
        mRestaurantPhoto = itemView.findViewById(R.id.rv_item_restaurant_photo);
        mStar1 = itemView.findViewById(R.id.rv_star_1);
        mStar2 = itemView.findViewById(R.id.rv_star_2);
        mStar3 = itemView.findViewById(R.id.rv_star_3);
    }

    public void displayData(List<ResultSearchNearby> mRestaurantList,
                            Context mContext, Location mDeviceLocation, int position){

        System.out.println("Valeur de mon dictionnaire de RestaurantListFragment dans RestaurantAdapter:" + restaurantId);

        ResultSearchNearby resultsNearby = mRestaurantList.get(position);
        restaurantId = resultsNearby.getPlaceId();
/*
        // for opening restaurant detail activity
        double lat = resultsNearby.getGeometry().getLocation().getLat();
        double lng = resultsNearby.getGeometry().getLocation().getLng();
        LatLng restaurantLatLng = new LatLng(lat, lng);
*/
        mRestaurantName.setText(resultsNearby.getName());
        mRestaurantAddress.setText(resultsNearby.getVicinity());


        //TODO: récupérer détail des heures avec placeDetail et place_id
        if(resultsNearby.getOpeningHours() != null){
            if(resultsNearby.getOpeningHours().getOpenNow())
                mRestaurantOpeningHours.setText(R.string.open);
            else
                mRestaurantOpeningHours.setText(R.string.closed);

        }
        else  {
            mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
        }

        //default picture if no photo of restaurant
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

        mRestaurantDistance.setText((int) mDeviceLocation.distanceTo(restaurantLocation) + " meters");

        // click on item
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open restaurant details
                Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
                intent.putExtra("PLACE_ID_KEY", restaurantId);

                v.getContext().startActivity(intent);
            }
        });

    }
}
