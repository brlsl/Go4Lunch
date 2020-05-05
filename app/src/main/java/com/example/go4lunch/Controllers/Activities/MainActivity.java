package com.example.go4lunch.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.go4lunch.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private androidx.appcompat.widget.Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;



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
                break;
            case R.id.activity_main_drawer_logout:
                this.signOutUserFromFirebase();
                break;

                // TODO: pour le bottom navigationView à déplacer
                /*
            case R.id.bottom_navigation_map:
                //TODO: ouvrir fragment de la carte
                Toast.makeText(this, "Ouverture de la carte", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_navigation_restaurant_list:
                //TODO: ouvrir fragment de la liste de restaurants
                Toast.makeText(this, "Ouverture de la liste de restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_navigation_workmates_list:
                //TODO: ouvrir fragment de la liste des collègues
                Toast.makeText(this, "Ouverture de la liste des collègues", Toast.LENGTH_SHORT).show();
                break;
                */
            default:
                break;

        }
        //close navigation drawer after choice
        //drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configureToolBar();
        this.configureDrawerLayout();
        this.configureNavigationView();
       // ne fonctionne pas
        // this.configureBottomView();
    }

    //manage back button
    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
        }
    }

    // 1 - Configure Toolbar
    private void configureToolBar(){
        this.toolbar =  findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);
    }

    // 2 - Configure Drawer Layout
    private void configureDrawerLayout(){
        this.drawerLayout = (DrawerLayout) findViewById(R.id.activity_main_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // 3 - Configure NavigationView
    private void configureNavigationView(){
        this.navigationView = (NavigationView) findViewById(R.id.activity_main_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // 4 - configure BottomView
    private void configureBottomView(){
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                return updateMainFragment(item.getItemId());
            }
        });
    }


    private Boolean updateMainFragment(Integer integer){
        switch (integer) {
            case R.id.bottom_navigation_map:
                //TODO: ouvrir fragment de la carte
                Toast.makeText(this, "Ouverture de la carte", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_navigation_restaurant_list:
                //TODO: ouvrir fragment de la liste de restaurants
                Toast.makeText(this, "Ouverture de la liste de restaurants", Toast.LENGTH_SHORT).show();
                break;
            case R.id.bottom_navigation_workmates_list:
                //TODO: ouvrir fragment de la liste des collègues
                Toast.makeText(this, "Ouverture de la liste des collègues", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;

        }
        return true;
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
