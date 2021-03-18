package com.example.provajava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
    EditText emailText, passText;
    Button logInBtn;
    TextView goToRegister;
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
    }
}