package com.example.diluygulamas.model;

public class Level {
    private int id;
    private int levelNumber;
    private int languageId;
    private boolean isUnlocked;
    private boolean isCompleted;
    private float successRate;
    private int totalWords;
    private int correctAnswers;

    public Level() {
    }

    public Level(int id, int levelNumber, int languageId, boolean isUnlocked, boolean isCompleted, float successRate, int totalWords, int correctAnswers) {
        this.id = id;
        this.levelNumber = levelNumber;
        this.languageId = languageId;
        this.isUnlocked = isUnlocked;
        this.isCompleted = isCompleted;
        this.successRate = successRate;
        this.totalWords = totalWords;
        this.correctAnswers = correctAnswers;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(int levelNumber) {
        this.levelNumber = levelNumber;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public float getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(float successRate) {
        this.successRate = successRate;
    }

    public int getTotalWords() {
        return totalWords;
    }

    public void setTotalWords(int totalWords) {
        this.totalWords = totalWords;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }
}