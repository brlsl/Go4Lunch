package com.example.go4lunch.controllers.activities;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.example.go4lunch.R;

import com.example.go4lunch.api.UserHelper;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;
import java.util.Objects;

public class ConnectActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 123;
    private ConstraintLayout mConstraintLayout;

    @Override
    public int getFragmentLayout() {
        return R.layout.connect_activity_first;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mConstraintLayout = findViewById(R.id.constraint_layout_connect_activity);
    }

    @Override
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        super.overridePendingTransition(enterAnim, exitAnim);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (this.isCurrentUserLogged()){
            this.startMainActivity();
        } else {
            mConstraintLayout.setOnClickListener(v -> {startSignInActivity();
            overridePendingTransition(R.anim.zoom_in,R.anim.static_animation);}
            );
        }

        overridePendingTransition(R.anim.zoom_out,R.anim.static_animation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode,resultCode, data);
    }


    AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
            .Builder(R.layout.connect_activity_signin)
            .setGoogleButtonId(R.id.google_sign_in)
            .setFacebookButtonId(R.id.facebook_sign_in)
            .build();

    private void startSignInActivity(){
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build() ,
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.AppTheme_NoTitle)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                RC_SIGN_IN
        );
    }

    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_in,R.anim.static_animation);

    }



    public void createUserInFirestore(){
        if (this.getCurrentUser() != null ){

            String urlPicture = (this.getCurrentUser().getPhotoUrl() != null) ? this.getCurrentUser().getPhotoUrl().toString() : null;
            if (urlPicture != null){
                urlPicture = urlPicture+"?height=250"; // for better resolution of profile picture
            }
            String username = this.getCurrentUser().getDisplayName();
            String uid = this.getCurrentUser().getUid();
            SharedPreferences pref = getPreferences(MODE_PRIVATE);
            pref.edit().putString("uid_key",getCurrentUser().getUid()).apply();

         //   if (UserHelper.getExistingUser(getCurrentUser().getUid())) { }
            UserHelper.createUser(uid,username,urlPicture).addOnFailureListener(this.onFailureListener());
        }
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data) {

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                //if( user exists in firebase){
                // query
                //  recupere si existe dans firebase
                //  FirebaseAuth.getInstance().getCurrentUser();}
                //else {}
                this.createUserInFirestore();
                Toast.makeText(this, R.string.connexion_succeeded, Toast.LENGTH_SHORT).show();
                this.startMainActivity();
            } else { // ERRORS
                if (response == null) {
                    Toast.makeText(this, R.string.authentification_canceled, Toast.LENGTH_SHORT).show();
                } else if (Objects.requireNonNull(response.getError()).getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(this, R.string.no_internet_connexion, Toast.LENGTH_SHORT).show();
                } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                    Toast.makeText(this, R.string.unknown_error_has_occurred, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}


