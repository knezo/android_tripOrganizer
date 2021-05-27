package com.example.triporganizer;

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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class NewTripActivity extends AppCompatActivity {

    private EditText etName, etLocation, etLat, etLng, etDate, etTime;
    private Button btnAdd;
    private ImageButton ibClearLocation;

    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);

        etName = findViewById(R.id.edittext_trip_name);
        etLocation = findViewById(R.id.edittextLocation);
        etLat = findViewById(R.id.edittext_latitude);
        etLng = findViewById(R.id.edittext_longitude);
        etDate = findViewById(R.id.edittext_date);
        etTime = findViewById(R.id.edittext_time);
        btnAdd = findViewById(R.id.button_addtrip);
        ibClearLocation = findViewById(R.id.imageButton_clearlocation);

        // Places API
        Places.initialize(getApplicationContext(), getString(R.string.MAPS_API_KEY));


        // click listener for Time picker
        etTime.setFocusable(false);
        etTime.setKeyListener(null);
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTimePicker(v);
            }
        });

        // click listener for Date picker
        etDate.setFocusable(false);
        etDate.setKeyListener(null);
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePicker(v);
            }
        });


        // click for autocomplete from Google Places API
        etLocation.setFocusable(false);
        etLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);

                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(NewTripActivity.this);

                startActivityForResult(intent, 100);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTrip();
            }
        });

        ibClearLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etLocation.setText("");
                etLat.setText("");
                etLng.setText("");
            }
        });
    }


    // create new trip in database
    private void addNewTrip() {
        //if editTexts not valid -> return
        if(!checkEditTexts()){
            return;
        }

        database = FirebaseDatabase.getInstance();
        reference = database.getReference("Trips");

        String name = etName.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        float lat = Float.parseFloat(etLat.getText().toString());
        float lng = Float.parseFloat(etLng.getText().toString());
        String ownerID = FirebaseAuth.getInstance().getCurrentUser().getUid();

//        Trip trip = new Trip(name, location, lat, lng, ownerID);
//        String tripKey = reference.push().getKey();

//        reference.child(tripKey).setValue(trip);

        Toast.makeText(NewTripActivity.this, "Location"+ location, Toast.LENGTH_LONG).show();
    }

    //check if editexts are valid
    private boolean checkEditTexts() {
        if(etName.getText().toString().matches("")){
            etName.setError("Trip name is required!");
            etName.requestFocus();
            return false;
        }
        if(etLocation.getText().toString().matches("") || etLat.getText().toString().matches("") || etLng.getText().toString().matches("")){
            Toast.makeText(NewTripActivity.this, "Trip location is requried!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etTime.getText().toString().matches("")){
            Toast.makeText(NewTripActivity.this, "Trip time is requried!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etDate.getText().toString().matches("")){
            Toast.makeText(NewTripActivity.this, "Trip date is requried!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // autocomplete from Google Places API
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ok
        if (requestCode == 100 && resultCode == RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);

            etLocation.setText(place.getAddress());

            String positionLatLng = String.valueOf(place.getLatLng());
            Toast.makeText(getApplicationContext(), positionLatLng, Toast.LENGTH_LONG).show();
            etLat.setText(String.valueOf(place.getLatLng().latitude));
            etLng.setText(String.valueOf(place.getLatLng().longitude));

        //not ok
        } else if (requestCode == AutocompleteActivity.RESULT_ERROR){
            Toast.makeText(getApplicationContext(), "To baš i ne radi", Toast.LENGTH_LONG).show();
        }
    }


    // time picker for new Trip
    public void openTimePicker(View view) {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(NewTripActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                int hour = hourOfDay;
                int minute = min;
                String minuteStr = String.valueOf(min);
                if (minute < 10) {minuteStr = "0" + minuteStr;} // if min is < 10, e.g. min = 5, show as '05'
                etTime.setText(String.valueOf(hour + ":" + minuteStr));
//                Toast.makeText(getApplicationContext(), hour+":"+minute, Toast.LENGTH_SHORT).show();

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.show();
    }

    // date picker for new Trip
    public void openDatePicker(View view){
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                NewTripActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;
                String date = dayOfMonth + "." + month + "." + year + ".";
                etDate.setText(date);
            }
        },calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }


}
