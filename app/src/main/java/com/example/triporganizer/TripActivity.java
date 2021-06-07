package com.example.triporganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triporganizer.Helpers.DeleteTripDialog;
import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.Models.Trip;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TripActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, DeleteTripDialog.DeleteTripDialogListener {

    TextView tvTripName, tvTripTime, tvTripDate;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    Button btnMap, btnGoogleMaps, btnComments;
    ImageButton ibTripOptions;
    String tripID;
    Float latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        tvTripName = findViewById(R.id.tripName);
        tvTripTime = findViewById(R.id.tripTime);
        tvTripDate = findViewById(R.id.tripDate);

        getIncomingIntent();

        // get Trip data and display it
        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);

                assert trip != null;
                tvTripName.setText(trip.getName());
                tvTripTime.setText(Utils.timestampToTime(trip.getDate()));
                tvTripDate.setText(Utils.timestampToDate(trip.getDate()));

                latitude = trip.getLatitude();
                longitude = trip.getLongitude();

//                Log.d("BAZA", longitude.toString());
//                Log.d("BAZA", latitude.toString());
//                Log.d("BAZA", Utils.timestampToTime(trip.getDate()));
//                Log.d("BAZA", Utils.timestampToDate(trip.getDate()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.child(tripID).addValueEventListener(valueEventListener);

        // open Map activity
        btnMap = findViewById(R.id.btn_map);
        btnMap.setOnClickListener(v -> openMap());

        // open Google Maps for directions
        btnGoogleMaps = findViewById(R.id.btn_googlemap);
        btnGoogleMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGoogleMaps();
            }
        });

        // open Comments and Pictures activity
        btnComments = findViewById(R.id.btn_comments);
        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openComments();
            }
        });

        // open Trip options
        ibTripOptions = findViewById(R.id.ib_trip_options);
        ibTripOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

    }

    // open Comments & Pictures intent
    private void openComments() {
        Intent intent = new Intent(this, CommentsActivity.class);
        intent.putExtra("trip_id", tripID);
        startActivity(intent);
    }

    // open Google Maps directions
    private void openGoogleMaps() {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("google.navigation:q=" + latitude + "," + longitude+"&model=d"));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        }
    }

    // menu for trip options
    private void showPopupMenu(View v){
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.trip_popup_menu);
        popupMenu.show();
    }

    // open new Map activity
    private void openMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    // get intent extras sent form main acitvity
    private void getIncomingIntent(){
        if(getIntent().hasExtra("trip_id")){
            tripID = getIntent().getStringExtra("trip_id");
//            Toast.makeText(this, tripID, Toast.LENGTH_SHORT).show();
        }
    }

    // trip options - edit or delete
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popup_trip_edit:
//                Toast.makeText(this, "edit", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, EditTripActivity.class);
                intent.putExtra("trip_id", tripID);
                startActivity(intent);
                return true;
            case R.id.popup_trip_delete:
//                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                openDeleteDialog();
                return true;
        }
        return false;
    }

    // trip options -> delete trip -> open Dialog
    public void openDeleteDialog(){
        DeleteTripDialog deleteTripDialog = new DeleteTripDialog();
        deleteTripDialog.show(getSupportFragmentManager(), "Example Dialog");
    }

    // delete trip using Dialog
    @Override
    public void onDeleteClicked() {
        Toast.makeText(this, "Sad bi izbrisao trip", Toast.LENGTH_SHORT).show();
        databaseReference.child(tripID).removeEventListener(valueEventListener);
        databaseReference.child(tripID).removeValue();
        finish();

    }
}
