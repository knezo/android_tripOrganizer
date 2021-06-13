package com.example.triporganizer.Helpers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.triporganizer.Models.User;
import com.example.triporganizer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class AddUserToTripDialog extends AppCompatDialogFragment {
    private AddUserToTripDialogListener listener;
    Context contextRecived;

    ArrayList<String> usernames;


    public AddUserToTripDialog(Context contextRecived) {
        this.contextRecived = contextRecived;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add new user to trip");


        final AutoCompleteTextView usernameToAdd = new AutoCompleteTextView(contextRecived);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contextRecived, android.R.layout.simple_list_item_1, usernames);
        usernameToAdd.setAdapter(adapter);
//        usernameToAdd.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(usernameToAdd);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onAddClicked(String.valueOf(usernameToAdd.getText()));
                    }
                });
        return builder.create();
    }

    public interface AddUserToTripDialogListener {
        void onAddClicked(String username);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        contextRecived = context;

        usernames = new ArrayList<>();
        getUsernamesFromDatabase();

        try {
            listener = (AddUserToTripDialogListener) context;
        } catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement DeleteTripDialogListener");
        }
    }


    private void getUsernamesFromDatabase() {
        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    String username = user.username;
                    usernames.add(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
