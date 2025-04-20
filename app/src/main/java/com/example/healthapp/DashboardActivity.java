package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class DashboardActivity extends AppCompatActivity {

    private TextView tvWelcome, tvAge, tvMedicalHistory, tvGender;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    Button btnUploadReport, btnViewReports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.tool);
        setSupportActionBar(toolbar);
        btnUploadReport = findViewById(R.id.btnUploadReport);
        btnViewReports = findViewById(R.id.btnViewReports);

        // Firebase initialization
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // UI elements
        tvWelcome = findViewById(R.id.tvWelcome);
        tvAge = findViewById(R.id.tvAge);
        tvMedicalHistory = findViewById(R.id.tvMedicalHistory);
        tvGender = findViewById(R.id.tvGender);

        // Load user details
        loadUserDetails();

        btnUploadReport.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, UploadReportActivity.class);
            startActivity(intent);
        });

        btnViewReports.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, ViewReportsActivity.class);
            startActivity(intent);
        });
    }

    private void loadUserDetails() {
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(userId);

            userRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    String age = documentSnapshot.getString("age");
                    String medicalHistory = documentSnapshot.getString("medicalHistory");
                    String gender = documentSnapshot.getString("gender");

                    tvWelcome.setText("Welcome, " + name + "!");
                    tvAge.setText("Age: " + age);
                    tvMedicalHistory.setText("Medical History: " + medicalHistory);
                    tvGender.setText("Gender: "+gender);
                }
            }).addOnFailureListener(e -> Toast.makeText(this, "Failed to load user data!", Toast.LENGTH_SHORT).show());
        }
    }

    // Inflate the menu (Profile button in top-right)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu); // Load the menu
        return true;
    }

    // Handle Profile button click
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}