package com.example.provajava;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class Login extends AppCompatActivity {
    EditText emailText, passText;
    Button logInBtn;
    TextView goToRegister, forgot;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // selecting the inputs
        emailText = findViewById(R.id.email);
        passText = findViewById(R.id.pass);
        logInBtn = findViewById(R.id.login);
        goToRegister = findViewById(R.id.goToRegister);
        forgot = findViewById(R.id.forgot);

        // firebase initialization
        mAuth = FirebaseAuth.getInstance();

        // go to sign up
        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Signup.class));
                finish();
            }
        });

        // check if logged in
        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // login
        logInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailText.getText().toString().trim();
                String pass = passText.getText().toString().trim();

                // fields validation
                if(TextUtils.isEmpty(email)){
                    emailText.setError("Email is required");
                    return;
                }

                if(TextUtils.isEmpty(pass)){
                    passText.setError("Password is required");
                    return;
                }

                // login to firebase
                mAuth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d("user", user.toString());

                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        // password recovery
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder passwordReset = new AlertDialog.Builder(Login.this);
                final EditText resetEmail = new EditText(v.getContext());

                passwordReset.setTitle("Password recovery");
                passwordReset.setMessage("Enter your email to receive a recovering email:");
                passwordReset.setView(resetEmail);

                passwordReset.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // sends recovery message
                        String email = resetEmail.getText().toString();
                        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Login.this, "A reset link has been sent to your email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Login.this, "There was an error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordReset.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                passwordReset.create().show();
            }
        });
    }
}