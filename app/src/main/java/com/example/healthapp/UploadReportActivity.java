package com.example.healthapp;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.Manifest;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class UploadReportActivity extends AppCompatActivity {

    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri;
    private Button btnSelectFile, btnSaveFile;
    private TextView txtFileName;
    private ProgressDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_upload_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }

        btnSelectFile = findViewById(R.id.btnSelectFile);
        btnSaveFile = findViewById(R.id.btnUploadFile); // Renamed upload button
        txtFileName = findViewById(R.id.txtFileName);
        progressDialog = new ProgressDialog(this);

        btnSelectFile.setOnClickListener(v -> selectFile());
        btnSaveFile.setOnClickListener(v -> saveFileLocally());
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            txtFileName.setText(fileUri.getLastPathSegment());
        }
    }

    private void saveFileLocally() {
        if (fileUri != null) {
            progressDialog.setMessage("Saving...");
            progressDialog.show();

            try {
                InputStream inputStream = getContentResolver().openInputStream(fileUri);
                File storageDir = new File(getFilesDir(), "MedicalReports");
                if (!storageDir.exists()) {
                    storageDir.mkdirs();  // Create directory if it doesn't exist
                }

                String fileName = System.currentTimeMillis() + getFileExtension(fileUri);
                File savedFile = new File(storageDir, fileName);

                OutputStream outputStream = new FileOutputStream(savedFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.close();
                inputStream.close();

                progressDialog.dismiss();
                Toast.makeText(this, "File saved: " + savedFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

                // Start next activity and pass file path
                Intent intent = new Intent(UploadReportActivity.this, PdfSummaryActivity.class);
                intent.putExtra("pdf_path", savedFile.getAbsolutePath());
                startActivity(intent);
                finish();

            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(this, "Save Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();}

        } else {
            Toast.makeText(this, "Please select a file first", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        String extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(uri));
        return extension != null ? "." + extension : "";
    }
}