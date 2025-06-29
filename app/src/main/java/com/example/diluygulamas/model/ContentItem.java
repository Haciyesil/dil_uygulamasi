package com.example.diluygulamas.model;
import android.graphics.Color;

public class ContentItem {
    private String originalText;
    private String translatedText;
    private String pronunciation;
    private String imageUrl;
    private String months; // Mevsim ayları bilgisi için eklendi
    private int red = 0;
    private int green = 0;
    private int blue = 0;
    private int languageId = 1; // Varsayılan olarak Türkmence (1)
    private int level = 1; // Varsayılan olarak seviye 1

    public ContentItem() {}

    public ContentItem(String originalText, String translatedText, String pronunciation, String imageUrl) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.pronunciation = pronunciation;
        this.imageUrl = imageUrl;
    }

    // Dil ve seviye bilgisi içeren constructor
    public ContentItem(String originalText, String translatedText, String pronunciation, String imageUrl, int languageId, int level) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.pronunciation = pronunciation;
        this.imageUrl = imageUrl;
        this.languageId = languageId;
        this.level = level;
    }

    // Mevsimler için constructor
    public ContentItem(String originalText, String translatedText, String pronunciation, String months, String imageUrl) {
        this.originalText = originalText;
        this.translatedText = translatedText;
        this.pronunciation = pronunciation;
        this.months = months;
        this.imageUrl = imageUrl;
    }

    // Getter metodları
    public String getOriginalText() {
        return originalText;
    }

    public String getTranslatedText() {
        return translatedText;
    }

    public String getPronunciation() {
        return pronunciation;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    // months getter'ı eklendi
    public String getMonths() {
        return months;
    }

    // RGB getter metodları
    public int getRed() {
        return red;
    }

    public int getGreen() {
        return green;
    }

    public int getBlue() {
        return blue;
    }

    // RGB değerlerinden renk oluşturan yardımcı metod
    public int getColor() {
        return Color.rgb(red, green, blue);
    }

    // Dil ve seviye getter metodları
    public int getLanguageId() {
        return languageId;
    }

    public int getLevel() {
        return level;
    }

    // Setter metodları
    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }

    public void setPronunciation(String pronunciation) {
        this.pronunciation = pronunciation;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // months setter'ı eklendi
    public void setMonths(String months) {
        this.months = months;
    }

    public void setRed(int red) {
        this.red = red;
    }

    public void setGreen(int green) {
        this.green = green;
    }

    public void setBlue(int blue) {
        this.blue = blue;
    }

    // Dil ve seviye setter metodları
    public void setLanguageId(int languageId) {
        this.languageId = languageId;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    // Pratik bir şekilde renkle ilişkilendirmek için helper metod
    public void setDefaultColor(String colorName) {
        // Türkmen dili renkleri
        switch (colorName.toLowerCase()) {
            case "gyzyl": // Kırmızı
                setRed(255); setGreen(0); setBlue(0);
                break;
            case "gök": // Mavi
                setRed(0); setGreen(0); setBlue(255);
                break;
            case "ýaşyl": // Yeşil
                setRed(0); setGreen(255); setBlue(0);
                break;
            case "sary": // Sarı
                setRed(255); setGreen(255); setBlue(0);
                break;
            case "ak": // Beyaz
                setRed(255); setGreen(255); setBlue(255);
                break;
            case "gara": // Siyah
                setRed(0); setGreen(0); setBlue(0);
                break;
            case "mele": // Kahverengi
                setRed(165); setGreen(42); setBlue(42);
                break;
            // Arapça renk adları
            case "أحمر": // Kırmızı
                setRed(255); setGreen(0); setBlue(0);
                break;
            case "أزرق": // Mavi
                setRed(0); setGreen(0); setBlue(255);
                break;
            case "أخضر": // Yeşil
                setRed(0); setGreen(128); setBlue(0);
                break;
            case "أصفر": // Sarı
                setRed(255); setGreen(255); setBlue(0);
                break;
            case "أبيض": // Beyaz
                setRed(255); setGreen(255); setBlue(255);
                break;
            case "أسود": // Siyah
                setRed(0); setGreen(0); setBlue(0);
                break;
            // Türkçe renk adları
            case "kırmızı":
                setRed(255); setGreen(0); setBlue(0);
                break;
            case "mavi":
                setRed(0); setGreen(0); setBlue(255);
                break;
            case "yeşil":
                setRed(0); setGreen(128); setBlue(0);
                break;
            case "sarı":
                setRed(255); setGreen(255); setBlue(0);
                break;
            case "beyaz":
                setRed(255); setGreen(255); setBlue(255);
                break;
            case "siyah":
                setRed(0); setGreen(0); setBlue(0);
                break;
            default:
                setRed(128); setGreen(128); setBlue(128); // Gri
                break;
        }
    }

    // Çevirilen renge göre renk değerlerini belirleyen metod
    public void setColorByTranslation() {
        if (translatedText != null) {
            switch (translatedText.toLowerCase()) {
                case "kırmızı":
                    setRed(255); setGreen(0); setBlue(0);
                    break;
                case "mavi":
                    setRed(0); setGreen(0); setBlue(255);
                    break;
                case "yeşil":
                    setRed(0); setGreen(128); setBlue(0);
                    break;
                case "sarı":
                    setRed(255); setGreen(255); setBlue(0);
                    break;
                case "beyaz":
                    setRed(255); setGreen(255); setBlue(255);
                    break;
                case "siyah":
                    setRed(0); setGreen(0); setBlue(0);
                    break;
                default:
                    // Çeviri bulunamadıysa varsayılan renk
                    setRed(128); setGreen(128); setBlue(128);
                    break;
            }
        }
    }
}