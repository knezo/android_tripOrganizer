package com.example.triporganizer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.triporganizer.Adapters.NotificationAdapter;
import com.example.triporganizer.LoginActivity;
import com.example.triporganizer.Models.Notification;
import com.example.triporganizer.Models.User;
import com.example.triporganizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class NotificationFragment extends Fragment {

    private String userID;
    RecyclerView rvNotifications;
    ArrayList<Notification> notifications;
    private NotificationAdapter notificationAdapter;
    DatabaseReference notficationReference;

    ArrayList<String> notificationsIDforDelete;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        ProgressBar pbNotifications = view.findViewById(R.id.pbNotifications);
        TextView tvNoNotifications = view.findViewById(R.id.tv_noNotifications);
        Button btnClearNotifications = view.findViewById(R.id.btn_clearnotifications);

        notifications = new ArrayList<>();

        // get userID
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        notficationReference = FirebaseDatabase.getInstance().getReference("Notifications");


        rvNotifications = view.findViewById(R.id.recycleviewNotifications);
        rvNotifications.setHasFixedSize(true);
        rvNotifications.setLayoutManager(new LinearLayoutManager(getActivity()));

        notificationAdapter = new NotificationAdapter(getActivity(), notifications);
        rvNotifications.setAdapter(notificationAdapter);


        // get Notifications from database
        notficationReference
                .orderByChild("forUserID")
                .equalTo(userID)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pbNotifications.setVisibility(View.GONE);
                notifications.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Notification notification = dataSnapshot.getValue(Notification.class);

                    notifications.add(0, notification);

                }


                if (notifications.isEmpty()){
                    tvNoNotifications.setVisibility(View.VISIBLE);
                    btnClearNotifications.setVisibility(View.GONE);
                } else {
                    tvNoNotifications.setVisibility(View.GONE);
                    btnClearNotifications.setVisibility(View.VISIBLE);
                }

                notificationAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //clear notifications button
        btnClearNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearNotifiations();
            }
        });

        return view;
    }


    // click on clear notification button
    private void clearNotifiations() {

        notificationsIDforDelete = new ArrayList<>();

        notficationReference.orderByChild("forUserID").equalTo(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Notification n = dataSnapshot.getValue(Notification.class);
                    notificationsIDforDelete.add(n.getNotificationID());
                }

                deleteNotificationsFromDatabase();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void deleteNotificationsFromDatabase() {

        for (String notificationID : notificationsIDforDelete){
            FirebaseDatabase.getInstance().getReference("Notifications")
                    .child(notificationID)
                    .removeValue();
        }

    }


}
