package com.example.provajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    EditText userText, emailText, passText, phoneText;
    Button signUpBtn;
    TextView goToLogin;
    String userID;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // selecting the inputs
        userText = findViewById(R.id.user);
        emailText = findViewById(R.id.re_email);
        passText = findViewById(R.id.re_pass);
        phoneText = findViewById(R.id.re_phone);
        signUpBtn = findViewById(R.id.signup);
        goToLogin = findViewById(R.id.goToLogin);

        // firebase initialization
        mAuth = FirebaseAuth.getInstance(); // authentication
        db = FirebaseFirestore.getInstance(); // database Cloud Firestore

        // checks if user is logged
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // go to login
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });

        // validation and registration
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String pass = passText.getText().toString().trim();
                String username = userText.getText().toString().trim();
                String phone = phoneText.getText().toString().trim();

                // fields validation
                if(TextUtils.isEmpty(email)){
                    emailText.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    passText.setError("Password is required");
                    return;
                }

                if(pass.length() < 6){
                    passText.setError("Password must be at least 6 characters long");
                    return;
                }

                // registration
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // email verification
                            mAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Signup.this, "An verification email has been sent", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Signup.this, "There was an error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            // user data storage
                            userID = mAuth.getCurrentUser().getUid();

                            Map<String, Object> user = new HashMap<>();
                            user.put("user", username);
                            user.put("phone", phone);
                            user.put("email", email);

                            db.collection("users").document(userID).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d("firebase", "User data sent to server. User ID = " + userID);
                                }
                            });

                            // log message
                            Log.d("firebase", "User created successfully");

                            // change to logged in
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Signup.this, "There was an error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}