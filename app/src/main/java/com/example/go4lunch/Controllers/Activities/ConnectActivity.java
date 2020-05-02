package com.example.go4lunch.Controllers.Activities;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Arrays;

public class ConnectActivity extends BaseActivity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ok fonctionne
        if (this.isCurrentUserLogged()){
            this.startMainActivity();
        } else {
            this.startSignInActivity();
        }

        //TODO: faire un bouton déconnexion

    }

    @Override
    public int getFragmentLayout() {
        return R.layout.connect_layout;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handleResponseAfterSignIn(requestCode,resultCode, data);
    }

    AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
            .Builder(R.layout.connect_layout)
            .setGoogleButtonId(R.id.buttonGoogle)
            .setFacebookButtonId(R.id.buttonFacebook)
            .build();

    private void startSignInActivity(){
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(Arrays.asList(
                                new AuthUI.IdpConfig.GoogleBuilder().build() ,
                                new AuthUI.IdpConfig.FacebookBuilder().build()))
                        .setIsSmartLockEnabled(false, true)
                        .setTheme(R.style.AppTheme_NoActionBar)
                        .setAuthMethodPickerLayout(customLayout)
                        .build(),
                RC_SIGN_IN
        );

    }

    // Todo: créer le layout de l'activité principale
    private void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) { // SUCCESS
                Toast.makeText(this, "Connexion réussie", Toast.LENGTH_SHORT).show();
                this.startMainActivity();
            } else { // ERRORS
                Toast.makeText(this, "Connexion échouée", Toast.LENGTH_SHORT).show();
                }
            }
        }
}


