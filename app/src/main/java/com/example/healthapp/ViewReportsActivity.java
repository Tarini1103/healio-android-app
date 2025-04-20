package com.example.healthapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
public class ViewReportsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> reportList;
    private File reportsDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_reports);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listViewReports);
        reportList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reportList);
        listView.setAdapter(adapter);

        reportsDir = new File(getFilesDir(), "MedicalReports");

        loadReports();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String fileName = reportList.get(position);
            File file = new File(reportsDir, fileName);

            if (fileName.endsWith(".pdf")) {
                Intent intent = new Intent(ViewReportsActivity.this, PdfSummaryActivity.class);
                intent.putExtra("pdf_path", file.getAbsolutePath());
                startActivity(intent);
            } else {
                openFile(file);
            }
        });
    }

    private void loadReports() {
        if (reportsDir.exists() && reportsDir.isDirectory()) {
            File[] files = reportsDir.listFiles();
            if (files != null) {
                reportList.clear();
                for (File file : files) {
                    if (file.getName().endsWith(".pdf")) {
                        reportList.add(file.getName());
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } else {
            Toast.makeText(this, "No reports found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openFile(File file) {
        Uri fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(fileUri, getMimeType(file.getAbsolutePath()));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No compatible viewer installed", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeType(String filePath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
