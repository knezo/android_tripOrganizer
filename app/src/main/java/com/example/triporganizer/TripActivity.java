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
import com.example.triporganizer.Models.Notification;
import com.example.triporganizer.Models.Task;
import com.example.triporganizer.Models.Tasklist;
import com.example.triporganizer.Models.Trip;
import com.example.triporganizer.Models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TripActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, DeleteTripDialog.DeleteTripDialogListener, AddUserToTripDialog.AddUserToTripDialogListener {

    TextView tvTripName, tvTripTime, tvTripDate, tvTripLocation;
    Button btnMap, btnGoogleMaps, btnComments;
    ImageButton ibTripOptions;

    String tripID;
    Float latitude, longitude;
    String newUserID;
    String newUserUsername;

    Trip currentTrip;
    ArrayList<String> currentTripMembers = new ArrayList<>();
    User currentUser;

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
        tvTripLocation = findViewById(R.id.tripLocation);

        getIncomingIntent();

        allTasklists = new ArrayList<>();

        // get Trip data and display it
        databaseReference = FirebaseDatabase.getInstance().getReference("Trips");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentTrip = snapshot.getValue(Trip.class);

                currentTripMembers = currentTrip.getMembers();

                assert currentTrip != null;
                tvTripName.setText(currentTrip.getName());
                tvTripTime.setText(Utils.timestampToTime(currentTrip.getDate()));
                tvTripDate.setText(Utils.timestampToDate(currentTrip.getDate()));

                latitude = currentTrip.getLatitude();
                longitude = currentTrip.getLongitude();

                String location = currentTrip.getLocation();
//                String location = currentTrip.getLocation().split(",")[0];
                tvTripLocation.setText(location);

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


        // get current (logged in) User;
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(currentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentUser = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
//                Toast.makeText(this, "Dodaje?? usera", Toast.LENGTH_SHORT).show();
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
//        Toast.makeText(this, "Sad bi izbrisao trip", Toast.LENGTH_SHORT).show();
        databaseReference.child(tripID).removeEventListener(valueEventListener);
        databaseReference.child(tripID).removeValue();
        finish();

    }


    // add user via dialog
    @Override
    public void onAddClicked(String username) {

        newUserID = "";
        newUserUsername = username;

        ArrayList<String> currentTripMembers = currentTrip.getMembers();
        

        FirebaseDatabase.getInstance().getReference("Users")
                .orderByChild("username").equalTo(username).limitToFirst(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            newUserID = user.userID;

                            // check if user is already member of trip
                            if (currentTripMembers.contains(newUserID)){
                                Toast.makeText(TripActivity.this, "User is already member of this trip!", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }

                        if (!newUserID.equals("")){
                            addUsertoTrip();
                        } else {
                            Toast.makeText(TripActivity.this, "Sorry, couldn't add user to trip, try again.", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }


    //      -> create new tasklist for new user
    //      -> add new user to trip
    //      -> create notification for users
    private void addUsertoTrip() {

        String tasklistID = tasklistReference.push().getKey();
        String tripID_userID = tripID + "_" + newUserID;
        ArrayList<Task> tasks = null;

        Tasklist tasklist = new Tasklist(tasklistID, tripID, newUserID, tripID_userID, newUserUsername, tasks);
        FirebaseDatabase.getInstance().getReference("Tasklists").child(tasklistID).setValue(tasklist);


        ArrayList<String> members = currentTrip.getMembers();
        members.add(newUserID);

        Map<String, Object> tripUpdate = new HashMap<>();
        tripUpdate.put("members", members);

        databaseReference.child(tripID).updateChildren(tripUpdate);


        // create notifitcations

        // send notification to "old" trip members
        for (String memberID : currentTripMembers){
//            Log.d("NOTIFICATION", "memberID: "+memberID);

            if (newUserID.equals(memberID)){
                continue;
            }

            String notificationTitle = "New user in your trip!";
            String notificationText = "User " + newUserUsername + " is added to your trip: " + currentTrip.getName() + ".";
            long timestamp = new Date().getTime();

            String notificationID = FirebaseDatabase.getInstance().getReference("Notifications").push().getKey();
            Notification notification = new Notification(notificationID, memberID, notificationTitle, notificationText, timestamp);

            FirebaseDatabase.getInstance().getReference("Notifications").child(notificationID).setValue(notification);
        }

        // send notification to new trip member
        String title = "You have new trip!";
        String text = "User: " + currentUser.username + " added you to trip: " + currentTrip.getName();
        long timestamp = new Date().getTime();

        String notificationID = FirebaseDatabase.getInstance().getReference("Notifications").push().getKey();
        Notification newUserNotification = new Notification(notificationID, newUserID, title, text, timestamp);

        FirebaseDatabase.getInstance().getReference("Notifications").child(notificationID).setValue(newUserNotification);

    }
}
