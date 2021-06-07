package com.example.triporganizer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Helpers.Utils;
import com.example.triporganizer.R;
import com.example.triporganizer.Models.Trip;
import com.example.triporganizer.TripActivity;

import java.util.ArrayList;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    Context context;
    ArrayList<Trip> trips;
    ArrayList<String> tripKeys;

    public TripAdapter(Context context, ArrayList<Trip> trips, ArrayList<String> tripKeys) {
        this.context = context;
        this.trips = trips;
        this.tripKeys = tripKeys;
    }



    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.trip_row,parent,false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip trip = trips.get(position);
        String tripKey = tripKeys.get(position);

        String time = Utils.timestampToTime(trip.getDate());
        String date = Utils.timestampToDate(trip.getDate());

        holder.name.setText(trip.getName());
        holder.time.setText(time);
        holder.date.setText(date);
        holder.parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, tripKey, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, TripActivity.class);
                intent.putExtra("trip_id", tripKey);
                context.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }


    public static class TripViewHolder extends RecyclerView.ViewHolder{

        TextView name, time, date;
        CardView parent_layout;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.tvTripName);
            time = itemView.findViewById(R.id.tvTripTime);
            date = itemView.findViewById(R.id.tvTripDate);
            parent_layout = itemView.findViewById(R.id.card_view);

        }
    }
}
