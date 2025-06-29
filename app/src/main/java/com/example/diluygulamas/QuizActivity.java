package com.example.diluygulamas;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import java.util.Arrays;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.diluygulamas.model.Word;
import com.example.diluygulamas.util.JsonHelper;
import com.example.diluygulamas.util.PreferenceManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "QuizActivity";
    private TextView progressTextView;
    private ProgressBar progressBar;
    private TextView promptTextView;
    private TextView wordTextView;
    private ImageButton speakButton;
    // Değişken tiplerini TextView olarak değiştirdik
    private TextView option1Button, option2Button, option3Button, option4Button;
    private TextView[] optionButtons;
    private Button previousButton, nextButton;
    private CardView option1Card, option2Card, option3Card, option4Card;
    private CardView[] optionCards;
    private List<Word> words;
    private Word currentWord;
    private int currentIndex = 0;
    private int correctAnswers = 0;
    private int languageId;
    private int level;
    private PreferenceManager preferenceManager;
    private JsonHelper jsonHelper;
    private MediaPlayer mediaPlayer;
    // Soruların cevaplandırılıp cevaplanmadığını takip etmek için dizi
    private boolean[] questionAnsweredState;
    // Her soru için seçilen butonun indeksini saklamak için dizi
    private int[] selectedButtonIndices;
    // Her soru için doğru cevabın buton indeksini saklamak için dizi
    private int[] correctButtonIndices;
    // Her soru için seçenekleri saklamak için dizi
    private String[][] allOptions;
    // Renkler
    private int primaryColor = Color.parseColor("#673AB7"); // Mor
    private int lightColor = Color.parseColor("#D3CCDF");   // Daha koyu açık mor
    private int correctColor = Color.parseColor("#4CAF50"); // Yeşil
    private int wrongColor = Color.parseColor("#F44336");   // Kırmızı
    private int textDarkColor = Color.parseColor("#333333"); // Koyu gri
    private int textLightColor = Color.WHITE; // Beyaz

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_quiz);
            Log.d(TAG, "onCreate başladı");

            // Kontrolleri başlat
            initializeViews();

            // Intent'ten gelen verileri al
            languageId = getIntent().getIntExtra("languageId", 1);
            level = getIntent().getIntExtra("level", 1);
            Log.d(TAG, "Language ID: " + languageId + ", Level: " + level);

            preferenceManager = new PreferenceManager(this);
            jsonHelper = new JsonHelper(this);

            // Kelimeleri yükle
            loadWords();

            // Soru durumlarını tutan dizileri başlat
            if (words != null && !words.isEmpty()) {
                questionAnsweredState = new boolean[words.size()];
                selectedButtonIndices = new int[words.size()];
                correctButtonIndices = new int[words.size()];
                allOptions = new String[words.size()][4]; // Her soru için 4 seçenek

                // Başlangıçta tüm soruları cevaplanmamış olarak işaretle
                for (int i = 0; i < words.size(); i++) {
                    questionAnsweredState[i] = false;
                    selectedButtonIndices[i] = -1;
                    correctButtonIndices[i] = -1;
                }

                // İlk kelimeyi göster
                updateProgress();
                showWord();
            } else {
                finish();
            }

            // Buton tıklama olaylarını ayarla
            setupButtonListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            finish();
        }
    }

    private void initializeViews() {
        try {
            progressTextView = findViewById(R.id.progressTextView);
            progressBar = findViewById(R.id.progressBar);
            promptTextView = findViewById(R.id.promptTextView);
            wordTextView = findViewById(R.id.wordTextView);
            speakButton = findViewById(R.id.speakButton);

            // Seçenek butonları (TextView olarak)
            option1Button = findViewById(R.id.option1Button);
            option2Button = findViewById(R.id.option2Button);
            option3Button = findViewById(R.id.option3Button);
            option4Button = findViewById(R.id.option4Button);

            // Kartlar
            option1Card = findViewById(R.id.option1Card);
            option2Card = findViewById(R.id.option2Card);
            option3Card = findViewById(R.id.option3Card);
            option4Card = findViewById(R.id.option4Card);

            // Seçenek butonlarını ve kartlarını dizilerde sakla
            optionButtons = new TextView[] {option1Button, option2Button, option3Button, option4Button};
            optionCards = new CardView[] {option1Card, option2Card, option3Card, option4Card};

            previousButton = findViewById(R.id.previousButton);
            nextButton = findViewById(R.id.nextButton);
        } catch (Exception e) {
            Log.e(TAG, "Error in initializeViews", e);
            throw e;
        }
    }

    private void setupButtonListeners() {
        try {
            // Cevap seçenekleri kartları (CardView'lar) için tıklama olayı
            for (int i = 0; i < optionCards.length; i++) {
                final int index = i;
                if (optionCards[i] != null) {
                    optionCards[i].setOnClickListener(v -> {
                        if (!questionAnsweredState[currentIndex]) {
                            String selectedAnswer = optionButtons[index].getText().toString();
                            selectedButtonIndices[currentIndex] = index;
                            checkAnswer(selectedAnswer, index);
                        }
                    });
                }
            }

            // Seçenek butonları (TextView'lar) için tıklama olayı
            for (int i = 0; i < optionButtons.length; i++) {
                final int index = i;
                if (optionButtons[i] != null) {
                    optionButtons[i].setOnClickListener(v -> {
                        if (!questionAnsweredState[currentIndex]) {
                            String selectedAnswer = optionButtons[index].getText().toString();
                            selectedButtonIndices[currentIndex] = index;
                            checkAnswer(selectedAnswer, index);
                        }
                    });
                }
            }

            // Navigasyon butonları
            if (previousButton != null) {
                previousButton.setOnClickListener(this);
            }
            if (nextButton != null) {
                nextButton.setOnClickListener(this);
            }

            // Ses çalma butonu
            if (speakButton != null) {
                speakButton.setOnClickListener(this);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in setupButtonListeners", e);
        }
    }

    @Override
    public void onClick(View view) {
        try {
            int viewId = view.getId();
            // Navigasyon butonları
            if (viewId == R.id.previousButton) {
                showPreviousWord();
            } else if (viewId == R.id.nextButton) {
                showNextWord();
            }
            // Ses çalma butonu
            else if (viewId == R.id.speakButton) {
                speakWord();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onClick", e);
        }
    }

    private void loadWords() {
        try {
            // JsonHelper'dan kelimeleri yükle - seviye için sabit kelimeler
            words = jsonHelper.getWordsForLanguage(languageId, level);
            if (words == null) {
                words = new ArrayList<>();
                Log.e(TAG, "Kelime listesi yüklenemedi!");
            }
            Log.d(TAG, "Kelime sayısı: " + words.size());
            if (words.isEmpty()) {
                finish();
            }

            // Her seviyede aynı kelimelerin aynı sırada görünmesi için karıştırma yok
            // Seçenekler karıştırılabilir ama kelime sırası hep aynı olsun
        } catch (Exception e) {
            Log.e(TAG, "Error in loadWords", e);
        }
    }

    private void updateProgress() {
        try {
            if (progressTextView != null) {
                progressTextView.setText((currentIndex + 1) + "/" + words.size());
            }
            if (progressBar != null) {
                progressBar.setMax(words.size());
                progressBar.setProgress(currentIndex + 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateProgress", e);
        }
    }

    private void showWord() {
        try {
            if (currentIndex < 0) {
                currentIndex = 0;
            } else if (currentIndex >= words.size()) {
                // Quiz tamamlandı
                finishQuiz();
                return;
            }

            currentWord = words.get(currentIndex);
            // Kelimeyi göster
            if (wordTextView != null) {
                wordTextView.setText(currentWord.getOriginalWord());
            }

            // İlerleme durumunu güncelle
            updateProgress();

            // Navigasyon butonlarının durumunu güncelle
            updateNavigationButtons();

            // Eğer soru daha önce cevaplanmışsa, önceki cevabı göster
            if (questionAnsweredState[currentIndex]) {
                restorePreviousAnswer();
            } else {
                // Cevap seçeneklerini hazırla
                prepareOptions();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showWord", e);
        }
    }

    private void restorePreviousAnswer() {
        try {
            // Önce tüm butonları sıfırla
            resetButtons();

            // Kaydedilmiş seçenekleri göster
            for (int i = 0; i < optionButtons.length; i++) {
                if (optionButtons[i] != null && i < allOptions[currentIndex].length && allOptions[currentIndex][i] != null) {
                    optionButtons[i].setText(allOptions[currentIndex][i]);
                }
            }

            // Doğru cevabın indeksini al
            int correctIndex = correctButtonIndices[currentIndex];
            // Seçilen butonun indeksini al
            int selectedIndex = selectedButtonIndices[currentIndex];

            // Eğer bir seçenek seçilmişse
            if (selectedIndex >= 0) {
                if (selectedIndex == correctIndex) {
                    // Doğru cevap - yeşil göster
                    optionCards[selectedIndex].setCardBackgroundColor(correctColor);
                    optionButtons[selectedIndex].setTextColor(textLightColor);
                } else {
                    // Yanlış cevap - kırmızı göster ve doğru cevabı yeşil yap
                    optionCards[selectedIndex].setCardBackgroundColor(wrongColor);
                    optionButtons[selectedIndex].setTextColor(textLightColor);
                    if (correctIndex >= 0 && correctIndex < optionCards.length) {
                        optionCards[correctIndex].setCardBackgroundColor(correctColor);
                        optionButtons[correctIndex].setTextColor(textLightColor);
                    }
                }

                // Diğer seçenekleri pasifleştir
                for (int i = 0; i < optionCards.length; i++) {
                    if (i != selectedIndex && i != correctIndex) {
                        optionCards[i].setClickable(false);
                        optionCards[i].setAlpha(0.5f);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in restorePreviousAnswer", e);
        }
    }

    private void resetButtons() {
        // Tüm seçenek kartlarını ve butonlarını sıfırla
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i] != null && optionCards[i] != null) {
                optionCards[i].setCardBackgroundColor(lightColor);
                optionButtons[i].setTextColor(textDarkColor);
                optionCards[i].setClickable(true);
                optionCards[i].setAlpha(1.0f);
            }
        }
    }

    private void updateNavigationButtons() {
        try {
            if (previousButton != null) {
                previousButton.setEnabled(currentIndex > 0);
                previousButton.setAlpha(currentIndex > 0 ? 1.0f : 0.5f);
                previousButton.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                previousButton.setTextColor(textLightColor);
            }
            if (nextButton != null) {
                boolean isLastQuestion = currentIndex >= words.size() - 1;
                nextButton.setText(isLastQuestion ? "Bitir" : "Sonraki");
                nextButton.setBackgroundTintList(ColorStateList.valueOf(primaryColor));
                nextButton.setTextColor(textLightColor);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in updateNavigationButtons", e);
        }
    }

    private void showPreviousWord() {
        try {
            if (currentIndex > 0) {
                currentIndex--;
                showWord();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showPreviousWord", e);
        }
    }

    private void showNextWord() {
        try {
            if (currentIndex < words.size() - 1) {
                currentIndex++;
                showWord();
            } else {
                // Son soru ise tamamla
                finishQuiz();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in showNextWord", e);
        }
    }

    private void speakWord() {
        try {
            // Burada TTS (Text-to-Speech) veya ses dosyası çalma işlemi yapılabilir
            // Şimdilik boş bırakıyoruz
        } catch (Exception e) {
            Log.e(TAG, "Error in speakWord", e);
        }
    }

    private void prepareOptions() {
        try {
            // Önce tüm butonları sıfırla - hepsi aynı görünümde olsun
            resetButtons();

            // Doğru cevap
            String correctAnswer = currentWord.getTranslatedWord();

            // Eğer halihazırda options var ise kullan, yoksa rastgele oluştur
            String[] options;
            if (currentWord.getOptions() != null && currentWord.getOptions().length >= 4) {
                // JSON'dan gelen hazır seçenekleri kullan
                options = currentWord.getOptions();
                // Seçenekleri karıştır (doğru cevabı rastgele bir pozisyona koy)
                List<String> optionsList = new ArrayList<>(Arrays.asList(options));
                Collections.shuffle(optionsList);
                options = optionsList.toArray(new String[0]);
            } else {
                // Yanlış cevaplar için rastgele kelimeler seç
                List<String> optionsList = new ArrayList<>();
                optionsList.add(correctAnswer);
                List<Word> tempWords = new ArrayList<>(words);
                tempWords.remove(currentWord);
                Collections.shuffle(tempWords);

                // 3 yanlış cevap ekle
                for (int i = 0; i < 3 && i < tempWords.size(); i++) {
                    optionsList.add(tempWords.get(i).getTranslatedWord());
                }

                // Eksik seçenek varsa ekleme yap
                while (optionsList.size() < 4 && tempWords.size() > 0) {
                    optionsList.add("Seçenek " + optionsList.size());
                }

                // Seçenekleri karıştır
                Collections.shuffle(optionsList);
                options = optionsList.toArray(new String[0]);
            }

            // Seçenekleri kaydet
            allOptions[currentIndex] = new String[options.length];
            System.arraycopy(options, 0, allOptions[currentIndex], 0, options.length);

            // Butonlara seçenekleri ata
            for (int i = 0; i < optionButtons.length && i < options.length; i++) {
                if (optionButtons[i] != null) {
                    optionButtons[i].setText(options[i]);
                    // Doğru cevabın hangi butonda olduğunu kaydet
                    if (options[i].equals(correctAnswer)) {
                        correctButtonIndices[currentIndex] = i;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in prepareOptions", e);
        }
    }

    private void checkAnswer(String selectedAnswer, int selectedIndex) {
        try {
            // Soruyu cevaplanmış olarak işaretle
            questionAnsweredState[currentIndex] = true;
            // Seçilen indeksi kaydet
            selectedButtonIndices[currentIndex] = selectedIndex;

            String correctAnswer = currentWord.getTranslatedWord();
            boolean isCorrect = correctAnswer.equals(selectedAnswer);

            if (isCorrect) {
                // Doğru cevap
                correctAnswers++;
                // Seçilen kartı yeşil yap
                if (selectedIndex >= 0 && selectedIndex < optionCards.length) {
                    optionCards[selectedIndex].setCardBackgroundColor(correctColor);
                    optionButtons[selectedIndex].setTextColor(textLightColor);
                }
            } else {
                // Yanlış cevap
                // Seçilen kartı kırmızı yap
                if (selectedIndex >= 0 && selectedIndex < optionCards.length) {
                    optionCards[selectedIndex].setCardBackgroundColor(wrongColor);
                    optionButtons[selectedIndex].setTextColor(textLightColor);
                }
                // Doğru cevabı içeren kartı yeşil yap
                int correctIndex = correctButtonIndices[currentIndex];
                if (correctIndex >= 0 && correctIndex < optionCards.length) {
                    optionCards[correctIndex].setCardBackgroundColor(correctColor);
                    optionButtons[correctIndex].setTextColor(textLightColor);
                }
            }

            // Diğer seçenekleri devre dışı bırak
            for (int i = 0; i < optionCards.length; i++) {
                if (i != selectedIndex && i != correctButtonIndices[currentIndex]) {
                    optionCards[i].setClickable(false);
                    optionCards[i].setAlpha(0.5f);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in checkAnswer", e);
        }
    }

    private void finishQuiz() {
        try {
            // Başarı oranını hesapla
            float successRate = words.size() > 0 ? (float) correctAnswers * 100 / words.size() : 0;

            // Mevcut seviye durumunu kontrol et
            int savedCorrectAnswers = preferenceManager.getCorrectAnswers(languageId, level);
            float savedSuccessRate = preferenceManager.getSuccessRate(languageId, level);
            boolean isLevelCompleted = preferenceManager.isLevelCompleted(languageId, level);

            Log.d(TAG, "Mevcut durum - Tamamlandı: " + isLevelCompleted + ", Skor: " + savedSuccessRate + "%");
            Log.d(TAG, "Yeni skor: " + successRate + "%");

            // ÖNEMLİ DEĞİŞİKLİK: Seviye tamamlanma mantığı

            // 1. Eğer seviye daha önce tamamlanmamışsa ve yeni başarı oranı %70 veya üzeriyse
            if (!isLevelCompleted && successRate >= 70) {
                Log.d(TAG, "Seviye ilk kez tamamlandı! Seviye: " + level + ", Başarı: " + successRate + "%");
                preferenceManager.saveQuizResults(languageId, level, correctAnswers, words.size(), successRate);
                preferenceManager.setLevelCompleted(languageId, level, true);
                preferenceManager.unlockNextLevel(languageId, level);
            }
            // 2. Eğer seviye zaten tamamlanmışsa ama yeni skor daha iyiyse
            else if (isLevelCompleted && successRate > savedSuccessRate) {
                Log.d(TAG, "Daha iyi skor elde edildi! Seviye: " + level + ", Yeni başarı: " + successRate + "%, Eski başarı: " + savedSuccessRate + "%");
                preferenceManager.setCorrectAnswers(languageId, level, correctAnswers);
                preferenceManager.setSuccessRate(languageId, level, successRate);
                // Seviye zaten tamamlanmış olduğu için tekrar setLevelCompleted çağrılmasına gerek yok
            }
            // 3. Eğer seviye tamamlanmadıysa ve başarı %70'in altındaysa normal kayıt yap
            else if (!isLevelCompleted) {
                Log.d(TAG, "Seviye tamamlanamadı. Seviye: " + level + ", Başarı: " + successRate + "%");
                preferenceManager.saveQuizResults(languageId, level, correctAnswers, words.size(), successRate);
                // Başarı oranı düşük olduğu için seviye tamamlanmadı olarak kalır
            }
            // 4. Seviye tamamlanmışsa ve yeni skor daha düşükse hiçbir şey yapma
            else {
                Log.d(TAG, "Değişiklik yok. Seviye zaten tamamlanmış: " + level + ", Mevcut başarı: " + savedSuccessRate + "%, Yeni başarı: " + successRate + "%");
                // Hiçbir şey yapma, mevcut durum korunsun
            }

            // Aktiviteyi kapat
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error in finishQuiz", e);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Eğer ses çalınıyorsa temizle
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}