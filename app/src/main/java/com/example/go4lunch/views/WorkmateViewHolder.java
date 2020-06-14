package com.example.go4lunch.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    public ImageView mUserAvatar;
    public TextView mUserChoice;

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        mUserChoice = itemView.findViewById(R.id.rv_item_workmate_choice);
        mUserAvatar = itemView.findViewById(R.id.rv_item_workmate_avatar);
    }


    public void displayData(User user, Context context) {

        // prevent current user in recycler view
        String currentUserConnected = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (user.getUid().equals(currentUserConnected)) {
            itemView.setVisibility(View.INVISIBLE);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
        }

        UserHelper.getUser(user.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                User currentUser = documentSnapshot.toObject(User.class);

                // configure image
                Glide
                        .with(context)
                        .load(user.getUrlPicture())
                        .circleCrop()
                        .into(mUserAvatar);

                // configure choice text
                if (currentUser.getUserRestaurantChoiceId() == null) {
                    mUserChoice.setText(user.getUsername() + " has not chosen yet");
                } else
                    mUserChoice.setText(user.getUsername() + " is eating at " + user.getUserRestaurantChoiceId());

            }
        });

    }

}
