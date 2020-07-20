package com.example.go4lunch.controllers.activities;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.go4lunch.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public abstract class BaseActivity extends AppCompatActivity
{
    public String PLACE_API_KEY;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.getActivityLayout());
        PLACE_API_KEY =  getApplicationContext().getString(R.string.google_place_api_key);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // prevent screen orientation change

    }

    public abstract int getActivityLayout();

    protected FirebaseUser getCurrentUser(){ return FirebaseAuth.getInstance().getCurrentUser(); }

    protected Boolean isCurrentUserLogged(){ return (this.getCurrentUser() != null); }

    // if there is error during Firestore request
    protected OnFailureListener onFailureListener() {
        return e -> Toast.makeText(getApplicationContext(), R.string.unknown_error_has_occurred, Toast.LENGTH_LONG).show();
    }
}
