package com.example.triporganizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTextEmail, editTextPassword;
    private Button signIn, register;
    private ProgressBar progressBarLogin;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittextLoginEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextLoginPassword);

        signIn = (Button) findViewById(R.id.btnLogin);
        register = (Button) findViewById(R.id.btnRegister);
        progressBarLogin = (ProgressBar) findViewById(R.id.progressBarLogin);

        signIn.setOnClickListener(this);
        register.setOnClickListener(this);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        checkUserLogin(user);



    }

    // if user has logged in before, start MainActivity
    private void checkUserLogin(FirebaseUser user) {
        if(user != null){
//            Log.d("CREATION", "User je ulogiran");
//            Toast.makeText(LoginActivity.this, "Logiran si", Toast.LENGTH_LONG).show();
            finish();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
        } else {
//            Log.d("CREATION", "User nije ulogiran!!!");
            Toast.makeText(LoginActivity.this, "Nije logiran user", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRegister:
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.btnLogin:
                Toast.makeText(LoginActivity.this, "Kliknuo si login", Toast.LENGTH_LONG).show();
                userLogIn();

                break;
        }
    }

    // User Log in
    private void userLogIn() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(email.isEmpty()){
            editTextEmail.setError("Email is requried!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is requried!");
            editTextPassword.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please enter a valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Minimal password length is 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        progressBarLogin.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressBarLogin.setVisibility(View.GONE);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    progressBarLogin.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Failed to login, try again!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
