package com.example.go4lunch.controllers.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.go4lunch.R;
import com.example.go4lunch.models.MyPlaces;
import com.example.go4lunch.models.Results;
import com.example.go4lunch.remote.Common;
import com.example.go4lunch.remote.IGoogleApiInterface;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MapFragment extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    private static final String TAG = "MAP FRAGMENT";
    private static final float DEFAULT_ZOOM = 15f;

    private static final String PLACE_API_KEY= "AIzaSyAK366wqKIdy-Td7snXrjIRaI9MkXb2VZE";

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private int mLocationPermissionGranted = 0; // refused by default

    private GoogleMap mMap;
    private Location mLastKnownLocation;

    private double latitude, longitude;
    private Marker mMarker;

    private IGoogleApiInterface mService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout for this fragment
        View mView = inflater.inflate(R.layout.map_fragment, container, false);
        MapView mMapView =  mView.findViewById(R.id.map);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);
        }
        mService = Common.getGoogleApiService();
        getLocationPermission();
        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @AfterPermissionGranted(LOCATION_PERMISSION_REQUEST)
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
            EasyPermissions.requestPermissions(this, "Go4Lunch needs location permission", LOCATION_PERMISSION_REQUEST, permissions);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (mLocationPermissionGranted == 1) {
            getDeviceLocation();
        }

    }

    // traiter la demande d'autorisation
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
                    return;
                }
                else
                {
                    Toast.makeText(requireContext(), "Permission refused", Toast.LENGTH_SHORT).show();
                    EasyPermissions.requestPermissions(this, "Go4Lunch needs location permission", LOCATION_PERMISSION_REQUEST, permissions);
                }
            }
        }
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            if (mLocationPermissionGranted == 1) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) requireContext(), new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) { // callback
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            latitude = mLastKnownLocation.getLatitude();
                            longitude = mLastKnownLocation.getLongitude();

                            showRestaurantsNearby();

                        } else {
                            Toast.makeText(requireContext(), "Unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void showRestaurantsNearby() {
        Toast.makeText(requireContext(), "Restaurant show nearby", Toast.LENGTH_SHORT).show();
        mMap.clear();

        String url = getUrl(latitude,longitude);

        mService.getNearbyPlaces(url)
                .enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                if (response.isSuccessful()){

                    for (int i = 0; i < response.body().getResults().length; i++) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        Results googlePlace = response.body().getResults()[i];
                        double lat = googlePlace.getGeometry().getLocation().getLatitude();
                        double lng = googlePlace.getGeometry().getLocation().getLongitude();
                        String placeName = googlePlace.getName();
                        String vinicity = googlePlace.getVicinity();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                        markerOptions.title(placeName);

                        mMap.addMarker(markerOptions);
                        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    }

                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {
                Log.d(TAG, "Callback failure");
            }
        });

    }

    private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlaceUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+5000);
        googlePlaceUrl.append("&type=restaurant");
        googlePlaceUrl.append("&key="+PLACE_API_KEY);
        Log.d("getUrl", googlePlaceUrl.toString());
        return googlePlaceUrl.toString();
    }



}
