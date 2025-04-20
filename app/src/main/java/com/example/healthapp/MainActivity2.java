package com.example.healthapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set up ViewPager with fragments
        ViewPager viewPager = findViewById(R.id.viewPager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // ✅ Get the text sent from ReportSummaryActivity
        String passedText = getIntent().getStringExtra("pdf_text");

        // ✅ Create bundle to pass to SummarizeFragment
        Bundle bundle = new Bundle();
        bundle.putString("pdf_text", passedText);

        // ✅ Set arguments to fragment
        SummarizeFragment summarizeFragment = new SummarizeFragment();
        summarizeFragment.setArguments(bundle);

        adapter.addFragment(summarizeFragment, "Summarize Reports");
        adapter.addFragment(new QuestionsFragment(), "Ask Questions");
        
        viewPager.setAdapter(adapter);
    }
}