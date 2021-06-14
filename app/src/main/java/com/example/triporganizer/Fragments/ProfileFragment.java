package com.example.triporganizer.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.triporganizer.LoginActivity;
import com.example.triporganizer.Models.User;
import com.example.triporganizer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private User user;
    private EditText editTextPassword;

    public ProfileFragment(User user) {
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView usernameTV = view.findViewById(R.id.tv_profile_username2);
        TextView emailTV = view.findViewById(R.id.tv_profile_email2);
        Button deleteButton = view.findViewById(R.id.btn_profile_delete);
        editTextPassword = view.findViewById(R.id.et_profile_password);


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
            }
        });



        usernameTV.setText(user.username);
        emailTV.setText(user.email);



        return view;
    }

    private void deleteUser() {

        if (editTextPassword.getText().toString().matches("")) {
            editTextPassword.setError("Password is required to delete account!");
            editTextPassword.requestFocus();
            return;
        }

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        String password = editTextPassword.getText().toString();
        AuthCredential credential = EmailAuthProvider.getCredential(user.email, password);


        fbUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    // signOut current user
                    FirebaseAuth.getInstance().signOut();

                    // delete user from firebaseAuth
                    fbUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "User account deleted", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                    // delete user from database
                    FirebaseDatabase.getInstance().getReference("Users").child(user.userID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }
                    });




                } else {
                    Toast.makeText(getActivity(), "Couldn't delete account, try again", Toast.LENGTH_SHORT).show();
                    editTextPassword.setText("");
                    editTextPassword.requestFocus();

                }
            }
        });



    }
}
