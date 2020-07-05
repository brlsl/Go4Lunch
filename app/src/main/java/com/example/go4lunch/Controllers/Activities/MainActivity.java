package com.example.go4lunch.controllers.activities;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private androidx.appcompat.widget.Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;

    //private ActivityMainBinding activityMainBinding;

    // bottom navigation View fragments configuration
    final MapFragment mMapFragment = new MapFragment();
    final RestaurantListFragment mRestaurantListFragment = new RestaurantListFragment();
    final Fragment mWorkmatesListFragment = new WorkmatesListFragment();
    final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = mMapFragment; // first fragment active when app opens

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.activity_main_your_meal:
                UserHelper.getUser(getCurrentUser().getUid()).addOnSuccessListener(documentSnapshot -> {
                    String userRestaurantChoiceId = Objects.requireNonNull(documentSnapshot.toObject(User.class)).getRestaurantChoiceId();
                    if (userRestaurantChoiceId != null) {
                        Intent intent = new Intent(getApplicationContext(), RestaurantDetailActivity.class);
                        intent.putExtra("PLACE_ID_KEY", userRestaurantChoiceId);
                        startActivity(intent);
                        Toast.makeText(this, "Your lunch", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(this, "You haven't choose any restaurant yet!", Toast.LENGTH_SHORT).show();
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
        //close navigation drawer after choice
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    //manage back button
    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // this.configureViewBinding();
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
        this.configureHeaderNavigationView();
        this.configureBottomView();


    }

    // 1 configure ViewBinding
    private void configureViewBinding(){
       /* // Main Activity Layout
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewActivityMain =  activityMainBinding.getRoot();
        setContentView(viewActivityMain);*/
    }

    // 2 - Configure Toolbar
    private void configureToolBar(){
        this.mToolbar =  findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mToolbar);
    }

    // 3 - Configure Drawer Layout (left menu drawer)
    private void configureDrawerLayout(){
        this.mDrawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 4 - Configure NavigationView
    private void configureNavigationView(){
        this.mNavigationView = findViewById(R.id.activity_main_navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);

    }


    // 5
    private void configureHeaderNavigationView() {
        View mHeaderView =  mNavigationView.getHeaderView(0);

        ImageView mUserAvatar = mHeaderView.findViewById(R.id.user_avatar);
        TextView mUserName = mHeaderView.findViewById(R.id.user_name);
        TextView mUserEmail = mHeaderView.findViewById(R.id.user_email);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Name, email address, and profile photo Url
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

    /*
    // pour le binding
        //activityMainNavHeaderBinding.userEmail.setText(email);
        }*/
    }

    // 6 - configure BottomView
    private void configureBottomView(){
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation_view);

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
