package com.example.healthapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.text.TextUtils;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {


    private EditText etName, etAge, etMedicalHistory;
    private Spinner spinnerGender;
    private Button btnSaveProfile;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Reference UI elements
        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etMedicalHistory = findViewById(R.id.etMedicalHistory);
        spinnerGender = findViewById(R.id.spinnerGender);
        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        // Load existing data (if any)
        loadUserProfile();

        // Save button click listener
        btnSaveProfile.setOnClickListener(v -> saveUserProfile());
    }

    private void saveUserProfile() {
        String name = etName.getText().toString().trim();
        String age = etAge.getText().toString().trim();
        String gender = spinnerGender.getSelectedItem().toString();
        String medicalHistory = etMedicalHistory.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(age)) {
            Toast.makeText(this, "Name and age are required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            Map<String, Object> userProfile = new HashMap<>();
            userProfile.put("name", name);
            userProfile.put("age", age);
            userProfile.put("gender", gender);
            userProfile.put("medicalHistory", medicalHistory);

            userRef.set(userProfile)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Profile saved!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                        finish();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error saving profile", Toast.LENGTH_SHORT).show());
            Intent intent = new Intent(ProfileActivity.this, DashboardActivity.class);
            startActivity(intent);
        }
    }

    private void loadUserProfile() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    etName.setText(documentSnapshot.getString("name"));
                    etAge.setText(documentSnapshot.getString("age"));
                    etMedicalHistory.setText(documentSnapshot.getString("medicalHistory"));

                    String gender = documentSnapshot.getString("gender");
                    if (gender != null) {
                        switch (gender) {
                            case "Male":
                                spinnerGender.setSelection(0);
                                break;
                            case "Female":
                                spinnerGender.setSelection(1);
                                break;
                            case "Other":
                                spinnerGender.setSelection(2);
                                break;
                        }
                    }
                }
            });
        }
    }
}