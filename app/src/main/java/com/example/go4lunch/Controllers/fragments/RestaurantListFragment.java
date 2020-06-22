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
import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.example.go4lunch.views.restaurant_list_fragment_rv.RestaurantAdapter;

import java.util.List;
import java.util.Objects;

public class RestaurantListFragment extends androidx.fragment.app.Fragment {
    private RecyclerView mRecyclerView;
    private RestaurantAdapter mAdapter;

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
            mapFragment.getDeviceLocation();
            Toast.makeText(requireContext(), "Refreshing data", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        });

    }

    public void setRestaurantAdapterNearby(List<ResultSearchNearby> resultListSearchNearby, Context context, Location deviceLocation) {
        this.mAdapter = new RestaurantAdapter(resultListSearchNearby,context,deviceLocation);
        this.mRecyclerView.setAdapter(mAdapter);
    }



}
