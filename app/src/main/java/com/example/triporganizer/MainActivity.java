package com.example.triporganizer;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Adapters.TripAdapter;
import com.example.triporganizer.Models.Trip;
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
    private ProgressBar progressBar;
    private TripAdapter tripAdapter;
    ArrayList<Trip> trips;
    ArrayList<String> tripKeys;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init
        drawer = findViewById(R.id.drawer_layout);
        FloatingActionButton fab = findViewById(R.id.fab_btn);
        progressBar = findViewById(R.id.pbTrips);

        // navigation
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //database

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips");


        // recycleView
        RecyclerView rvTripList = findViewById(R.id.recycleviewTrips);
        rvTripList.setHasFixedSize(true);
        rvTripList.setLayoutManager(new LinearLayoutManager(this));

        trips = new ArrayList<>();
        tripKeys = new ArrayList<>();
        tripAdapter = new TripAdapter(this, trips, tripKeys);
        rvTripList.setAdapter(tripAdapter);
        // TODO: ako se nema šta prikazati u recycleview-u?

        // TODO: "bolji" query koji će dohvaćat samo nadolazeće izlete
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                trips.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Trip trip = dataSnapshot.getValue(Trip.class);
                    trips.add(trip);

                    String tripKey = dataSnapshot.getKey();
                    tripKeys.add(tripKey);
                }
                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        // hamburger toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // actionbar drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //add new trip floating action button
        fab.setOnClickListener(v -> {
            Toast.makeText(MainActivity.this, "you clikced on fab button", Toast.LENGTH_LONG).show();
//                openNewTripDialog();
            startActivity(new Intent(MainActivity.this, NewTripActivity.class));
        });

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
            case R.id.nav_trips:
                // TODO: vidi jel uopće treba imat Trips, možda imat Trips i past Trips
                break;
            case R.id.nav_profile:
                // TODO: dodat uređivanje profila korisnika
                break;
            case R.id.nav_logout:
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                String name = user.getEmail();
//                Toast.makeText(MainActivity.this, name, Toast.LENGTH_SHORT).show();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

                break;
        }

        return true;
    }
}
