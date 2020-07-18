package com.example.go4lunch.views.restaurant_list_fragment_rv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import com.example.go4lunch.utils.Utils;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

class RestaurantViewHolder extends RecyclerView.ViewHolder {

    // ----- FOR DATA -----

    private static String API_KEY;
    private Context mContext;

    // ----- FOR UI -----
    private TextView mRestaurantName, mRestaurantAddress, mRestaurantOpeningHours,
            mRestaurantDistance, mNumberOfInterested;
    private ImageView mRestaurantPhoto, mStar1, mStar2, mStar3;
    private String mRestaurantId, mOpenMorning, mCloseMorning, mOpenEvening, mCloseEvening;
    private static final String BASE_GOOGLE_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=300&photoreference=" ;

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
    void displayData(List<ResultDetails> restaurantDetailsList,
                     Context mContext, Location mDeviceLocation, int position){

        mResultDetails = restaurantDetailsList.get(position);
        mRestaurantId = mResultDetails.getPlaceId();

        configureTextsFields();
        configureDistance(mDeviceLocation);
        configurePicture();
        configureRating();
        configureJoiningWorkmates();

        // click on item
        itemView.setOnClickListener(v -> {
            // open restaurant details
            Intent intent = new Intent(v.getContext(), RestaurantDetailActivity.class);
            intent.putExtra("PLACE_ID_KEY", mRestaurantId);
            v.getContext().startActivity(intent);
        });

        if (mRestaurantOpeningHours.getText().toString().equals(mContext.getString(R.string.closing_soon))){
            mRestaurantOpeningHours.setTextColor(Color.RED);
        } else{
            mRestaurantOpeningHours.setTextColor(Color.BLUE);
        }
    }

    // ----- CONFIGURE DATA -----

    private void configureTextsFields() {
        mRestaurantName.setText(mResultDetails.getName());
        mRestaurantAddress.setText(mResultDetails.getVicinity());

        if(mResultDetails.getOpeningHours() == null){
            mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
        } else
            updateHoursUI(mResultDetails.getOpeningHours().getWeekdayText());
    }

    private void configureJoiningWorkmates() {
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
    }

