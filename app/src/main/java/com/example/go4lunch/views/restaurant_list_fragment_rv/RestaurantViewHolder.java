package com.example.go4lunch.views.restaurant_list_fragment_rv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.User;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.utils.DateUtils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class RestaurantViewHolder extends RecyclerView.ViewHolder {
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=" ;
    private static String API_KEY;

    private TextView mRestaurantName, mRestaurantAddress, mRestaurantOpeningHours,
            mRestaurantDistance, mNumberOfInterested;
    private ImageView mRestaurantPhoto, mStar1, mStar2, mStar3;
    private String mRestaurantId;
    private Context mContext;
    private ResultDetails mResultDetails;

    RestaurantViewHolder(@NonNull View itemView) {
        super(itemView);
        API_KEY = itemView.getContext().getString(R.string.google_api_key);
        mContext = itemView.getContext();

        mRestaurantName = itemView.findViewById(R.id.rv_item_restaurant_name);
        mRestaurantAddress = itemView.findViewById(R.id.rv_item_restaurant_address);
        mRestaurantOpeningHours = itemView.findViewById(R.id.rv_item_opening_hours);
        mRestaurantDistance = itemView.findViewById(R.id.rv_item_restaurant_distance_from_current_position);
        mRestaurantPhoto = itemView.findViewById(R.id.rv_item_restaurant_photo);
        mNumberOfInterested = itemView.findViewById(R.id.rv_number_of_workmates_interested);

        mStar1 = itemView.findViewById(R.id.rv_star_1);
        mStar2 = itemView.findViewById(R.id.rv_star_2);
        mStar3 = itemView.findViewById(R.id.rv_star_3);
    }
    void displayData(List<ResultDetails> mRestaurantDetailsList,
                     Context mContext, Location mDeviceLocation, int position){

        // data depends if we receive from a request result or a list from autocomplete result
        if (mRestaurantDetailsList.get(position).getResult() != null){
        mResultDetails = mRestaurantDetailsList.get(position).getResult();
        } else
        {
            mResultDetails = mRestaurantDetailsList.get(position);
        }
        mRestaurantId = mResultDetails.getPlaceId();


        mRestaurantName.setText(mResultDetails.getName());
        mRestaurantAddress.setText(mResultDetails.getVicinity());


        //default picture if no photo of restaurant
        if(mResultDetails.getPhotos() == null) {
            Glide.with(mContext)
                    .load(R.drawable.connect_activity_food)
                    .centerCrop()
                    .into(mRestaurantPhoto);

        }
        else {
            Glide.with(mContext)
                    .load(BASE_GOOGLE_PHOTO_URL + mResultDetails.getPhotos().get(0)
                            .getPhotoReference() + "&key=" + API_KEY)
                    .centerCrop()
                    .into(mRestaurantPhoto);
        }


        if(mResultDetails.getOpeningHours() == null){
            mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
        } else
            updateHoursUI(mResultDetails.getOpeningHours().getWeekdayText());



        if (mResultDetails.getRating() != null) {
            if (mResultDetails.getRating() > 2.3F && mResultDetails.getRating() <= 3.3F) {
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.GONE);
                mStar3.setVisibility(View.GONE);
            }
            else if (mResultDetails.getRating() > 3.3F && mResultDetails.getRating() < 4.3F){
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.GONE);
            }
            else if(mResultDetails.getRating() >= 4.3F) {
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
        restaurantLocation.setLatitude(mResultDetails.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(mResultDetails.getGeometry().getLocation().getLng());
        mRestaurantDistance.setText(mContext.getString(R.string.meters, (int) mDeviceLocation.distanceTo(restaurantLocation)));

        // number of workmates joining
        UserHelper.getUsersCollection()
                .whereEqualTo("restaurantChoiceId", mRestaurantId).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> userList = new ArrayList<>();

                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                        User user = documentSnapshot.toObject(User.class);
                        userList.add(user);
                    }
                    String number = String.valueOf(userList.size());
                    mNumberOfInterested.setText(number);

                });

        // click on item
        itemView.setOnClickListener(v -> {
            // open restaurant details
            Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("PLACE_ID_KEY", mRestaurantId);
            v.getContext().startActivity(intent);
        });

    }

    private void updateHoursUI(List<String> restaurantHours) {
        String todayString = DateUtils.getTodayDateToStr(); // monday, tuesday, ... sunday
        int todayInteger = DateUtils.getTodayDateToInteger(todayString); // 0,1,... 6
        String restaurantTodayHours = restaurantHours.get(todayInteger); //Tuesday: 11:00 AM – 11:00 PM
        String[] hoursWithoutDay = restaurantTodayHours.toLowerCase().split(todayString+": "); //11:00 AM – 11:00 PM

        System.out.println("Horaires du jour: " +restaurantTodayHours);
        System.out.println("Jour en STR:" +DateUtils.getTodayDateToStr());
        System.out.println("Jour en integer:" +todayInteger);
        System.out.println("Horaires sans jour:" +hoursWithoutDay[1]);

        if (restaurantTodayHours.toLowerCase().contains(todayString)){
            if (restaurantTodayHours.toLowerCase().contains("closed"))
                mRestaurantOpeningHours.setText(R.string.closed);
            else if (restaurantTodayHours.toLowerCase().contains("open"))
                mRestaurantOpeningHours.setText(R.string.open);
            else if (restaurantTodayHours.toLowerCase().contains(","))
                morningEveningSchedule(hoursWithoutDay);
            else if(restaurantTodayHours.toLowerCase().contains("–"))
                fullDaySchedule(hoursWithoutDay);
            else
                mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
        } else
            mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
    }

    private void morningEveningSchedule(String[] hoursWithoutDay){
        String hours = hoursWithoutDay[1].trim(); //" 11:30 am – 1:30 pm, 6:00 – 9:30 pm"
        String[] splitHours = hours.split(",");

        String morningHours = splitHours[0].trim(); //"11:30 am – 1:30 pm"
        String[] splitMorning = morningHours.split("–");

        String openMorning = splitMorning[0].trim(); //11:30 am
        if (!openMorning.contains("am"))
            openMorning = openMorning +" am";

        String closeMorning = splitMorning[1].trim(); //" 1:30 pm"
        if(!closeMorning.contains("pm"))
            closeMorning = closeMorning + " pm";

        String eveningHours = splitHours[1].trim(); // 6:00 – 9:30 PM
        String[] splitEvening = eveningHours.split("–");

        String openEvening = splitEvening[0].trim();// 6:00
        if(!openEvening.contains("pm"))
            openEvening = openEvening +" pm";

        String closeEvening = splitEvening[1].trim(); // 9:30 PM;

        try {
            if (DateUtils.currentHourCompareToRestaurantHour(openMorning) <0 )// current hours before morning open hours
                mRestaurantOpeningHours.setText(itemView.getContext().getString(R.string.open_at, openMorning));
            else if (DateUtils.currentHourCompareToRestaurantHour(closeMorning)<0){ // current hours before closing hours
                mRestaurantOpeningHours.setText(itemView.getContext().getString(R.string.open_from_until, openMorning, closeMorning));
                if (DateUtils.isClosingSoon(closeMorning)) { // if closing <= 45 minutes
                    configureClosingSoonText();
                }
            }
            else if (DateUtils.currentHourCompareToRestaurantHour(openEvening) <0) // current hours before evening open hours
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_at,openEvening));
            else if (DateUtils.currentHourCompareToRestaurantHour(closeEvening) < 0){ // current hours before evening closing hours
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_from_until, openEvening,closeEvening));
                if (DateUtils.isClosingSoon(closeEvening)) { // if closing <= 45 minutes
                    configureClosingSoonText();
                }
            }
            else
                mRestaurantOpeningHours.setText(R.string.closed);
        } catch (ParseException e) {
            System.out.println("RestaurantViewHolder morning evening schedule"+e.getMessage());
        }
    }

    private void configureClosingSoonText() {
        mRestaurantOpeningHours.setText(R.string.closing_soon);
        mRestaurantOpeningHours.setTextColor(Color.RED);
        mRestaurantOpeningHours.setTypeface(mRestaurantOpeningHours.getTypeface(), Typeface.BOLD);
    }

    private void fullDaySchedule(String[] hoursWithoutDay) {
        String dayHours = hoursWithoutDay[1].trim(); //11:00 AM – 11:00 PM
        String[] splitDay = dayHours.split("–");
        String openDay = splitDay[0].trim(); // 11:00 AM
        if (!openDay.contains("am"))
            openDay = openDay+ " am";
        String closeDay = splitDay[1].trim(); // 11:00 PM
        if(!closeDay.contains(" pm") && !closeDay.contains("am"))
            closeDay = closeDay + " pm";
        try {
            if (DateUtils.currentHourCompareToRestaurantHour(openDay) <0)
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_at, openDay));
            else if (DateUtils.currentHourCompareToRestaurantHour(closeDay) <0) {
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_from_until, openDay, closeDay));
                if(DateUtils.isClosingSoon(closeDay)){
                    configureClosingSoonText(); // ok works
                }
            }
            else if(DateUtils.compareOpeningHoursToCloseHours(openDay,closeDay) <0)
                mRestaurantOpeningHours.setText(mContext.getString(R.string.close_tomorrow_at, closeDay));
            else if(DateUtils.compareOpeningHoursToCloseHours(openDay,closeDay) > 0)
                mRestaurantOpeningHours.setText(R.string.closed);
            else
                mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
        } catch (ParseException e) {
            System.out.println("RestaurantViewHolder full day schedule"+e.getMessage());
        }
    }
}
