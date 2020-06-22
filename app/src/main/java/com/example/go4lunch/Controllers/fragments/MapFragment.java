package com.example.go4lunch.controllers.fragments;

import android.Manifest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceManager;


import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.controllers.activities.MainActivity;
import com.example.go4lunch.models.User;
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.Prediction;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.SearchNearby;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    // -- FOR DATA
    private Location location;
    private int mLocationPermissionGranted = 0; // refused by default
    private Disposable mDisposable;
    private LocationCallback locationCallback;
    private UUID uuid = UUID.randomUUID(); // Universally Unique Identifier
    private String mSessionToken = uuid.toString();
    private HashMap<LatLng, ResultSearchNearby> myDictionary = new HashMap<>();
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    private static String PLACE_API_KEY;

    // -- FOR UI
    private GoogleMap mMap;
    private static final float DEFAULT_ZOOM = 15f;
    private FloatingActionButton mGetDeviceLocationFab;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PLACE_API_KEY = requireActivity().getString(R.string.google_place_api_key);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

        // for location update
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null)
                    return;
                for(Location location: locationResult.getLocations()){
                    String stringLatitude = String.valueOf(location.getLatitude());
                    String stringLongitude = String.valueOf(location.getLongitude());
                    //executeHttpRequestNearbySearchWithRetrofit(stringLatitude,stringLongitude);
                }
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View mView = inflater.inflate(R.layout.map_fragment, container, false);

        MapView mMapView = mView.findViewById(R.id.mapView);
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        getLocationPermission();
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            mGetDeviceLocationFab = requireView().findViewById(R.id.getLocationFab);

    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onResume() {
        super.onResume();
        //if (requestingLocation)
        //startLocationUpdate();
    }
