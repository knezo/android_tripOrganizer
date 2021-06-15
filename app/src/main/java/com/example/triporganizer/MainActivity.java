package com.example.triporganizer;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Adapters.TripAdapter;
import com.example.triporganizer.Fragments.FutureTripsFragment;
import com.example.triporganizer.Fragments.NotificationFragment;
import com.example.triporganizer.Fragments.PastTripsFragment;
import com.example.triporganizer.Fragments.ProfileFragment;
import com.example.triporganizer.Models.Trip;
import com.example.triporganizer.Models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
//    private ProgressBar progressBar;
//    private TripAdapter tripAdapter;
//    ArrayList<Trip> trips;
//    ArrayList<String> tripKeys;
//
    String userID;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        drawer = findViewById(R.id.drawer_layout);


        // navigation
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView textView = headerView.findViewById(R.id.nav_username);

        navigationView.setNavigationItemSelectedListener(this);


        // if user is logged in
        if(FirebaseAuth.getInstance().getCurrentUser() != null){

            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //get user from database
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(userID)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            user = snapshot.getValue(User.class);
                            textView.setText(user.username);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }

        //database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips");


        // hamburger toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.greenish1)));

        // actionbar drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        // open future tips fragment when app opened
        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FutureTripsFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_future_trips);
        }
    }



    // close navigation drawer
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    // clickListener on navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_future_trips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FutureTripsFragment()).commit();
                getSupportActionBar().setTitle("Future trips");
                break;
            case R.id.nav_past_trips:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PastTripsFragment()).commit();
                getSupportActionBar().setTitle("Past trips");
                break;
            case R.id.nav_notifications:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NotificationFragment()).commit();
                getSupportActionBar().setTitle("Notifications");
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment(user)).commit();
                getSupportActionBar().setTitle("Profile");
                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
