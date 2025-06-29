package com.example.diluygulamas.model;

public class Word {
    private int id;
    private String originalWord;
    private String translatedWord;
    private String[] options;
    private int languageId;
    private int level;
    private String category; // Kategori alanı eklendi

    public Word() {
    }

    public Word(int id, String originalWord, String translatedWord, String[] options, int languageId, int level) {
        this.id = id;
        this.originalWord = originalWord;
        this.translatedWord = translatedWord;
        this.options = options;
        this.languageId = languageId;
        this.level = level;
    }

    public Word(int id, String originalWord, String translatedWord, String[] options, int languageId, int level, String category) {
        this.id = id;
        this.originalWord = originalWord;
        this.translatedWord = translatedWord;
        this.options = options;
        this.languageId = languageId;
        this.level = level;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginalWord() {
        return originalWord;
    }

    public void setOriginalWord(String originalWord) {
        this.originalWord = originalWord;
    }

    public String getTranslatedWord() {
        return translatedWord;
    }

    public void setTranslatedWord(String translatedWord) {
        this.translatedWord = translatedWord;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getLanguageId() {
        return languageId;
    }

    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // Kategori için getter ve setter metodları eklendi
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}