/*
    private void startLocationUpdate() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(300000); // in milliseconds = 300 seconds
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.getMainLooper());
    }
*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.mDisposable != null && !this.mDisposable.isDisposed())
            this.mDisposable.dispose();

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

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = 1;
                    Toast.makeText(requireContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    getDeviceLocation();
                    mGetDeviceLocationFab.setOnClickListener(v -> getDeviceLocation());
                } else {
                    Toast.makeText(requireContext(), R.string.permission_refused, Toast.LENGTH_SHORT).show();
                    mGetDeviceLocationFab.setOnClickListener(v -> Toast.makeText(requireContext(),
                            "Please give location permission first", Toast.LENGTH_SHORT).show());
                    EasyPermissions.requestPermissions(this, getString(R.string.ask_for_permission), LOCATION_PERMISSION_REQUEST, permissions);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted == 1) {
            mGetDeviceLocationFab.setOnClickListener(v -> getDeviceLocation());
            getDeviceLocation();
            mMap.setOnMyLocationButtonClickListener(() -> {
                getDeviceLocation();
                return false;
            });

        }
    }

    public void writeLastKnownLocation(Location deviceLocation){
        SharedPreferences pref = requireActivity().getPreferences(Context.MODE_PRIVATE);
        deviceLocation.getLongitude();
        pref.edit().putString("device_latitude", String.valueOf(deviceLocation.getLatitude())).apply();
        pref.edit().putString("device_longitude", String.valueOf(deviceLocation.getLongitude())).apply();
    }
    public void readLastKnownLocation(){
        String lat = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("device_latitude", "48.8534");
        String lng = requireActivity().getPreferences(Context.MODE_PRIVATE).getString("device_longitude", "2.3488");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), DEFAULT_ZOOM));

    }
    void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), deviceLocation -> {
                    // Got last known location. In some rare situations this can be null.
                    if (deviceLocation != null) {
                        // Logic to handle location object
                        setLocation(deviceLocation);
                        writeLastKnownLocation(deviceLocation);
                        handleDeviceLocation(deviceLocation);

                    } else {
                        Toast.makeText(requireContext(), R.string.unknown_location, Toast.LENGTH_SHORT).show();
                        Log.d("MapFragment", "Device location unknown");
                        readLastKnownLocation();

                        //startLocationUpdate();
                    }
                });

    }

    private void handleDeviceLocation(Location deviceLocation){
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(deviceLocation.getLatitude(),
                        deviceLocation.getLongitude()), DEFAULT_ZOOM));
        executeHttpRequestNearbySearchWithRetrofit();
    }

    public void executeHttpRequestNearbySearchWithRetrofit() {
        String deviceLocationStr = getLocation().getLatitude()+","+getLocation().getLongitude();
        String radius_preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString("radius","500");

        this.mDisposable = GooglePlaceStreams.streamFetchNearbySearch(deviceLocationStr,radius_preferences, PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<SearchNearby>() {
            @Override
            public void onNext(SearchNearby searchNearby) {
                mMap.clear();
                HashMap<LatLng,ResultSearchNearby> myDictionary = new HashMap<>();

                for (int i = 0; i < searchNearby.getResults().size(); i++) {
                    double lat = searchNearby.getResults().get(i).getGeometry().getLocation().getLat();
                    double lng = searchNearby.getResults().get(i).getGeometry().getLocation().getLng();
                    LatLng markerLatLng = new LatLng(lat, lng);

                    handleMarkerRestaurant(markerLatLng);

                    // put restaurant position & place id in dictionary
                    myDictionary.put(markerLatLng, searchNearby.getResults().get(i));
                }
                // set dictionary for autoComplete
                setMyDictionary(myDictionary);

                // pass data to restaurant fragment (results, context and device location)
                RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                restaurantListFragment.setRestaurantAdapterNearby(searchNearby.getResults(), requireActivity(), getLocation());


                onMarkerClick();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                 handleMarkerWorkmateGoingToRestaurant(getMyDictionary());
            }
        });
    }

    public void executeHttpRequestAutoCompleteWithRetrofit(String input){
        String deviceLocationStr = getLocation().getLatitude()+","+getLocation().getLongitude();
        String radius_preferences = PreferenceManager.getDefaultSharedPreferences(requireActivity()).getString("radius","500");

        this.mDisposable = GooglePlaceStreams.streamFetchAutoComplete(input,"establishment",
                deviceLocationStr, radius_preferences, mSessionToken, PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<AutoComplete>() {
                    @Override
                    public void onNext(AutoComplete autoComplete) {
                        mMap.clear();

                        for (int i = 0; i < autoComplete.getPredictions().size(); i++) {
                            String restaurantId = autoComplete.getPredictions().get(i).getPlaceId();
                            LatLng restaurantLatLng = getLatLngKeyByRestaurantIdValue(getMyDictionary(),restaurantId);
                            if (getMyDictionary().containsKey(restaurantLatLng)){

                                MarkerOptions markerOptions = new MarkerOptions().position(restaurantLatLng).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                Marker restaurantMarker = mMap.addMarker(markerOptions);
                                restaurantMarker.isVisible();
                            }
                        }
                        List<ResultSearchNearby> resultsSearch =  getResultSearchNearbyFromPrediction(autoComplete.getPredictions());

                        // pass data to restaurant Fragment (search result, context and device location)
                        RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                        restaurantListFragment.setRestaurantAdapterNearby(resultsSearch, requireContext(), getLocation());

                        onMarkerClick();
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    // ---

    private void onMarkerClick(){
        mMap.setOnMarkerClickListener(marker -> {
            Intent intentDetail = new Intent(requireContext(), RestaurantDetailActivity.class);
            intentDetail.putExtra("PLACE_ID_KEY", myDictionary.get(marker.getPosition()).getPlaceId());
            startActivity(intentDetail);
            return false;
        });
    }

    // --- UI ---

    private void handleMarkerRestaurant(LatLng markerLatLng){
        MarkerOptions markerOptions = new MarkerOptions().position(markerLatLng).icon(BitmapDescriptorFactory
                .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
        Marker restaurantMarker = mMap.addMarker(markerOptions);
        restaurantMarker.isVisible();

    };

    private void handleMarkerWorkmateGoingToRestaurant(HashMap<LatLng,ResultSearchNearby> myDictionary){
        for (Map.Entry<LatLng,ResultSearchNearby> entry : myDictionary.entrySet()) {
            UserHelper.getUsersCollection()
                    .whereEqualTo("restaurantChoiceId", entry.getValue().getPlaceId()).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots){
                            User user = documentSnapshot.toObject(User.class);
                            LatLng userRestaurantChoiceLatLng = getLatLngKeyByRestaurantIdValue(myDictionary,user.getRestaurantChoiceId());
                            assert userRestaurantChoiceLatLng != null;
                            MarkerOptions markerOptions = new MarkerOptions().position(userRestaurantChoiceLatLng).icon(BitmapDescriptorFactory
                                    .defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                            Marker restaurantMarker = mMap.addMarker(markerOptions);
                            restaurantMarker.isVisible();
                        }

                    });
        }
    }

    // --- UTILS ---

    public static LatLng getLatLngKeyByRestaurantIdValue(Map<LatLng,ResultSearchNearby> map, String id) {
        for (Map.Entry<LatLng,ResultSearchNearby> entry : map.entrySet()) {
            if (Objects.equals(id, entry.getValue().getPlaceId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<ResultSearchNearby> getResultSearchNearbyFromPrediction(List<Prediction> listPredictions){
        List<ResultSearchNearby> resultSearchNearbies = new ArrayList<>();
        for (Prediction prediction: listPredictions) {
            for(Map.Entry<LatLng,ResultSearchNearby> resultSearchNearby: myDictionary.entrySet()){
                if (resultSearchNearby.getValue().getPlaceId().equals(prediction.getPlaceId())){
                    resultSearchNearbies.add(resultSearchNearby.getValue());
                }
            }

        }
        return resultSearchNearbies;
    }

    // -------------------
    // GETTERS AND SETTERS
    // -------------------

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMap<LatLng, ResultSearchNearby> getMyDictionary() {
        return myDictionary;
    }

    public void setMyDictionary(HashMap<LatLng, ResultSearchNearby> myDictionary) {
        this.myDictionary = myDictionary;
    }
}