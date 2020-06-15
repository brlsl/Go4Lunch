package com.example.go4lunch.views.workmates_list_fragment_rv;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.example.go4lunch.R;
import com.example.go4lunch.models.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<User, WorkmateViewHolder> {

    private Context mContext;

    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context) {
        super(options);
        mContext = context;
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workmate_fragment_workmate_list,parent,false);
        return new WorkmateViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position, @NonNull User model) {
        holder.displayData(model,mContext);
    }
}
