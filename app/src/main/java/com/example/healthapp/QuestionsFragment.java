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
import androidx.fragment.app.Fragment;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.Executors;

public class QuestionsFragment extends Fragment {
    private EditText questionInput;
    private TextView answerView;

    // Simple medical knowledge base
    private final String[][] MEDICAL_KB = {
            // General Symptoms
            {"headache", "Common causes include tension, migraines, dehydration, or eye strain. Try rest, hydration, OTC pain relievers, and a dark room. Seek help if: sudden/severe pain, after head injury, with fever/confusion, or if persistent."},
            {"fever", "Body's response to infection. Rest and fluids are key. For adults: seek help if >103°F (39.4°C), lasts >3 days, or with stiff neck/confusion. For children: >100.4°F (38°C) in infants or >104°F (40°C) in older kids."},
            {"cough", "Common with colds, flu, allergies, or infections. Stay hydrated, try honey (not for infants), or cough drops. Seek help if: lasts >2 weeks, produces blood, or with difficulty breathing/wheezing."},
            {"sore throat", "Often viral (colds/flu) or strep throat. Try warm liquids, gargling salt water, or lozenges. Seek help if: severe pain, difficulty swallowing, lasts >1 week, or with fever/rash."},

            // Digestive Issues
            {"stomach pain", "May indicate indigestion, gas, food intolerance, or infection. Try bland foods (BRAT diet), hydration, and rest. Seek help for: severe pain, vomiting blood, black stools, or pain lasting >2 days."},
            {"diarrhea", "Often caused by viruses, food poisoning, or medications. Stay hydrated with electrolyte solutions. Seek help if: lasts >2 days (adults) or >24h (children), with fever, or signs of dehydration."},
            {"constipation", "Insufficient fiber/water, or medication side effect. Increase fiber, water, and activity. Try OTC remedies if needed. Seek help if: lasts >2 weeks, severe pain, or blood in stool."},
            {"heartburn", "Stomach acid reflux. Avoid spicy/fatty foods, eat smaller meals, don't lie down after eating. Antacids may help. Seek help if: frequent (2+/week), difficulty swallowing, or weight loss."},

            // Respiratory
            {"cold", "Viral infection (rhinovirus) with runny nose, cough, sore throat. Rest, fluids, OTC meds can help. Usually lasts 7-10 days. Seek help if: symptoms worsen or last >10 days."},
            {"flu", "Influenza virus causes fever, body aches, fatigue, cough. Rest, fluids, antivirals if early. Seek help if: difficulty breathing, chest pain, or symptoms improve then worsen."},
            {"asthma", "Chronic airway inflammation causing wheezing/shortness of breath. Use prescribed inhalers. Seek emergency help for: severe difficulty breathing, blue lips, or inhaler not working."},
            {"allergies", "Immune response to allergens causing sneezing, itchy eyes/nose. Antihistamines may help. Seek help if: symptoms severe, OTC meds ineffective, or breathing difficulties."},

            // Chronic Conditions
            {"diabetes", "High blood sugar causes thirst, frequent urination, fatigue. Manage with diet, exercise, medication. Seek help for: very high/low blood sugar, confusion, or fruity-smelling breath."},
            {"high blood pressure", "Often no symptoms. Regular monitoring important. Manage with diet (low salt), exercise, medication. Seek help for: severe headache, chest pain, or vision changes."},

            // Infections
            {"urinary infection", "UTI symptoms: burning urination, frequent urge, cloudy urine. Increase water intake. Seek help for proper antibiotics, especially if fever or back pain (kidney infection)."},
            {"skin infection", "Redness, swelling, warmth, pus. Keep clean, apply antibiotic ointment. Seek help if: spreading rapidly, with fever, or large/deep infection."},
            {"ear infection", "Common in children - ear pain, fever, fussiness. Pain relievers may help. Seek help for proper diagnosis as antibiotics are sometimes needed."},

            // Other Common Conditions
            {"back pain", "Often muscle strain. Rest, gentle stretching, OTC pain relievers. Seek help if: severe, lasts >2 weeks, or with leg weakness/tingling."},
            {"arthritis", "Joint inflammation causing pain/stiffness. Manage with exercise, weight control, OTC meds. Seek help for: sudden severe pain, joint deformity, or limited mobility."},
            {"anxiety", "Excessive worry, restlessness, rapid heartbeat. Deep breathing, exercise may help. Seek professional help if interfering with daily life."},

            // Emergency Conditions (brief info)
            {"heart attack", "EMERGENCY: Chest pain/pressure, arm/jaw pain, nausea, sweating. Call emergency services immediately."},
            {"stroke", "EMERGENCY: FAST - Face drooping, Arm weakness, Speech difficulty, Time to call emergency."}
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_questions, container, false);

        questionInput = view.findViewById(R.id.questionInput);
        answerView = view.findViewById(R.id.answerView);
        Button askButton = view.findViewById(R.id.askButton);

