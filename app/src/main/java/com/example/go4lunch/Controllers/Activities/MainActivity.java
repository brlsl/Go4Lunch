package com.example.go4lunch.controllers.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;

import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.fragments.MapFragment;
import com.example.go4lunch.controllers.fragments.RestaurantListFragment;
import com.example.go4lunch.controllers.fragments.WorkmatesListFragment;
import com.example.go4lunch.models.User;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    // ----- FOR UI -----
    private androidx.appcompat.widget.Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    final MapFragment mMapFragment = new MapFragment();
    final RestaurantListFragment mRestaurantListFragment = new RestaurantListFragment();
    final Fragment mWorkmatesListFragment = new WorkmatesListFragment();
    final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = mMapFragment; // first fragment active when app opens

    @Override
    public int getActivityLayout() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureHeaderNavigationView();
        this.configureBottomView();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) { // menu drawer
        switch (menuItem.getItemId()) {
            case R.id.activity_main_your_meal:
                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                    String userRestaurantChoiceId = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getRestaurantChoiceId();
                    if (userRestaurantChoiceId != null) {
                        Intent intent = new Intent(getApplicationContext(), RestaurantDetailActivity.class);
                        intent.putExtra("PLACE_ID_KEY", userRestaurantChoiceId);
                        startActivity(intent);
                        Snackbar.make(mDrawerLayout, R.string.your_restaurant_choice, Snackbar.LENGTH_SHORT).show();
                    } else
                        Snackbar.make(mDrawerLayout, R.string.you_have_not_choose_any_restaurant, Snackbar.LENGTH_SHORT).show();
                });
                break;
            case R.id.activity_main_drawer_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_main_drawer_logout:
                this.signOutUserFromFirebase();
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);  //close navigation drawer after choice
        return true;
    }


    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else { // in home activity, permit to go back to home screen
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    // ----- UI CONFIGURATION -----
    private void configureToolBar(){
        this.mToolbar =  findViewById(R.id.toolbar_main_activity);
        setSupportActionBar(mToolbar);
    }

    // left menu drawer
    private void configureDrawerLayout(){
        this.mDrawerLayout = findViewById(R.id.drawer_layout_main_activity);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // menu drawer contents
    private void configureNavigationView(){
        this.mNavigationView = findViewById(R.id.navigation_view_main_activity);
        mNavigationView.setNavigationItemSelectedListener(this);
    }

    private void configureHeaderNavigationView() {
        View mHeaderView =  mNavigationView.getHeaderView(0);

        ImageView mUserAvatar = mHeaderView.findViewById(R.id.user_avatar);
        TextView mUserName = mHeaderView.findViewById(R.id.user_name);
        TextView mUserEmail = mHeaderView.findViewById(R.id.user_email);

        FirebaseUser user = getCurrentUser();

        if (user != null) {
            // User name, email address, and avatar picture
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            mUserName.setText(name);
            mUserEmail.setText(email);

            Glide.with(this)
                    .load(photoUrl)
                    .circleCrop()
                    .into(mUserAvatar);
        }
    }

    private void configureBottomView(){
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation_view);

        //prevent fragment recreation each time we swap fragments
        fm.beginTransaction()
                .add(R.id.fragment_container, mMapFragment)
                .add(R.id.fragment_container, mRestaurantListFragment).hide(mRestaurantListFragment)
                .add(R.id.fragment_container, mWorkmatesListFragment).hide(mWorkmatesListFragment)
                .commit();

        //manage click on bottom navigation view
        mBottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottom_navigation_map:
                    fm.beginTransaction().hide(active).show(mMapFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    active = mMapFragment;
                    configureToolbarTitleAndColor(R.string.map_view_title);
                    return true;
                case R.id.bottom_navigation_restaurant_list:
                    fm.beginTransaction().hide(active).show(mRestaurantListFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    active = mRestaurantListFragment;
                    configureToolbarTitleAndColor(R.string.restaurant_list_title);
                    return true;
                case R.id.bottom_navigation_workmates_list:
                    fm.beginTransaction().hide(active).show(mWorkmatesListFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                    active = mWorkmatesListFragment;
                    configureToolbarTitleAndColor(R.string.workmates_list_title);
                    return true;
            }
            return false;
        });
    }

    private void configureToolbarTitleAndColor(int title){
        mToolbar.setTitle(title);
        mToolbar.setTitleTextColor(Color.WHITE);
    }

    // configure sign out
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> {
                    // user is now signed out
                    startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                    finish();
                });
    }

    // --- GETTERS ---

    public MapFragment getMapFragment(){
        return mMapFragment;
    }

    public RestaurantListFragment getRestaurantListFragment() {
        return mRestaurantListFragment;
    }
}