package com.example.go4lunch.views.workmates_list_rv_restaurant_detail_activity;

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
import com.google.firebase.firestore.DocumentSnapshot;

public class JoiningWorkmateViewHolder extends RecyclerView.ViewHolder {

    private ImageView mUserAvatar;
    private TextView mTextViewIsJoining;

    public JoiningWorkmateViewHolder(@NonNull View itemView) {
        super(itemView);
        mUserAvatar = itemView.findViewById(R.id.avatar_workmate_joining_image_view_restaurant_detail_activity);
        mTextViewIsJoining = itemView.findViewById(R.id.user_is_joining_text_view_restaurant_detail_activity);
    }

    public void displayData(User model, String mRestaurantId, Context context) {
        UserHelper.getAllUser(model.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User databaseUser = documentSnapshot.toObject(User.class);

                if(!databaseUser.getRestaurantChoiceId().equals(mRestaurantId)){
                    //itemView.setVisibility(View.GONE);
                    //itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0));
                }else {
                    // configure avatar
                    Glide
                            .with(context)
                            .load(model.getUrlPicture())
                            .circleCrop()
                            .into(mUserAvatar);

                    //configure text
                    mTextViewIsJoining.setText(databaseUser.getUsername()+ " is joining!");
                }
            }
        });
    }
}
