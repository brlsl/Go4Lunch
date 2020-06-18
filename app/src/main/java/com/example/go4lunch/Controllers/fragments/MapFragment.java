package com.example.go4lunch.controllers.fragments;

import android.Manifest;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Bundle;


import android.util.Log;
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
import com.example.go4lunch.models.apiGooglePlace.placeAutoComplete.Prediction;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.SearchNearby;
import com.example.go4lunch.utils.GooglePlaceStreams;
import com.example.go4lunch.views.workmates_list_rv_restaurant_detail_activity.JoiningWorkmateAdapter;
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

    private static final int LOCATION_PERMISSION_REQUEST = 1234;

    private static final float DEFAULT_ZOOM = 15f;

    private static String PLACE_API_KEY = "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE";


    private FusedLocationProviderClient mFusedLocationProviderClient;

    private int mLocationPermissionGranted = 0; // refused by default

    private GoogleMap mMap;


    private LocationCallback locationCallback;

    private Location location;

    private Disposable mDisposable;


    private HashMap<LatLng, ResultSearchNearby> myDictionary = new HashMap<>();

    private UUID uuid = UUID.randomUUID(); // Universally Unique Identifier
    private String mSessionToken = uuid.toString();
    private FloatingActionButton mGetDeviceLocationFab;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            mGetDeviceLocationFab = getView().findViewById(R.id.getLocationFab);

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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = 1;
                    Toast.makeText(requireContext(), R.string.permission_granted, Toast.LENGTH_SHORT).show();
                    getDeviceLocation();
                    mGetDeviceLocationFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDeviceLocation();
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), R.string.permission_refused, Toast.LENGTH_SHORT).show();
                    mGetDeviceLocationFab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(requireContext(), "Please give location permission first", Toast.LENGTH_SHORT).show();
                        }
                    });
                    EasyPermissions.requestPermissions(this, getString(R.string.ask_for_permission), LOCATION_PERMISSION_REQUEST, permissions);
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (mLocationPermissionGranted == 1) {
            mGetDeviceLocationFab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getDeviceLocation();
                }
            });
            getDeviceLocation();
            mMap.setOnMyLocationButtonClickListener(() -> {
                getDeviceLocation();
                return false;
            });

        }
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
                        } else {
                            Toast.makeText(requireContext(), R.string.unknown_location, Toast.LENGTH_SHORT).show();
                            Log.d("MapFragment","Device location unknown");
                            //startLocationUpdate();
                        }
                    }
                }).addOnFailureListener(requireActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Map Fragment","get Device Location on Failure Listener:" , e);
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
        Location deviceLocation = getLocation();
        String deviceLocationStr = deviceLocation.getLatitude()+","+deviceLocation.getLongitude();
        this.mDisposable = GooglePlaceStreams.streamFetchNearbySearch(deviceLocationStr,500,"restaurant", PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<SearchNearby>() {
            @Override
            public void onNext(SearchNearby searchNearby) {
                mMap.clear();
                HashMap<LatLng,ResultSearchNearby> myDictionary = new HashMap<>();

                for (int i = 0; i < searchNearby.getResults().size(); i++) {
                    double lat = searchNearby.getResults().get(i).getGeometry().getLocation().getLat();
                    double lng = searchNearby.getResults().get(i).getGeometry().getLocation().getLng();
                    LatLng markerLatLng = new LatLng(lat, lng);


                    MarkerOptions markerOptions = new MarkerOptions().position(markerLatLng).icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    Marker restaurantMarker = mMap.addMarker(markerOptions);
                    restaurantMarker.isVisible();

                    // put restaurant position & place id in dictionary
                    myDictionary.put(markerLatLng, searchNearby.getResults().get(i));

                }

                // set dictionary for autoComplete
                setMyDictionary(myDictionary);

                // pass data to restaurant fragment (results, dictionary, context and device location)
                RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                restaurantListFragment.setRestaurantAdapterNearby(searchNearby.getResults(), requireActivity(), deviceLocation);


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Intent intentDetail = new Intent(requireContext(), RestaurantDetailActivity.class);
                        //Intent intentList = new Intent(requireContext(), JoiningWorkmateAdapter.class);
                        intentDetail.putExtra("PLACE_ID_KEY", myDictionary.get(marker.getPosition()).getPlaceId());
                        //intentList.putExtra("PLACE_ID_KEY", myDictionary.get(marker.getPosition()).getPlaceId());
                        startActivity(intentDetail);
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

    public void executeHttpRequestAutoCompleteWithRetrofit(String input){
        HashMap<LatLng,ResultSearchNearby> myDictionary = getMyDictionary();
        Location deviceLocation = getLocation();
        String deviceLocationStr = deviceLocation.getLatitude()+","+deviceLocation.getLongitude();

        this.mDisposable = GooglePlaceStreams.streamFetchAutoComplete(input,"establishment",deviceLocationStr,
                500,"" , mSessionToken, PLACE_API_KEY)
                .subscribeWith(new DisposableObserver<AutoComplete>() {
                    @Override
                    public void onNext(AutoComplete autoComplete) {
                        mMap.clear();

                        for (int i = 0; i < autoComplete.getPredictions().size(); i++) {
                            String restaurantId = autoComplete.getPredictions().get(i).getPlaceId();
                            LatLng restaurantLatLng = getPredictionKeyByValue(myDictionary,restaurantId);
                            if (myDictionary.containsKey(restaurantLatLng)){
                                // get position (key) associated to value (place id) and add marker
                                //LatLng latLng = getPredictionKeyByValue(myDictionary, autoComplete.getPredictions().get(i).getPlaceId());

                                MarkerOptions markerOptions = new MarkerOptions().position(restaurantLatLng).icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                Marker restaurantMarker = mMap.addMarker(markerOptions);
                                restaurantMarker.isVisible();

                            }

                        }
                        List<ResultSearchNearby> resultsSearch =  getResultSearchNearbyFromPrediction(autoComplete.getPredictions());

                        RestaurantListFragment restaurantListFragment = ((MainActivity) requireActivity()).getRestaurantListFragment();
                        restaurantListFragment.setRestaurantAdapterNearby(resultsSearch, requireContext(), deviceLocation );

                        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                            @Override
                            public boolean onMarkerClick(Marker marker) {
                                Intent intent = new Intent(requireContext(), RestaurantDetailActivity.class);
                                intent.putExtra("PLACE_ID_KEY", Objects.requireNonNull(myDictionary.get(marker.getPosition())).getPlaceId());
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

    public static LatLng getPredictionKeyByValue(Map<LatLng,ResultSearchNearby > map, String id) {
        for (Map.Entry<LatLng,ResultSearchNearby > entry : map.entrySet()) {
            if (Objects.equals(id, entry.getValue().getPlaceId())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<ResultSearchNearby> getResultSearchNearbyFromPrediction(List<Prediction> listPredictions){
        List<ResultSearchNearby> resultSearchNearbies = new ArrayList<>();
        for (Prediction prediction: listPredictions) {
            for(Map.Entry<LatLng,ResultSearchNearby > resultSearchNearby: myDictionary.entrySet()){
                if (resultSearchNearby.getValue().getPlaceId().equals(prediction.getPlaceId())){
                    resultSearchNearbies.add(resultSearchNearby.getValue());
                }

            }

        }
        return resultSearchNearbies;
    }

    // -------------------
    // GETTER AND SETTER
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