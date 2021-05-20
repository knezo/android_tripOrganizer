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

        FloatingActionButton fab = findViewById(R.id.fab_btn);
        drawer = findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        card1 = findViewById(R.id.card_view);
        System.out.println(card1);

        // hamburger toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        //add new trip floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "you clikced on fab button", Toast.LENGTH_LONG).show();
                openNewTripDialog();
            }
        });



        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTripActivity();
            }
        });


//        btn_time.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar calendar = Calendar.getInstance();
//                TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                        @Override
//                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                            hour = hourOfDay;
//                            min = minute;
////                            Toast.makeText(getApplicationContext(), hour+":"+minute, Toast.LENGTH_SHORT).show();
//                            calend ar.set(0,0,0,hour,min);
////                            newTripTime.setText(DateFormat.format("hh:mm aa", calendar));
//
//                        }
//                    }, 12, 0, true);
//                timePickerDialog.show();
//                Toast.makeText(MainActivity.this, "kakica", Toast.LENGTH_LONG).show();
//            }
//        });
    }

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

    //new trip dialog
    public void openNewTripDialog() {
        DialogNewTrip dialogNewTrip = new DialogNewTrip();
        dialogNewTrip.show(getSupportFragmentManager(), "new trip dialog");

    }

    public void openTimePicker(View view) {
//        Toast.makeText(MainActivity.this, "kakica", Toast.LENGTH_LONG).show();
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                hour = hourOfDay;
                minute = min;
//                Toast.makeText(getApplicationContext(), hour+":"+minute, Toast.LENGTH_SHORT).show();
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

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
