package com.example.healthapp;

public class User {
    private String userId;
    private String name;
    private int age;
    private String gender;
    private String medicalHistory;

    // Default constructor (required for Firestore)
    public User() {}

    public User(String userId, String name, int age, String gender, String medicalHistory) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = medicalHistory;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMedicalHistory() { return medicalHistory; }
    public void setMedicalHistory(String medicalHistory) { this.medicalHistory = medicalHistory; }
}
