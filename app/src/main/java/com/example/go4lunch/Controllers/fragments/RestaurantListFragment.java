package com.example.go4lunch.controllers.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.go4lunch.R;

import com.example.go4lunch.controllers.activities.MainActivity;
import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.views.restaurant_list_fragment_rv.RestaurantAdapter;

import java.util.HashMap;
import java.util.List;

public class RestaurantListFragment extends androidx.fragment.app.Fragment {
    private RecyclerView mRecyclerView;
    private RestaurantAdapter mAdapter;
    private HashMap<String, List<String>> mRestaurantHourDictionary;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_list_navigation, container, false);

        configureRecyclerView(view);
        configureSwipeRefreshLayout(view);

        return view;
    }

    private void configureRecyclerView(View view){
        this.mRecyclerView = view.findViewById(R.id.recycler_view_restaurants);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
    }

    private void configureSwipeRefreshLayout(View view){
        mSwipeRefreshLayout = view.findViewById(R.id.restaurant_list_rv__refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            MapFragment mapFragment =  ((MainActivity) requireActivity()).getMapFragment();
            mapFragment.getLastKnownLocation();
            Toast.makeText(requireContext(), "Refreshing data", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        });

    }

    void setRestaurantAdapterNearby(List<ResultDetails> resultDetails, Context context, Location deviceLocation) {
        this.mAdapter = new RestaurantAdapter(resultDetails,context,deviceLocation);
        this.mRecyclerView.setAdapter(mAdapter);
    }
/*
    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.disposeWhenDestroy();
    }
*/
    public void setRestaurantHourDictionary(HashMap<String, List<String>> restaurantHoursDictionary) {
        mRestaurantHourDictionary = restaurantHoursDictionary;
    }
/*
    public HashMap<String, List<String>> getmRestaurantHourDictionary() {
        return mRestaurantHourDictionary;
    }

 */
}
