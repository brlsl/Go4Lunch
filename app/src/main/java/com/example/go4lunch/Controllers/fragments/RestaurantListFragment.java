package com.example.go4lunch.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.R;

import com.example.go4lunch.models.apiGooglePlace.placeSearchNearby.ResultSearchNearby;
import com.example.go4lunch.views.RestaurantAdapter;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class RestaurantListFragment extends androidx.fragment.app.Fragment {
    private RecyclerView mRecyclerView;
    private RestaurantAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_list_navigation, container, false);

        //this.resultList = new ArrayList<>(); // avoid resultList null value
        //this.resultList.size();
        this.mRecyclerView = (RecyclerView) view;
        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        return view;
    }
    public void setResultList(List<ResultSearchNearby> resultList, HashMap<LatLng,String> myDictionary, Context context) {

        //this.resultList = resultList;
        this.adapter = new RestaurantAdapter(resultList, myDictionary,context);
        this.mRecyclerView.setAdapter(this.adapter);

    }




}
