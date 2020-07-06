package com.example.go4lunch.controllers.activities;

import android.content.Intent;

import android.content.pm.ActivityInfo;
import android.os.Bundle;



import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.firebase.ui.auth.AuthUI;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // prevent screen orientation change
    }


    public static class SettingsFragment extends PreferenceFragmentCompat {

        static final String RADIUS_KEY = "radius";
        public static final String DELETE_ACCOUNT_KEY = "delete_account";

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            ListPreference radiusPreferences = findPreference(RADIUS_KEY);

            if (radiusPreferences != null) {
                PreferenceManager
                        .getDefaultSharedPreferences(radiusPreferences.getContext())
                        .getString(RADIUS_KEY, "500");

            }
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference deleteAccount = findPreference(DELETE_ACCOUNT_KEY);
            if (deleteAccount!= null){
                deleteAccount.setOnPreferenceClickListener(preference -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                    builder.setMessage(R.string.delete_account_question);
                    builder.setPositiveButton("Yes", (dialog, which) ->
                            UserHelper.deleteUser(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                            .addOnSuccessListener(aVoid -> AuthUI.getInstance()
                                    .signOut(requireContext())
                                    .addOnCompleteListener(task ->
                                            {
                                        Intent intent = new Intent(requireActivity(), ConnectActivity.class);
                                        requireActivity().finishAffinity();
                                        startActivity(intent);
                                            }
                                    )));
                    builder.setNegativeButton(R.string.answer_no, null);
                    builder.show();
                    return true;
                });
            }
        }
    }
}