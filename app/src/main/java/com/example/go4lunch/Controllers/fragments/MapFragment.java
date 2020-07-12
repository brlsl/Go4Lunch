package com.example.go4lunch.controllers.fragments;

import android.Manifest;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import androidx.preference.PreferenceManager;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.MainActivity;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.Prediction;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.notifications.NotificationReceiver;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MapFragment extends BaseFragment implements OnMapReadyCallback {

    // ----- FOR DATA -----
    private Location deviceLocation;
    private int mLocationPermissionGranted = 0; // refused by default
    private Disposable mDisposable;
    private HashMap<LatLng, ResultDetails> myDictionary = new HashMap<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    private static String PLACE_API_KEY;
    private static final String PREFERENCES_NOTIFICATION_KEY ="notification_preferences_key";
    private static final String PREFERENCES_RADIUS_KEY = "radius";


    // ----- FOR UI -----
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15f;
    private ConstraintLayout mConstraintLayout;

    // ---- LIFE CYCLE -----

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PLACE_API_KEY = requireActivity().getString(R.string.google_place_api_key);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        setHasOptionsMenu(true); // show item in menu
        getLocationPermission(); // ask for location permission
        scheduleNotification(); // schedule a notification for 12:00
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View mView = inflater.inflate(R.layout.map_fragment, container, false);
        MapView mMapView = mView.findViewById(R.id.mapView);
        mConstraintLayout = mView.findViewById(R.id.constraint_layout_map_fragment);
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        scheduleNotification();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed())
            this.mDisposable.dispose();
    }

   // ----- MENU -----
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {  // remove sort restaurant from menu in this fragment
        MenuItem item = menu.findItem(R.id.restaurant_sort);
        if(item != null)
            item.setVisible(false);
    }

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST) // made with Easy Permission
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (EasyPermissions.hasPermissions(requireContext(), permissions)) {
            mLocationPermissionGranted = 1;
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.ask_for_permission), LOCATION_PERMISSION_REQUEST, permissions);
        }
    }

    // ask User permission with a dialog
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = 0;

        if (requestCode == LOCATION_PERMISSION_REQUEST) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = 1;
                Snackbar.make(mConstraintLayout, R.string.permission_granted, Snackbar.LENGTH_SHORT).show();
                getLastKnownLocation();
            } else {
                Snackbar.make(mConstraintLayout, R.string.permission_refused, Snackbar.LENGTH_SHORT).show();
                EasyPermissions.requestPermissions(this, getString(R.string.ask_for_permission), LOCATION_PERMISSION_REQUEST, permissions);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted == 1) {
            getLastKnownLocation();
            mMap.setOnMyLocationButtonClickListener(() -> {
                getLastKnownLocation();
                return false;
            });
        }
    }

    private void writeLastKnownLocation(Location deviceLocation){
        SharedPreferences pref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        deviceLocation.getLongitude();
        pref.edit().putString("device_latitude", String.valueOf(deviceLocation.getLatitude())).apply();
        pref.edit().putString("device_longitude", String.valueOf(deviceLocation.getLongitude())).apply();
    }
    private void readLastKnownLocation(){
        String lat = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("device_latitude", "48.8534");
        String lng = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("device_longitude", "2.3488");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), DEFAULT_ZOOM));

    }

    @SuppressLint("MissingPermission")
    public void getLastKnownLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), deviceLocation -> {
                    // Got last known location. In some rare situations this can be null.
                    if (deviceLocation != null) {
                        // Logic to handle location object
                        setDeviceLocation(deviceLocation);
                        writeLastKnownLocation(deviceLocation);
                        handleDeviceLocation(deviceLocation);

                    } else {
                        Snackbar.make(mConstraintLayout, R.string.unknown_location, Snackbar.LENGTH_SHORT).show();
                        Log.d("MapFragment", "Device location unknown");
                        readLastKnownLocation();
                    }
                });

    }

    @SuppressLint("MissingPermission")
    private void handleDeviceLocation(Location deviceLocation){
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(deviceLocation.getLatitude(),
                        deviceLocation.getLongitude()), DEFAULT_ZOOM));
        executeHttpSearchNearbyAndDetailsWithRetrofit();
    }

    // ----- REFROFIT REQUESTS -----
    public void executeHttpSearchNearbyAndDetailsWithRetrofit(){
        String deviceLocationStr = getDeviceLocation().getLatitude()+","+ getDeviceLocation().getLongitude();
        String radiusPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(PREFERENCES_RADIUS_KEY,"500");

        this.mDisposable = GooglePlaceStreams.streamNearbyThenFetchPlaceDetails(deviceLocationStr,radiusPreferences,PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<List<ResultDetails>>() {
                    @Override
                    public void onNext(List<ResultDetails> resultDetails) {
                        mMap.clear();
                        List<ResultDetails> resultDetailsList = new ArrayList<>();
                        for (int i = 0; i <resultDetails.size() ; i++) {
                            double lat = resultDetails.get(i).getResult().getGeometry().getLocation().getLat();
                            double lng = resultDetails.get(i).getResult().getGeometry().getLocation().getLng();
                            LatLng restaurantPosition = new LatLng(lat, lng);
                            if (resultDetails.get(i).getResult().getRating() == null){resultDetails.get(i).getResult().setRating(0.0);} // for sort method
                            putMarkerOnRestaurantPosition(restaurantPosition,R.drawable.food_blue);
                            myDictionary.put(restaurantPosition, resultDetails.get(i).getResult());
                            resultDetailsList.add(resultDetails.get(i).getResult());
                        }
                        // pass list of restaurants nearby details, context and device location to recycler view
                        RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                        restaurantListFragment.setRestaurantAdapterNearby(resultDetailsList, requireActivity(), getDeviceLocation());
                        putMarkerWhereWorkmateHaveLunch(myDictionary);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        onMarkerClick();
                    }
                });
    }

    public void executeHttpRequestAutoCompleteWithRetrofit(String input){
        String deviceLocationStr = getDeviceLocation().getLatitude()+","+ getDeviceLocation().getLongitude();
        String radiusPreferences = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString(PREFERENCES_RADIUS_KEY,"500");

        UUID uuid = UUID.randomUUID(); // Universally Unique Identifier
        String sessionToken = uuid.toString();

        this.mDisposable = GooglePlaceStreams.streamFetchAutoComplete(input,"establishment",
                deviceLocationStr, radiusPreferences, sessionToken, PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<AutoComplete>() {
                    @Override
                    public void onNext(AutoComplete autoComplete) {
                        mMap.clear();
                        for (int i = 0; i < autoComplete.getPredictions().size(); i++) {
                            String restaurantId = autoComplete.getPredictions().get(i).getPlaceId();
                            LatLng restaurantPosition = getLatLngKeyByRestaurantIdValue(myDictionary,restaurantId);
                            if (restaurantPosition != null && myDictionary.containsKey(restaurantPosition)){
                               putMarkerOnRestaurantPosition(restaurantPosition, R.drawable.food_orange); // green color
                            }
                        }
                        List<ResultDetails> resultDetails =  getResultDetailsFromPrediction(autoComplete.getPredictions());
                        // pass list of restaurants nearby details, context and device location to recycler view
                        RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                        restaurantListFragment.setRestaurantAdapterNearby(resultDetails, requireContext(), getDeviceLocation());
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        onMarkerClick();
                    }
                });
    }

    private void onMarkerClick(){
        mMap.setOnMarkerClickListener(marker -> {
            Intent intentDetail = new Intent(requireContext(), RestaurantDetailActivity.class);
            intentDetail.putExtra("PLACE_ID_KEY", Objects.requireNonNull(myDictionary.get(marker.getPosition())).getPlaceId());
            startActivity(intentDetail);
            return false;
        });
    }

    // ----- UI -----

    private void putMarkerOnRestaurantPosition(LatLng markerLatLng, int icon){
        mMap.addMarker(new MarkerOptions()
                .position(markerLatLng)
                .icon(BitmapDescriptorFactory.fromResource(icon)));
    }

    private void putMarkerWhereWorkmateHaveLunch(HashMap<LatLng, ResultDetails> myDictionary){
        for (Map.Entry<LatLng, ResultDetails> entry : myDictionary.entrySet()) {
            UserHelper.getUsersCollection()
                    .whereEqualTo("restaurantChoiceId", entry.getValue().getPlaceId()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            User user = documentSnapshot.toObject(User.class);
                            LatLng restaurantPosition = getLatLngKeyByRestaurantIdValue(myDictionary,user.getRestaurantChoiceId());
                            assert restaurantPosition != null;
                            putMarkerOnRestaurantPosition(restaurantPosition, R.drawable.food_green);
                        }
                    });
        }
    }

    private void scheduleNotification(){
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(requireContext());
        boolean isNotificationEnable = preferences.getBoolean(PREFERENCES_NOTIFICATION_KEY,true); // notification settings

        Intent intent = new Intent(requireActivity(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(requireActivity(), 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        Calendar now = Calendar.getInstance();
        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, 12);
        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 0);
        notificationTime.set(Calendar.MILLISECOND, 0);

        if (alarmManager !=null){
            if (isNotificationEnable){ // notifications are enabled in settings
                if (now.before(notificationTime)) { // now before  12:00
                    alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
                } else { // now after 12:00
                    notificationTime.set(Calendar.DAY_OF_YEAR, now.get(Calendar.DAY_OF_YEAR)+1);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(), pendingIntent);
                }
            } else { // notifications are disabled in settings
                alarmManager.cancel(pendingIntent); // cancel next scheduled notification
            }
        }

    }

    // ----- UTILS -----

    public static LatLng getLatLngKeyByRestaurantIdValue(Map<LatLng,ResultDetails> map, String id) {
        for (Map.Entry<LatLng,ResultDetails> entry : map.entrySet()) {
            if (Objects.equals(id, entry.getValue().getPlaceId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<ResultDetails> getResultDetailsFromPrediction(List<Prediction> listPredictions){
        List<ResultDetails> resultDetails = new ArrayList<>();
        for (Prediction prediction: listPredictions) {
            for(Map.Entry<LatLng, ResultDetails> resultDetailsEntry: myDictionary.entrySet()){
                if (resultDetailsEntry.getValue().getPlaceId().equals(prediction.getPlaceId())){
                    resultDetails.add(resultDetailsEntry.getValue());
                }
            }

        }
        return resultDetails;
    }

    // ----- GETTERS AND SETTERS -----
    public Location getDeviceLocation() {
        return deviceLocation;
    }

    public void setDeviceLocation(Location location) {
        this.deviceLocation = location;
    }

}