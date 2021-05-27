package com.example.triporganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    int minute, hour;
    CardView card1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawer = findViewById(R.id.drawer_layout);
        card1 = findViewById(R.id.card_view);
        FloatingActionButton fab = findViewById(R.id.fab_btn);
        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("Trips");

        Log.i("OUR INFO", "Predprint");

//        reference.child("-Ma_pinbzr411Hfy2TOx").child("name").get()
        reference.child("-Ma_pZKITe10U2JBTm6S").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.i("OUR INFO", "Printaj");
                Trip tripic = snapshot.getValue(Trip.class);
                Log.i("OUR INFO", String.valueOf(snapshot.getValue()));

                Log.i("TRIPOVI", tripic.getName());
                Log.i("TRIPOVI", tripic.getLocation());
                Log.i("TRIPOVI", String.valueOf(tripic.getLatitude()));
                Log.i("TRIPOVI", String.valueOf(tripic.getLongitude()));
                Log.i("TRIPOVI", tripic.getOwnerID());




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
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "you clikced on fab button", Toast.LENGTH_LONG).show();
//                openNewTripDialog();
                startActivity(new Intent(MainActivity.this, NewTripActivity.class));
            }
        });

        // TEMP - NOT USED
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTripActivity();
            }
        });

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
