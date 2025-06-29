package com.example.diluygulamas.model;

public class Language {
    private int id;
    private String name;
    private String code;
    private boolean isUnlocked;
    private String flagName; // Bayrak için drawable ismi eklendi

    public Language() {
    }

    public Language(int id, String name, String code, boolean isUnlocked) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.isUnlocked = isUnlocked;
    }

    public Language(int id, String name, String code, boolean isUnlocked, String flagName) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.isUnlocked = isUnlocked;
        this.flagName = flagName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public String getFlagName() {
        return flagName;
    }

    public void setFlagName(String flagName) {
        this.flagName = flagName;
    }
}