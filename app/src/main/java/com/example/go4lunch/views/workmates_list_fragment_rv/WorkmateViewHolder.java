package com.example.go4lunch.views.workmates_list_fragment_rv;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.RestaurantDetailActivity;
import com.example.go4lunch.models.User;

public class WorkmateViewHolder extends RecyclerView.ViewHolder {

    public ImageView mUserAvatar;
    public TextView mUserChoice;

    public WorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        mUserChoice = itemView.findViewById(R.id.rv_item_workmate_choice);
        mUserAvatar = itemView.findViewById(R.id.rv_item_workmate_avatar);
    }

    public void displayData(User model, Context context) {
        UserHelper.getUser(model.getUid()).addOnSuccessListener(documentSnapshot -> {
            User databaseUser = documentSnapshot.toObject(User.class);
            // configure avatar
            Glide
                    .with(context)
                    .load(model.getUrlPicture())
                    .circleCrop()
                    .into(mUserAvatar);

            // configure choice text
            assert databaseUser != null;
            if (databaseUser.getRestaurantChoiceId() == null ) {
                mUserChoice.setText(context.getString(R.string.user_has_not_choose_yet, model.getUsername()));
            } else
                mUserChoice.setText(context.getString(R.string.is_eating_at_restaurant_name,model.getUsername(), model.getRestaurantChoiceName()));

            // open restaurant details on item click
            itemView.setOnClickListener(v -> {
                if (databaseUser.getRestaurantChoiceId() != null){
                    Intent intent = new Intent(context, RestaurantDetailActivity.class);
                    intent.putExtra("PLACE_ID_KEY", model.getRestaurantChoiceId());
                    context.startActivity(intent);
                }
            });
        });
    }

}