        // Enable scrolling for answers
        answerView.setMovementMethod(new ScrollingMovementMethod());

        askButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = questionInput.getText().toString().trim().toLowerCase();
                if (!question.isEmpty()) {
                    String answer = getLocalMedicalAnswer(question);
                    if (answer == null) {
                        getMedicalAnswerFromAPI(question);
                    } else {
                        answerView.setText(answer);
                    }
                } else {
                    answerView.setText("Please enter a medical question.");
                }
            }
        });

        return view;
    }

    private String getLocalMedicalAnswer(String question) {
        // First check our local knowledge base
        for (String[] entry : MEDICAL_KB) {
            if (question.contains(entry[0])) {
                return "Based on your question about " + entry[0] + ":\n\n" + entry[1] +
                        "\n\nNote: This is general information only. Consult a doctor for medical advice.";
            }
        }
        return null;
    }

    void getMedicalAnswerFromAPI(String question) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                // Try a medical API first (simulated here - in production use a real medical API)
                if (question.contains("covid") || question.contains("coronavirus")) {
                    updateUI("COVID-19 is a respiratory illness. Symptoms include fever, cough, fatigue. " +
                            "Vaccination is recommended. Isolate if you test positive and consult a doctor.");
                    return;
                }

                // Fallback to Wikipedia with better processing
                String searchQuery = URLEncoder.encode(question, "UTF-8");
                String searchUrl = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=" +
                        searchQuery + "&format=json";

                URL url = new URL(searchUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000); // 10 second timeout
                conn.setReadTimeout(10000); // 10 second timeout

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray searchResults = json.getJSONObject("query").getJSONArray("search");

                if (searchResults.length() == 0) {
                    updateUI("I couldn't find medical information about that. Try asking about symptoms or conditions like headache, fever, etc.");
                    return;
                }

                String pageTitle = searchResults.getJSONObject(0).getString("title");
                String pageUrl = "https://en.wikipedia.org/api/rest_v1/page/summary/" +
                        URLEncoder.encode(pageTitle, "UTF-8");

                URL summaryUrl = new URL(pageUrl);
                HttpURLConnection summaryConn = (HttpURLConnection) summaryUrl.openConnection();
                summaryConn.setRequestMethod("GET");
                summaryConn.setConnectTimeout(10000);
                summaryConn.setReadTimeout(10000);

                BufferedReader summaryReader = new BufferedReader(new InputStreamReader(summaryConn.getInputStream()));
                StringBuilder summaryResponse = new StringBuilder();
                while ((line = summaryReader.readLine()) != null) {
                    summaryResponse.append(line);
                }
                summaryReader.close();

                JSONObject pageJson = new JSONObject(summaryResponse.toString());
                String extract = pageJson.optString("extract", "No relevant medical information found.");

                // Process the Wikipedia response to be more medical-question friendly
                extract = processMedicalExtract(extract, question);
                displayAnswer(extract, pageTitle);

            } catch (Exception e) {
                e.printStackTrace();
                updateUI("I'm having trouble answering that. Try asking about common symptoms or conditions.\n\nError: " + e.getMessage());
            }
        });
    }

    private String processMedicalExtract(String extract, String question) {
        // Clean up the Wikipedia response
        extract = extract.replaceAll("\\([^)]*\\)", "") // Remove parentheses content
                .replaceAll("\\[[^]]*\\]", "")  // Remove brackets
                .replaceAll("\\s+", " ")        // Collapse whitespace
                .trim();

        // Focus on relevant parts based on question
        String[] keywords = {"symptom", "cause", "treatment", "diagnos", "prevent"};
        String[] medicalSections = new String[keywords.length];

        for (int i = 0; i < keywords.length; i++) {
            if (question.contains(keywords[i])) {
                medicalSections[i] = extractSection(extract, keywords[i]);
            }
        }

        // Build prioritized response
        StringBuilder result = new StringBuilder();
        for (String section : medicalSections) {
            if (section != null && !section.isEmpty()) {
                result.append(section).append("\n\n");
            }
        }

        return result.length() > 0 ? result.toString() :
                "Here's what I found:\n\n" + extract.substring(0, Math.min(extract.length(), 500)) +
                        (extract.length() > 500 ? "..." : "");
    }

    private String extractSection(String text, String keyword) {
        String[] sentences = text.split("\\. ");
        StringBuilder section = new StringBuilder();
        for (String sentence : sentences) {
            if (sentence.toLowerCase().contains(keyword)) {
                section.append("- ").append(sentence.trim()).append(".\n");
            }
        }
        return section.toString();
    }

    private void displayAnswer(String answer, String source) {
        String disclaimer = "\n\nDisclaimer: This information is for general knowledge only " +
                "and not medical advice. Always consult a healthcare professional.";

        String formatted = answer + "\n\n(Source: " + source + ")" + disclaimer;
        updateUI(formatted);
    }

    void updateUI(String text) {
        new Handler(Looper.getMainLooper()).post(() -> answerView.setText(text));
    }
}