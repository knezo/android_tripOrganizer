package com.example.triporganizer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.Models.Trip;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditTripActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    EditText etEditName, etEditTime, etEditDate, etEditLocation, etEditLat, etEditLng;
    TextView tvEditName;
    ImageButton ibEditClear;
    Button btnUpdateTrip;
    String tripID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_trip);

        getIncomingIntent();

        // textviews
        tvEditName = findViewById(R.id.tv_edit_name);
        // edittexts
        etEditName = findViewById(R.id.et_edit_name);
        etEditLocation = findViewById(R.id.et_edit_location);
        etEditLat = findViewById(R.id.et_edit_latitude);
        etEditLng = findViewById(R.id.et_edit_longitude);
        etEditTime = findViewById(R.id.et_edit_time);
        etEditDate = findViewById(R.id.et_edit_date);
        // buttons
        ibEditClear = findViewById(R.id.ib_edit_clear);
        btnUpdateTrip = findViewById(R.id.btn_edit_update);


        // Places API
        Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));

        // Single event, get data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");
        databaseReference.child(tripID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Trip trip = snapshot.getValue(Trip.class);

                assert trip != null;
                tvEditName.setText(trip.getName());
                etEditName.setText(trip.getName());
                etEditLocation.setText(trip.getLocation());
                etEditLat.setText(String.valueOf(trip.getLatitude()));
                etEditLng.setText(String.valueOf(trip.getLongitude()));
                etEditTime.setText(Utils.timestampToTime(trip.getDate()));
                etEditDate.setText(Utils.timestampToDate(trip.getDate()));

                Toast.makeText(EditTripActivity.this, "Tostirajmo", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // click listener for Time picker
        etEditTime.setFocusable(false);
        etEditTime.setKeyListener(null);
        etEditTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(v);
            }
        });

        // click listener for Date picker
        etEditDate.setFocusable(false);
        etEditDate.setKeyListener(null);
        etEditDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });

        // click for autocomplete from Google Places API
        etEditLocation.setFocusable(false);
        etEditLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(EditTripActivity.this);

                startActivityForResult(intent, 100);
            }
        });

        // clear location, lat and lng when clicked X
        ibEditClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etEditLocation.setText("");
                etEditLat.setText("");
                etEditLng.setText("");
            }
        });

        // update trip data
        btnUpdateTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTrip();
            }
        });

    }

    private void updateTrip() {
        if(!checkEditTexts()){
            return;
        }

        String name = etEditName.getText().toString().trim();
        String location = etEditLocation.getText().toString().trim();
        float lat = Float.parseFloat(etEditLat.getText().toString());
        float lng = Float.parseFloat(etEditLng.getText().toString());
        long date = Utils.timeToTimestamp(etEditTime.getText().toString(), etEditDate.getText().toString());

//        Toast.makeText(this, String.valueOf(date), Toast.LENGTH_SHORT).show();

        Map<String, Object> tripUpdates = new HashMap<>();
        tripUpdates.put("name", name);
        tripUpdates.put("location", location);
        tripUpdates.put("latitude", lat);
        tripUpdates.put("longitude", lng);
        tripUpdates.put("date", date);


        databaseReference.child(tripID).updateChildren(tripUpdates).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(EditTripActivity.this, "Trip is successfully updated!", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private boolean checkEditTexts() {
        if(etEditName.getText().toString().matches("")){
            etEditName.setError("Trip name is required!");
            etEditName.requestFocus();
            return false;
        }
        if(etEditLocation.getText().toString().matches("") || etEditLat.getText().toString().matches("") || etEditLng.getText().toString().matches("")){
            Toast.makeText(EditTripActivity.this, "Trip location is requried!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEditTime.getText().toString().matches("")){
            Toast.makeText(EditTripActivity.this, "Trip time is requried!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etEditDate.getText().toString().matches("")){
            Toast.makeText(EditTripActivity.this, "Trip date is requried!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    private void getIncomingIntent(){
        if(getIntent().hasExtra("trip_id")){
            tripID = getIntent().getStringExtra("trip_id");
            Toast.makeText(this, tripID, Toast.LENGTH_SHORT).show();
        }
    }

    // time picker for new Trip
    public void openTimePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(EditTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                int hour = hourOfDay;
                int minute = min;
                String minuteStr = String.valueOf(min);
                if (minute < 10) {minuteStr = "0" + minuteStr;} // if min is < 10, e.g. min = 5, show as '05'
                etEditTime.setText(String.valueOf(hour + ":" + minuteStr));
//                Toast.makeText(getApplicationContext(), hour+":"+minute, Toast.LENGTH_SHORT).show();

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    // date picker for new Trip
    public void openDatePicker(View view){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                EditTripActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "." + month + "." + year + ".";
                etEditDate.setText(date);
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    // autocomplete from Google Places API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ok
        if (requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);

            etEditLocation.setText(place.getAddress());

            String positionLatLng = String.valueOf(place.getLatLng());
            Toast.makeText(getApplicationContext(), positionLatLng, Toast.LENGTH_LONG).show();
            etEditLat.setText(String.valueOf(place.getLatLng().latitude));
            etEditLng.setText(String.valueOf(place.getLatLng().longitude));

            //not ok
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR){
            Toast.makeText(getApplicationContext(), "To baš i ne radi", Toast.LENGTH_LONG).show();
        }
    }
}
