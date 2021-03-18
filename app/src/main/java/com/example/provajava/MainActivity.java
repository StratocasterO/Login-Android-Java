package com.example.provajava;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    TextView text, logout, email, phone;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // inputs
        text = findViewById(R.id.main);
        logout = findViewById(R.id.logout);
        email = findViewById(R.id.profileEmail);
        phone = findViewById(R.id.profilePhone);

        // firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userID = mAuth.getCurrentUser().getUid();

        db.collection("users").document(userID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                email.setText(documentSnapshot.getString("email"));
                phone.setText(documentSnapshot.getString("phone"));
                text.setText("Hello " + documentSnapshot.getString("user") + ", this is your profile!");
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}