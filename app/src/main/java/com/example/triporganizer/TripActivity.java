package com.example.triporganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.triporganizer.Adapters.TasklistAdapter;
import com.example.triporganizer.Helpers.AddUserToTripDialog;
import com.example.triporganizer.Helpers.DeleteTripDialog;
import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.Models.Task;
import com.example.triporganizer.Models.Tasklist;
import com.example.triporganizer.Models.Trip;
import com.example.triporganizer.Models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TripActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, DeleteTripDialog.DeleteTripDialogListener, AddUserToTripDialog.AddUserToTripDialogListener {

    TextView tvTripName, tvTripTime, tvTripDate;
    Button btnMap, btnGoogleMaps, btnComments;
    ImageButton ibTripOptions;

    String tripID;
    Float latitude, longitude;
    String newUserID;
    String newUserUsername;

    Trip currentTrip;

    DatabaseReference databaseReference;
    DatabaseReference tasklistReference;
    ValueEventListener valueEventListener;

    RecyclerView tasklistRecycleview;
    TasklistAdapter tasklistAdapter;
    ArrayList<Tasklist> allTasklists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        tvTripName = findViewById(R.id.tripName);
        tvTripTime = findViewById(R.id.tripTime);
        tvTripDate = findViewById(R.id.tripDate);

        getIncomingIntent();

        allTasklists = new ArrayList<>();

        // get Trip data and display it
        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentTrip = snapshot.getValue(Trip.class);

                assert currentTrip != null;
                tvTripName.setText(currentTrip.getName());
                tvTripTime.setText(Utils.timestampToTime(currentTrip.getDate()));
                tvTripDate.setText(Utils.timestampToDate(currentTrip.getDate()));

                latitude = currentTrip.getLatitude();
                longitude = currentTrip.getLongitude();

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


        tasklistRecycleview = findViewById(R.id.rv_trip_tasklists);
        tasklistRecycleview.setHasFixedSize(true);
        tasklistRecycleview.setLayoutManager(new LinearLayoutManager(this));
        tasklistAdapter =  new TasklistAdapter(this, allTasklists);
        tasklistRecycleview.setAdapter(tasklistAdapter);

        // get trips tasklists
        tasklistReference = FirebaseDatabase.getInstance().getReference("Tasklists");
        tasklistReference.orderByChild("tripID").equalTo(tripID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allTasklists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Tasklist tasklist = dataSnapshot.getValue(Tasklist.class);
                    Log.d("TASK", "tasklist id: " + tasklist.getTasklistID());
                    allTasklists.add(tasklist);
                    // TODO: tu nastavi
                }
                tasklistAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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

    // trip options - addUser, edit or delete
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popup_trip_adduser:
                Toast.makeText(this, "Dodaješ usera", Toast.LENGTH_SHORT).show();
                AddUserToTripDialog addUserToTripDialog = new AddUserToTripDialog(this);
                addUserToTripDialog.show(getSupportFragmentManager(), "adduser dialog");
                return true;
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


    // add user via dialog
    @Override
    public void onAddClicked(String username) {

        newUserID = "";
        newUserUsername = username;

        //TODO: provjeri dodaje li već postojećeg usera za taj trip

        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("username").equalTo(username).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            newUserID = user.userID;

                            Log.d("USER", "Id dodanog usera je: " + newUserID);
                            Log.d("USER", "user je: " + user.toString());

                        }
                        
                        // if successfully got userID from username 
                        //      -> create new tasklist for new user
                        //      -> add new user to trip
                        if (!newUserID.equals("")){
                            Toast.makeText(TripActivity.this, "Ok je", Toast.LENGTH_SHORT).show();
                            addUsertoTrip();

                        } else {
                            Toast.makeText(TripActivity.this, "Sorry, couldn't add user to trip, try again.", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


//        Tasklist tasklist = new Tasklist(tasklistID, )

//        Toast.makeText(this, username, Toast.LENGTH_SHORT).show();
    }


    //      -> create new tasklist for new user
    //      -> add new user to trip
    private void addUsertoTrip() {
        String tasklistID = tasklistReference.push().getKey();
        String tripID_userID = tripID + "_" + newUserID;
        ArrayList<Task> tasks = null;

        Tasklist tasklist = new Tasklist(tasklistID, tripID, newUserID, tripID_userID, newUserUsername, tasks);
        FirebaseDatabase.getInstance().getReference("Tasklists").child(tasklistID).setValue(tasklist);



        //TODO: dodaje usera tripu
        ArrayList<String> members = currentTrip.getMembers();
        members.add(newUserID);
//        Log.d("USER", "trip members: " + members.toString());


        Map<String, Object> tripUpdate = new HashMap<>();
        tripUpdate.put("members", members);

//        Log.d("USER", "trip update: " + tripUpdate.toString());

        databaseReference.child(tripID).updateChildren(tripUpdate);


    }


}
