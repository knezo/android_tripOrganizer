package com.example.triporganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.os.Debug;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private ProgressBar progressBar;
    private RecyclerView rvTripList;
    private DatabaseReference databaseReference;
    private TripAdapter tripAdapter;
    ArrayList<Trip> trips;
    ArrayList<String> tripKeys;

    int minute, hour;
    CardView card1;



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

        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");


        // recycleView
        rvTripList = (RecyclerView) findViewById(R.id.recycleviewTrips);
        rvTripList.setHasFixedSize(true);
        rvTripList.setLayoutManager(new LinearLayoutManager(this));

        trips = new ArrayList<>();
        tripKeys = new ArrayList<>();
        tripAdapter = new TripAdapter(this, trips, tripKeys);
        rvTripList.setAdapter(tripAdapter);
        // TODO: ako se nema šta prikazati u recycleview-u?

        // TODO: "bolji" query koji će pokazivat samo nadolazeće izlete
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



//        Calendar c = new GregorianCalendar();
//        Log.d("TIME", String.valueOf(c.getTimeInMillis()));
//
//        Timestamp t = new Timestamp(c.getTimeInMillis());
//        Log.d("TIME", String.valueOf(t.getTime()));
//
//        reference.orderByChild("name").equalTo("Klekec").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
////                Log.i("OUR INFO", "Printaj");
////                Trip tripic = snapshot.getValue(Trip.class);
//                Log.i("OUR INFO", String.valueOf(snapshot.getValue()));
//
////                Log.i("TRIPOVI", tripic.getName());
////                Log.i("TRIPOVI", tripic.getLocation());
////                Log.i("TRIPOVI", String.valueOf(tripic.getLatitude()));
////                Log.i("TRIPOVI", String.valueOf(tripic.getLongitude()));
////                Log.i("TRIPOVI", tripic.getOwnerID());
////                Log.i("TRIPOVI", String.valueOf(tripic.getDate()));
////
////                long date = tripic.getDate();
////                Timestamp t = new Timestamp(date);
////                Date d = new Date(t.getTime());
////
////                Log.d("TRIPOVI", String.valueOf(d.getMinutes()));
////                Log.d("TRIPOVI", String.valueOf(d));
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });



        // hamburger toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // actionbar drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //add new trip floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "you clikced on fab button", Toast.LENGTH_LONG).show();
//                openNewTripDialog();
                startActivity(new Intent(MainActivity.this, NewTripActivity.class));
            }
        });

        // TEMP - NOT USED
//        card1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openTripActivity();
//            }
//        });

    }




    // open trip activity
    private void openTripActivity() {
        Intent intent = new Intent(this, TripActivity.class);
        startActivity(intent);
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


    //new trip dialog - NOT USING
    public void openNewTripDialog() {
        DialogNewTrip dialogNewTrip = new DialogNewTrip();
        dialogNewTrip.show(getSupportFragmentManager(), "new trip dialog");

    }


    // clickListener on navigation items
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
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
