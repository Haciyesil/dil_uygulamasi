package com.example.diluygulamas.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class PreferenceManager {
    private static final String TAG = "PreferenceManager";
    private static final String PREF_NAME = "LanguageAppPrefs";
    private static final String KEY_CURRENT_LANGUAGE = "current_language";
    private static final String KEY_CURRENT_LEVEL = "current_level_";
    private static final String KEY_LEVEL_UNLOCKED = "level_unlocked_";
    private static final String KEY_CORRECT_ANSWERS = "correct_answers_";
    private static final String KEY_SUCCESS_RATE = "success_rate_";
    private static final String KEY_LEVEL_COMPLETED = "level_completed_";
    private static final String KEY_TOTAL_WORDS = "total_words_";

    private SharedPreferences sharedPreferences;
    private Context context;

    public PreferenceManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setCurrentLanguage(int languageId) {
        try {
            sharedPreferences.edit().putInt(KEY_CURRENT_LANGUAGE, languageId).apply();
            Log.d(TAG, "Geçerli dil ayarlandı: " + languageId);
        } catch (Exception e) {
            Log.e(TAG, "setCurrentLanguage hatası: " + e.getMessage(), e);
        }
    }

    public int getCurrentLanguage() {
        try {
            int currentLanguage = sharedPreferences.getInt(KEY_CURRENT_LANGUAGE, 1);
            Log.d(TAG, "Geçerli dil alındı: " + currentLanguage);
            return currentLanguage;
        } catch (Exception e) {
            Log.e(TAG, "getCurrentLanguage hatası: " + e.getMessage(), e);
            return 1; // Hata durumunda varsayılan dil
        }
    }

    public void setCurrentLevel(int languageId, int level) {
        try {
            sharedPreferences.edit().putInt(KEY_CURRENT_LEVEL + languageId, level).apply();
            Log.d(TAG, "Geçerli seviye ayarlandı: Dil " + languageId + ", Seviye " + level);
        } catch (Exception e) {
            Log.e(TAG, "setCurrentLevel hatası: " + e.getMessage(), e);
        }
    }

    public int getCurrentLevel(int languageId) {
        try {
            int currentLevel = sharedPreferences.getInt(KEY_CURRENT_LEVEL + languageId, 1);
            Log.d(TAG, "Geçerli seviye alındı: Dil " + languageId + ", Seviye " + currentLevel);
            return currentLevel;
        } catch (Exception e) {
            Log.e(TAG, "getCurrentLevel hatası: " + e.getMessage(), e);
            return 1; // Hata durumunda ilk seviye
        }
    }

    public void setLevelUnlocked(int languageId, int level, boolean unlocked) {
        try {
            sharedPreferences.edit().putBoolean(KEY_LEVEL_UNLOCKED + languageId + "_" + level, unlocked).apply();
            Log.d(TAG, "Seviye kilit durumu ayarlandı: Dil " + languageId + ", Seviye " + level + ", Kilit Açık " + unlocked);
        } catch (Exception e) {
            Log.e(TAG, "setLevelUnlocked hatası: " + e.getMessage(), e);
        }
    }

    public boolean isLevelUnlocked(int languageId, int level) {
        try {
            if (level == 1) {
                // İlk seviye her zaman açık
                return true;
            }
            boolean isUnlocked = sharedPreferences.getBoolean(KEY_LEVEL_UNLOCKED + languageId + "_" + level, false);
            Log.d(TAG, "Seviye kilit durumu alındı: Dil " + languageId + ", Seviye " + level + ", Kilit Açık " + isUnlocked);
            return isUnlocked;
        } catch (Exception e) {
            Log.e(TAG, "isLevelUnlocked hatası: " + e.getMessage(), e);
            return level == 1; // Hata durumunda sadece ilk seviye açık
        }
    }
    // PreferenceManager.java sınıfına ekleyin
    public void setTotalWords(int languageId, int level, int totalWords) {
        try {
            sharedPreferences.edit().putInt(KEY_TOTAL_WORDS + languageId + "_" + level, totalWords).apply();
            Log.d(TAG, "Toplam kelime sayısı ayarlandı: Dil " + languageId + ", Seviye " + level + ", Toplam " + totalWords);
        } catch (Exception e) {
            Log.e(TAG, "setTotalWords hatası: " + e.getMessage(), e);
        }
    }


    public void saveQuizResults(int languageId, int level, int correctAnswers, int totalWords, float successRate) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            // Doğru cevap sayısını kaydet
            editor.putInt(KEY_CORRECT_ANSWERS + languageId + "_" + level, correctAnswers);
            // Toplam kelime sayısını kaydet
            editor.putInt(KEY_TOTAL_WORDS + languageId + "_" + level, totalWords);
            // Başarı oranını kaydet
            editor.putFloat(KEY_SUCCESS_RATE + languageId + "_" + level, successRate);

            // ÖNEMLİ DEĞİŞİKLİK: Seviye tamamlanma mantığını buradan kaldırıyoruz
            // Bu işlem artık QuizActivity'de daha kapsamlı bir şekilde yapılacak
            // Seviyenin tamamlandığını işaretleme kararı QuizActivity'de alınacak

            editor.apply();
            Log.d(TAG, "Quiz sonuçları kaydedildi: Dil " + languageId +
                    ", Seviye " + level +
                    ", Doğru " + correctAnswers +
                    "/" + totalWords +
                    ", Başarı %"+ successRate);
        } catch (Exception e) {
            Log.e(TAG, "saveQuizResults hatası: " + e.getMessage(), e);
        }
    }

    public void unlockNextLevel(int languageId, int currentLevel) {
        try {
            // Bir sonraki seviyeyi aç
            int nextLevel = currentLevel + 1;
            setLevelUnlocked(languageId, nextLevel, true);
            Log.d(TAG, "Sonraki seviye açıldı: Dil " + languageId + ", Seviye " + nextLevel);
        } catch (Exception e) {
            Log.e(TAG, "unlockNextLevel hatası: " + e.getMessage(), e);
        }
    }

    public int getCorrectAnswers(int languageId, int level) {
        try {
            int correctAnswers = sharedPreferences.getInt(KEY_CORRECT_ANSWERS + languageId + "_" + level, 0);
            Log.d(TAG, "Doğru cevap sayısı alındı: Dil " + languageId + ", Seviye " + level + ", Doğru " + correctAnswers);
            return correctAnswers;
        } catch (Exception e) {
            Log.e(TAG, "getCorrectAnswers hatası: " + e.getMessage(), e);
            return 0;
        }
    }

    public int getTotalWords(int languageId, int level) {
        try {
            int totalWords = sharedPreferences.getInt(KEY_TOTAL_WORDS + languageId + "_" + level, 25); // Varsayılan olarak 25
            Log.d(TAG, "Toplam kelime sayısı alındı: Dil " + languageId + ", Seviye " + level + ", Toplam " + totalWords);
            return totalWords;
        } catch (Exception e) {
            Log.e(TAG, "getTotalWords hatası: " + e.getMessage(), e);
            return 25; // Varsayılan değer
        }
    }

    public float getSuccessRate(int languageId, int level) {
        try {
            float successRate = sharedPreferences.getFloat(KEY_SUCCESS_RATE + languageId + "_" + level, 0);
            Log.d(TAG, "Başarı oranı alındı: Dil " + languageId + ", Seviye " + level + ", Oran %" + successRate);
            return successRate;
        } catch (Exception e) {
            Log.e(TAG, "getSuccessRate hatası: " + e.getMessage(), e);
            return 0;
        }
    }

    public boolean isLevelCompleted(int languageId, int level) {
        try {
            boolean isCompleted = sharedPreferences.getBoolean(KEY_LEVEL_COMPLETED + languageId + "_" + level, false);
            Log.d(TAG, "Seviye tamamlanma durumu alındı: Dil " + languageId + ", Seviye " + level + ", Tamamlandı " + isCompleted);
            return isCompleted;
        } catch (Exception e) {
            Log.e(TAG, "isLevelCompleted hatası: " + e.getMessage(), e);
            return false;
        }
    }

    public void setCorrectAnswers(int languageId, int level, int correctAnswers) {
        try {
            sharedPreferences.edit().putInt(KEY_CORRECT_ANSWERS + languageId + "_" + level, correctAnswers).apply();
            Log.d(TAG, "Doğru cevap sayısı ayarlandı: Dil " + languageId + ", Seviye " + level + ", Doğru " + correctAnswers);
        } catch (Exception e) {
            Log.e(TAG, "setCorrectAnswers hatası: " + e.getMessage(), e);
        }
    }

    public void setSuccessRate(int languageId, int level, float successRate) {
        try {
            sharedPreferences.edit().putFloat(KEY_SUCCESS_RATE + languageId + "_" + level, successRate).apply();
            Log.d(TAG, "Başarı oranı ayarlandı: Dil " + languageId + ", Seviye " + level + ", Oran %" + successRate);
        } catch (Exception e) {
            Log.e(TAG, "setSuccessRate hatası: " + e.getMessage(), e);
        }
    }

    public void setLevelCompleted(int languageId, int level, boolean completed) {
        try {
            sharedPreferences.edit().putBoolean(KEY_LEVEL_COMPLETED + languageId + "_" + level, completed).apply();
            Log.d(TAG, "Seviye tamamlanma durumu ayarlandı: Dil " + languageId + ", Seviye " + level + ", Tamamlandı " + completed);
        } catch (Exception e) {
            Log.e(TAG, "setLevelCompleted hatası: " + e.getMessage(), e);
        }
    }

    // Tüm dil verilerini sıfırla
    public void resetLanguageData(int languageId) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // SharedPreferences'daki tüm anahtarları al
            for (String key : sharedPreferences.getAll().keySet()) {
                // Bu dil ID'sine ait tüm anahtarları sil
                if (key.contains("_" + languageId + "_")) {
                    editor.remove(key);
                }
            }

            // İlk seviyeyi tekrar aç
            editor.putBoolean(KEY_LEVEL_UNLOCKED + languageId + "_1", true);

            editor.apply();
            Log.d(TAG, "Dil verileri sıfırlandı: Dil " + languageId);
        } catch (Exception e) {
            Log.e(TAG, "resetLanguageData hatası: " + e.getMessage(), e);
        }
    }

    // Tüm uygulama verilerini sıfırla
    public void resetAllData() {
        try {
            sharedPreferences.edit().clear().apply();
            Log.d(TAG, "Tüm uygulama verileri sıfırlandı");
        } catch (Exception e) {
            Log.e(TAG, "resetAllData hatası: " + e.getMessage(), e);
        }
    }
}