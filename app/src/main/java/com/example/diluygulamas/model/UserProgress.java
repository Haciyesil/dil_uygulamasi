package com.example.diluygulamas.model;

public class UserProgress {
    private int languageId;
    private int currentLevel;
    private int totalCorrectAnswers;
    private int totalQuestions;

    public UserProgress() {
    }

    public UserProgress(int languageId, int currentLevel, int totalCorrectAnswers, int totalQuestions) {
        this.languageId = languageId;
        this.currentLevel = currentLevel;
        this.totalCorrectAnswers = totalCorrectAnswers;
        this.totalQuestions = totalQuestions;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }
}