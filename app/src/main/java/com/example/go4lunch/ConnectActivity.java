package com.example.go4lunch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;

public class ConnectActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startSignInActivity();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
            .Builder(R.layout.connect_layout)
            .setGoogleButtonId(R.id.buttonGoogle)
            .setAnonymousButtonId(R.id.buttonGuest)
            .setFacebookButtonId(R.id.login_button)
            .build();

    private void startSignInActivity(){
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.AnonymousBuilder().build(),
                                new AuthUI.IdpConfig.GoogleBuilder().build() ,
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.AppTheme_NoActionBar)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                RC_SIGN_IN
        );

    }

}

