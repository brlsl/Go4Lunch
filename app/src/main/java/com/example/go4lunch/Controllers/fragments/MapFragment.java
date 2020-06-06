package com.example.go4lunch.controllers.fragments;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.go4lunch.R;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.controllers.activities.MainActivity;
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.AutoComplete;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.SearchNearby;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1234;

    private static final float DEFAULT_ZOOM = 15f;

    private static final String PLACE_API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE";



    private FusedLocationProviderClient mFusedLocationProviderClient;

    private int mLocationPermissionGranted = 0; // refused by default

    private GoogleMap mMap;



    private Location location;

    private Disposable mDisposable;


    public HashMap<LatLng, String> getMyDictionary() {
        return myDictionary;
    }

    public void setMyDictionary(HashMap<LatLng, String> myDictionary) {
        this.myDictionary = myDictionary;
    }

    private HashMap<LatLng,String> myDictionary = new HashMap<>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View mView = inflater.inflate(R.layout.map_fragment, container, false);
        MapView mMapView = mView.findViewById(R.id.mapView);
        if (mMapView != null) {
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        getLocationPermission();
        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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

    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST) // made with Easy Permission
    public void getLocationPermission() {
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
            EasyPermissions.requestPermissions(this, "Go4Lunch needs location permission", LOCATION_PERMISSION_REQUEST, permissions);
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = 1;
                    Toast.makeText(requireContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    getDeviceLocation();
                } else {
                    Toast.makeText(requireContext(), "Permission refused", Toast.LENGTH_SHORT).show();
                    EasyPermissions.requestPermissions(this, "Go4Lunch needs location permission", LOCATION_PERMISSION_REQUEST, permissions);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted == 1) {
            getDeviceLocation();
            mMap.setOnMyLocationButtonClickListener(() -> {
                getDeviceLocation();
                return false;
            });

        }
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void getDeviceLocation() {
        mFusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location deviceLocation) {
                        // Got last known location. In some rare situations this can be null.
                        if (deviceLocation != null) {
                            // Logic to handle location object
                            setLocation(deviceLocation);
                            handleDeviceLocation(deviceLocation);

                        }
                    }
                });
    }

    private void handleDeviceLocation(Location deviceLocation){
        mMap.setMyLocationEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(deviceLocation.getLatitude(),
                        deviceLocation.getLongitude()), DEFAULT_ZOOM));
        executeHttpRequestNearbySearchWithRetrofit();
        //executeHttpRequestAutoCompleteWithRetrofit();
    }

    public void executeHttpRequestNearbySearchWithRetrofit() {
        Location deviceLocation = getLocation();
        String deviceLocationStr = deviceLocation.getLatitude()+","+deviceLocation.getLongitude();
        this.mDisposable = GooglePlaceStreams.streamFetchNearbySearch(deviceLocationStr,500,"restaurant", PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<SearchNearby>() {
            @Override
            public void onNext(SearchNearby searchNearby) {
                mMap.clear();
                HashMap<LatLng,String> myDictionary = new HashMap<>();

                for (int i = 0; i < searchNearby.getResults().size(); i++) {
                    double lat = searchNearby.getResults().get(i).getGeometry().getLocation().getLat();
                    double lng = searchNearby.getResults().get(i).getGeometry().getLocation().getLng();
                    LatLng markerLatLng = new LatLng(lat, lng);

                    MarkerOptions markerOptions = new MarkerOptions().position(markerLatLng).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    Marker restaurantMarker = mMap.addMarker(markerOptions);
                    restaurantMarker.isVisible();

                    // put restaurant position & place id in dictionary
                    myDictionary.put(markerLatLng, searchNearby.getResults().get(i).getPlaceId());

                    }
                setMyDictionary(myDictionary);
                // pass data to restaurant fragment (results, dictionary, context and device location)
                RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                restaurantListFragment.setResultList(searchNearby.getResults(), myDictionary, requireActivity(), deviceLocation);


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Intent intent = new Intent(requireContext(), RestaurantDetailActivity.class);
                        intent.putExtra("DICTIONARY_KEY", myDictionary); // send dictionary position(key) and place id (value)
                        intent.putExtra("POSITION_KEY", marker.getPosition());// send marker position

                        startActivity(intent);
                        return false;
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    private void configureMapAutoComplete(){
        mMap.clear();
    }

    public void executeHttpRequestAutoCompleteWithRetrofit(String input){
        //String userLatitudeStr = String.valueOf(deviceLocation.getLatitude());
        //String userLongitudeStr = String.valueOf(deviceLocation.getLongitude());

        HashMap<LatLng,String> myDictionary = getMyDictionary();
        Location deviceLocation = getLocation();
        String deviceLocationStr = deviceLocation.getLatitude()+","+deviceLocation.getLongitude();

        this.mDisposable = GooglePlaceStreams.streamFetchAutoComplete(input,"establishment",deviceLocationStr,
                500,"" ,"token", PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<AutoComplete>() {
                    @Override
                    public void onNext(AutoComplete autoComplete) {
                        mMap.clear();
                        //Toast.makeText(requireContext(), "good to be here", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < autoComplete.getPredictions().size(); i++) {
                            if (myDictionary.containsValue(autoComplete.getPredictions().get(i).getPlaceId())){
                                //TODO: récupérer la position et mettre le marqueur assosié
                                LatLng latLng = getKeyByValue(myDictionary, autoComplete.getPredictions().get(i).getPlaceId());

                                MarkerOptions markerOptions = new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                Marker restaurantMarker = mMap.addMarker(markerOptions);
                                restaurantMarker.isVisible();
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}