package com.example.triporganizer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class TripActivity extends AppCompatActivity {

    Button btnMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        btnMap = findViewById(R.id.btn_map);

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

    }

    private void openMap() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
}
