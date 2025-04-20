package com.example.healthapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;
import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;

public class PdfSummaryActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pdf_summary);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        PDFBoxResourceLoader.init(getApplicationContext()); // Very important

        textView = findViewById(R.id.textViewPdfContent);

        String pdfPath = getIntent().getStringExtra("pdf_path");
        if (pdfPath != null) {
            extractTextFromPdf(pdfPath);
        } else {
            textView.setText("No PDF file path provided.");
        }
    }

    private void extractTextFromPdf(String filePath) {
        try {
            File file = new File(filePath);
            PDDocument document = PDDocument.load(file);
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document);
            document.close();

            Intent intent = new Intent(PdfSummaryActivity.this, MainActivity2.class);
            intent.putExtra("pdf_text", text);
            startActivity(intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "PDF Read Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            textView.setText("Failed to extract text from PDF.");
        }
    }

}