    private void configureRating() {
        if (mResultDetails.getRating() != null) {
            if (mResultDetails.getRating() <2.3){
                mStar1.setVisibility(View.GONE);
                mStar2.setVisibility(View.GONE);
                mStar3.setVisibility(View.GONE);
            }
            else if (mResultDetails.getRating() > 2.3F && mResultDetails.getRating() <= 3.3F) {
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
        }
    }


    private void configureDistance(Location mDeviceLocation) {
        // initialize location and set latitude/longitude
        Location restaurantLocation = new Location(LocationManager.NETWORK_PROVIDER);
        restaurantLocation.setLatitude(mResultDetails.getGeometry().getLocation().getLat());
        restaurantLocation.setLongitude(mResultDetails.getGeometry().getLocation().getLng());
        mRestaurantDistance.setText(mContext.getString(R.string.meters, (int) mDeviceLocation.distanceTo(restaurantLocation)));
    }

    private void configurePicture() {
        //default picture if no photo of restaurant
        if(mResultDetails.getPhotos() == null) {
            Glide.with(mContext)
                    .load(R.drawable.ic_meal_24dp)
                    .centerCrop()
                    .into(mRestaurantPhoto);
        }
        else {
            Glide.with(mContext)
                    .load(BASE_GOOGLE_PHOTO_URL + mResultDetails.getPhotos().get(0).getPhotoReference()
                            +"&key=" + API_KEY)
                    .centerCrop()
                    .into(mRestaurantPhoto);
        }
    }

    private void updateHoursUI(List<String> restaurantHours) {
        String todayString = Utils.getTodayDateToStr(); // monday, tuesday, ... sunday
        int todayInteger = Utils.getTodayToInteger(todayString); // 0,1,... 6
        String restaurantTodayHours = restaurantHours.get(todayInteger); //Tuesday: 11:00 AM – 11:00 PM
        String[] hoursWithoutDay = restaurantTodayHours.toLowerCase().split(todayString+": "); //11:00 AM – 11:00 PM
        String currentHour = Utils.getCurrentHourToStr();

        if (restaurantTodayHours.toLowerCase().contains(todayString)){
            if (restaurantTodayHours.toLowerCase().contains("closed"))
                mRestaurantOpeningHours.setText(R.string.closed);
            else if (restaurantTodayHours.toLowerCase().contains("open"))
                mRestaurantOpeningHours.setText(R.string.open);
            else if (restaurantTodayHours.toLowerCase().contains(","))
                morningEveningSchedule(hoursWithoutDay, currentHour);
            else if(restaurantTodayHours.toLowerCase().contains("–"))
                fullDaySchedule(hoursWithoutDay, currentHour);
            else
                mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
        } else
            mRestaurantOpeningHours.setText(R.string.opening_hours_not_available);
    }

    private void morningEveningSchedule(String[] hoursWithoutDay, String currentHour){
        configureMorningEveningHours(hoursWithoutDay);
        try {
            if (Utils.hour1CompareToHour2(currentHour,mCloseEvening) < 0 && Utils.hour1CompareToHour2(mCloseEvening,mOpenMorning)<0){
                mRestaurantOpeningHours.setText(mContext.getString(R.string.close_at, mCloseEvening));
                if (Utils.isClosingSoon(currentHour,mCloseMorning)) // if closing <= 45 minutes
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else if (Utils.hour1CompareToHour2(currentHour,mOpenMorning) <0 )// current hours before morning open hours
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_at, mOpenMorning));
            else if (Utils.hour1CompareToHour2(currentHour, mCloseMorning)<0){ // current hours before closing hours
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_from_until, mOpenMorning, mCloseMorning));
                if (Utils.isClosingSoon(currentHour,mCloseMorning)) // if closing <= 45 minutes
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else if (Utils.hour1CompareToHour2(currentHour,mOpenEvening) <0) // current hours before evening open hours
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_at,mOpenEvening));
            else if (Utils.hour1CompareToHour2(currentHour, mCloseEvening) < 0){ // current hours before evening closing hours
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_from_until, mOpenEvening,mCloseEvening));
                if (Utils.isClosingSoon(currentHour,mCloseEvening)) // if closing <= 45 minutes
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else if (Utils.hour1CompareToHour2(mCloseEvening, mOpenMorning)<0) { // case it closes after midnight
                mRestaurantOpeningHours.setText(mContext.getString(R.string.close_tomorrow_at, mCloseEvening));
                if (Utils.isClosingSoon(currentHour,mCloseEvening)) // if closing <= 45 minutes
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else
                mRestaurantOpeningHours.setText(R.string.closed);
        } catch (ParseException e) {
            System.out.println("RestaurantViewHolder morning evening schedule"+e.getMessage());
        }
    }

    private void configureMorningEveningHours(String[] hoursWithoutDay) {
        String hours = hoursWithoutDay[1].trim(); //" 11:30 am – 1:30 pm, 6:00 – 9:30 pm"
        String[] splitHours = hours.split(",");

        String morningHours = splitHours[0].trim(); //"11:30 am – 1:30 pm"
        String[] splitMorning = morningHours.split("–");

        mOpenMorning = splitMorning[0].trim(); //11:30 am
        if (!mOpenMorning.contains("am") && !mOpenMorning.contains("12:")) // 11:00
            mOpenMorning = mOpenMorning +" am";
        else if(mOpenMorning.contains("12:") && !mOpenMorning.contains("pm")) // for midday 12:00
            mOpenMorning = mOpenMorning + " pm";

        mCloseMorning = splitMorning[1].trim(); //" 1:30 pm"
        if(!mCloseMorning.contains("pm"))
            mCloseMorning = mCloseMorning + " pm";

        String eveningHours = splitHours[1].trim(); // 6:00 – 9:30 PM
        String[] splitEvening = eveningHours.split("–");

        mOpenEvening = splitEvening[0].trim();// 6:00
        if(!mOpenEvening.contains("pm"))
            mOpenEvening = mOpenEvening +" pm";

        mCloseEvening = splitEvening[1].trim(); // 9:30 PM;
        if (mCloseEvening.contains("12:") && !mCloseEvening.contains("am"))
            mCloseEvening = mCloseEvening + " am";
    }

    private void fullDaySchedule(String[] hoursWithoutDay, String currentHour) {
        String dayHours = hoursWithoutDay[1].trim(); //11:00 AM – 11:00 PM
        String[] splitDay = dayHours.split("–");
        String openDay = splitDay[0].trim(); // 11:00 AM
        if (!openDay.contains("am") && !openDay.contains("12:"))
            openDay = openDay+ " am";
        String closeDay = splitDay[1].trim(); // 11:00 PM
        if(!closeDay.contains(" pm") && !closeDay.contains("am"))
            closeDay = closeDay + " pm";
        try {
            if(Utils.hour1CompareToHour2(currentHour,closeDay) < 0 && Utils.hour1CompareToHour2(closeDay,openDay)<0) {
                mRestaurantOpeningHours.setText(mContext.getString(R.string.close_at, closeDay)); // if closes after 00:00 am
                if(Utils.isClosingSoon(currentHour,closeDay))
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else if (Utils.hour1CompareToHour2(currentHour, openDay) <0)
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_at, openDay));
            else if (Utils.hour1CompareToHour2(currentHour, closeDay) <0) {
                mRestaurantOpeningHours.setText(mContext.getString(R.string.open_from_until, openDay, closeDay)); // 08:00 am - 23:59 pm
                if(Utils.isClosingSoon(currentHour,closeDay))
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else if(Utils.hour1CompareToHour2(closeDay,openDay) <0) { // 10:00 am - 01:30 am
                mRestaurantOpeningHours.setText(mContext.getString(R.string.close_tomorrow_at, closeDay));
                if(Utils.isClosingSoon(currentHour,closeDay))
                    mRestaurantOpeningHours.setText(R.string.closing_soon);
            }
            else
                mRestaurantOpeningHours.setText(R.string.closed); // 9:30 am, 00:00 am
        } catch (ParseException e) {
            System.out.println("RestaurantViewHolder full day schedule"+e.getMessage());
        }
    }
}
