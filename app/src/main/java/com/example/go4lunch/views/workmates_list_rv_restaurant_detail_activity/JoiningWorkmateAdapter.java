package com.example.go4lunch.views.workmates_list_rv_restaurant_detail_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class JoiningWorkmateAdapter extends FirestoreRecyclerAdapter<User,JoiningWorkmateViewHolder> {

    private Context mContext;;

    public JoiningWorkmateAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        mContext = context;
    }


    @NonNull
    @Override
    public JoiningWorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workmate_restaurant_detail_activity, parent, false);
        return new JoiningWorkmateViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull JoiningWorkmateViewHolder holder, int position, @NonNull User model) {
        holder.displayData(model, mContext);
    }
}
