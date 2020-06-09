package com.example.go4lunch.controllers.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.go4lunch.R;
import com.example.go4lunch.controllers.fragments.MapFragment;
import com.example.go4lunch.controllers.fragments.RestaurantListFragment;
import com.example.go4lunch.controllers.fragments.WorkmatesFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private BottomNavigationView mBottomNavigationView;

    //private ActivityMainBinding activityMainBinding;



    // bottom navigation View fragments configuration
    final MapFragment mMapFragment = new MapFragment();
    final RestaurantListFragment mRestaurantListFragment = new RestaurantListFragment();
    final Fragment mWorkmatesListFragment = new WorkmatesFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = mMapFragment; // first fragment active when app opens

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

    }

    private void testAutocomplete(String input){
        // Initialize Places.
        Places.initialize(getApplicationContext(), PLACE_API_KEY_);
        // Create new Places Client Instance
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(input)
                .build();


        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i("MainActivity", prediction.getPlaceId());
                Log.i("MainActivity", prediction.getPrimaryText(null).toString());

            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("MainActivity", "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_restaurant_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.restaurant_action_search).getActionView();

        searchView.setQueryHint("Search a restaurant");

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query.trim().length() > 2 && mMapFragment.getLocation() != null) {
                    mMapFragment.executeHttpRequestAutoCompleteWithRetrofit(query);

                    // TODO: configurer adapter de la RV avec liste de Predictions
                }


                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }




    public RestaurantListFragment getRestaurantListFragment() {
        return mRestaurantListFragment;
    }


    @Override
    public int getFragmentLayout() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.activity_main_your_meal:
                //TODO: montrer le restaurant choisi
                Toast.makeText(this, "Mon repas", Toast.LENGTH_SHORT).show();
                break;
            case R.id.activity_main_drawer_settings:
                // TODO: ouvrir les paramètres
                Toast.makeText(this, "Les paramètres de l'application", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.activity_main_drawer_logout:
                this.signOutUserFromFirebase();
                break;

        }
        //close navigation drawer after choice
        //drawerLayout.closeDrawer(GravityCompat.START);
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
        this.toolbar =  findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    // 3 - Configure Drawer Layout (left menu drawer)
    private void configureDrawerLayout(){
        this.mDrawerLayout = findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
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
        this.mBottomNavigationView = findViewById(R.id.bottom_navigation_view);

        fm.beginTransaction()
                .add(R.id.fragment_container, mMapFragment)
                .add(R.id.fragment_container, mRestaurantListFragment).hide(mRestaurantListFragment)
                .add(R.id.fragment_container, mWorkmatesListFragment).hide(mWorkmatesListFragment)
                .commit();

        //manage click on bottom nav view
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_navigation_map:
                        fm.beginTransaction().hide(active).show(mMapFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        active = mMapFragment;
                        return true;
                    case R.id.bottom_navigation_restaurant_list:
                        fm.beginTransaction().hide(active).show(mRestaurantListFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        active = mRestaurantListFragment;
                        return true;
                    case R.id.bottom_navigation_workmates_list:
                        fm.beginTransaction().hide(active).show(mWorkmatesListFragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit();
                        active = mWorkmatesListFragment;
                        return true;
                }
                return false;
            }
        });
    }


    // configure sign out
    private void signOutUserFromFirebase(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // user is now signed out
                        startActivity(new Intent(MainActivity.this, ConnectActivity.class));
                        finish();
                    }
                });
    }

}
