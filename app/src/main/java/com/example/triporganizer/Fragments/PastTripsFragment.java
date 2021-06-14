package com.example.triporganizer.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Adapters.TripAdapter;
import com.example.triporganizer.Models.Trip;
import com.example.triporganizer.NewTripActivity;
import com.example.triporganizer.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class PastTripsFragment extends Fragment {

    private ProgressBar progressBar;
    private TripAdapter tripAdapter;
    ArrayList<Trip> trips;
    ArrayList<String> tripKeys;

    String userID;


    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trips, container, false);

        //init
        FloatingActionButton fab = view.findViewById(R.id.fab_btn);
        progressBar = view.findViewById(R.id.pbTrips);
        TextView tvNoTrips = view.findViewById(R.id.tv_noTrips);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Log.d("USER", "current user id: " + userID);

        // database
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Trips");


        // recycleView
        RecyclerView rvTripList = view.findViewById(R.id.recycleviewTrips);
        rvTripList.setHasFixedSize(true);
        rvTripList.setLayoutManager(new LinearLayoutManager(getActivity()));

        trips = new ArrayList<>();
        tripKeys = new ArrayList<>();
        tripAdapter = new TripAdapter(getActivity(), trips, tripKeys);
        rvTripList.setAdapter(tripAdapter);

        // get all trips IN PAST
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                trips.clear();

                Date today = new Date();
                long currentTimestamp = today.getTime();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String tripKey = dataSnapshot.getKey();

                    boolean past = currentTimestamp > trip.getDate();

                    if (past){
                        ArrayList<String> tripMembers = trip.getMembers();

                        for (String member : tripMembers){
                            if (member.equals(userID)){
                                trips.add(trip);
                                tripKeys.add(tripKey);
                            }
                        }
                    }
                }
                if (trips.isEmpty()){
                    tvNoTrips.setVisibility(View.VISIBLE);
                } else {
                    tvNoTrips.setVisibility(View.GONE);
                }

                tripAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //add new trip floating action button
        fab.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "you clikced on fab button", Toast.LENGTH_LONG).show();
//                openNewTripDialog();
            startActivity(new Intent(getActivity(), NewTripActivity.class));
        });

//        TextView textView = (TextView) view.findViewById(R.id.tekstzazamjenu);
//        textView.setText("Promjenjen je tekstic");
        return view;




    }
}
