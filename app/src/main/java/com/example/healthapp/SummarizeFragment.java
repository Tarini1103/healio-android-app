package com.example.healthapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import java.util.concurrent.Executors;

public class SummarizeFragment extends Fragment {
    private EditText reportInput;
    private TextView summaryView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summarize, container, false);


        reportInput = view.findViewById(R.id.reportInput);
        summaryView = view.findViewById(R.id.summaryView);
        Button summarizeButton = view.findViewById(R.id.summarizeButton);

        // Enable scrolling for summary
        summaryView.setMovementMethod(new ScrollingMovementMethod());

        // Set better visual appearance
        summaryView.setLineSpacing(1.1f, 1.2f);
        summaryView.setTextColor(ContextCompat.getColor(requireContext(), R.color.summary_text));
        summaryView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.summary_bg));
        Bundle args = getArguments();
        if (args != null) {
            String pdfText = args.getString("pdf_text");
            if (pdfText != null && !pdfText.trim().isEmpty()) {
                reportInput.setText(pdfText); // Fills the text
            }
        }

        summarizeButton.setOnClickListener(v -> {
            String report = reportInput.getText().toString().trim();
            if (!report.isEmpty()) {
                summarizeReport(report);
            } else {
                summaryView.setText("Please enter a medical report to summarize.");
            }
        });

        return view;
    }

    private void summarizeReport(String report) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Simulate processing delay
                Thread.sleep(800);

                String summary = generateEnhancedSummary(report);

                new Handler(Looper.getMainLooper()).post(() -> {
                    summaryView.setText(summary);
                });
            } catch (Exception e) {
                e.printStackTrace();
                new Handler(Looper.getMainLooper()).post(() -> {
                    summaryView.setText("❌ Error summarizing the report. Please try again.");
                });
            }
        });
    }

    private String generateEnhancedSummary(String report) {
        // Basic stats
        int lineCount = report.split("\n").length;
        int wordCount = report.split("\\s+").length;

        // Create formatted summary
        StringBuilder summary = new StringBuilder();
        summary.append("📋 Medical Report Summary\n\n");
        summary.append("📊 Original: ").append(lineCount).append(" lines, ").append(wordCount).append(" words\n\n");

        // Extract key sections with better formatting
        summary.append("🔍 Key Findings:\n");
        String[] keySections = {"diagnosis", "findings", "treatment", "recommendation", "impression", "results"};

        boolean foundSections = false;
        String lowerReport = report.toLowerCase();

        for (String section : keySections) {
            if (lowerReport.contains(section)) {
                foundSections = true;
                int start = lowerReport.indexOf(section);
                int end = Math.min(start + 200, report.length());
                String sectionContent = report.substring(start, end)
                        .replace("\n", " ")
                        .trim();

                summary.append("• ").append(capitalize(section)).append(": ")
                        .append(sectionContent.length() > 150 ?
                                sectionContent.substring(0, 150) + "..." :
                                sectionContent)
                        .append("\n\n");
            }
        }

        if (!foundSections) {
            summary.append("• General summary:\n");
            String[] sentences = report.split("\\. ");
            int sentencesToInclude = Math.min(5, sentences.length);
            for (int i = 0; i < sentencesToInclude; i++) {
                summary.append("  - ").append(sentences[i].trim()).append("\n");
            }
        }

        summary.append("\n⚠️ Note: This is an automated summary. Always consult your healthcare provider.");
        return summary.toString();
    }

    private String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}