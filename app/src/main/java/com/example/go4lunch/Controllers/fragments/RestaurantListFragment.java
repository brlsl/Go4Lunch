package com.example.go4lunch.controllers.fragments;

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

import com.example.go4lunch.models.apiGooglePlace.Result;
import com.example.go4lunch.views.RestaurantAdapter;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListFragment extends androidx.fragment.app.Fragment {
    private RecyclerView mRecyclerView;
    private RestaurantAdapter adapter;
    private List<Result> resultList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.restaurant_list_navigation, container, false);

        this.resultList = new ArrayList<>();
        this.adapter = new RestaurantAdapter(resultList);

        this.mRecyclerView = (RecyclerView) view;
        this.mRecyclerView.setAdapter(this.adapter);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));

        return view;
    }

}
