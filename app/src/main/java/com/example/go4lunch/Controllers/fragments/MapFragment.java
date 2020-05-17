package com.example.go4lunch.controllers.fragments;

import android.Manifest;
import android.app.Activity;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.go4lunch.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;


public class MapFragment extends androidx.fragment.app.Fragment implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST = 1234;
    private static final String TAG = "MAP FRAGMENT";
    private static final float DEFAULT_ZOOM = 15f;

    private FusedLocationProviderClient mFusedLocationProviderClient;

    private int mLocationPermissionGranted = 0; // refused by default

    private GoogleMap mMap;
    private Location mLastKnownLocation;


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
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
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


}
