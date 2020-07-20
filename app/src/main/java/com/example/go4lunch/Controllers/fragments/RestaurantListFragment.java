package com.example.go4lunch.controllers.fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.example.go4lunch.utils.Utils;
import com.example.go4lunch.views.restaurant_list_fragment_rv.RestaurantAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListFragment extends BaseFragment {

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RestaurantAdapter mRestaurantAdapter;
    private List<ResultDetails> mResultDetailsList = new ArrayList<>();

    // ----- LIFE CYCLE -----

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // show item in menu
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_list_navigation, container, false);

        configureRecyclerView(view);
        configureSwipeRefreshLayout(view);
        setHasOptionsMenu(true);

        return view;
    }

    // ----- CONFIGURE DATA -----

    private void configureRecyclerView(View view){
        this.mRecyclerView = view.findViewById(R.id.recycler_view_restaurants);
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        this.mRestaurantAdapter = new RestaurantAdapter(mResultDetailsList);
        this.mRecyclerView.setAdapter(mRestaurantAdapter);
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

    // ----- SORT MENU -----
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.sortByName:
                if (mResultDetailsList == null || mResultDetailsList.size() == 0)
                    Snackbar.make(mSwipeRefreshLayout , R.string.no_restaurant_around, Snackbar.LENGTH_SHORT).show();
                else {
                    Utils.sortRestaurantByNameAZ(mResultDetailsList);
                    mRestaurantAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.sortByRating:
                if (mResultDetailsList == null || mResultDetailsList.size() == 0)
                    Snackbar.make(mSwipeRefreshLayout , R.string.no_restaurant_around, Snackbar.LENGTH_SHORT).show();
                else{
                    Utils.sortHighRatingFirst(mResultDetailsList);
                    mRestaurantAdapter.notifyDataSetChanged();
                }
        }
        return true;
    }

    // ----- SETTER -----
    void setRestaurantAdapterNearby(List<ResultDetails> resultDetails, Context context, Location deviceLocation) {
        this.mRestaurantAdapter = new RestaurantAdapter(resultDetails, context, deviceLocation);
        this.mRecyclerView.setAdapter(mRestaurantAdapter);
        mResultDetailsList = resultDetails;
    }
}
