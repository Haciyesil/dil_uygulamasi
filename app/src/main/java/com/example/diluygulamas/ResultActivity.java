package com.example.diluygulamas;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.diluygulamas.util.PreferenceManager;

public class ResultActivity extends AppCompatActivity {

    private TextView resultTextView;
    private TextView scoreTextView;
    private TextView successRateTextView;
    private Button nextLevelButton;
    private Button retryButton;
    private Button homeButton;

    private int correctAnswers;
    private int totalQuestions;
    private int languageId;
    private int level;
    private float successRate;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_result);

            // Intent'ten sonuç bilgilerini al
            correctAnswers = getIntent().getIntExtra("correctAnswers", 0);
            totalQuestions = getIntent().getIntExtra("totalQuestions", 0);
            languageId = getIntent().getIntExtra("languageId", 1);
            level = getIntent().getIntExtra("level", 1);
            successRate = getIntent().getFloatExtra("successRate", 0);

            preferenceManager = new PreferenceManager(this);

            // UI elemanlarını tanımla
            resultTextView = findViewById(R.id.resultTextView);
            scoreTextView = findViewById(R.id.scoreTextView);
            successRateTextView = findViewById(R.id.successRateTextView);
            nextLevelButton = findViewById(R.id.nextLevelButton);
            retryButton = findViewById(R.id.retryButton);
            homeButton = findViewById(R.id.homeButton);

            // Sonuç bilgilerini göster
            scoreTextView.setText(correctAnswers + "/" + totalQuestions);
            successRateTextView.setText("Başarı Oranı: %" + Math.round(successRate));

            if (successRate >= 75) {
                resultTextView.setText("Tebrikler! Seviyeyi başarıyla tamamladınız.");

                // Seviyeyi tamamlandı olarak işaretle
                preferenceManager.setLevelCompleted(languageId, level, true);

                // Sonraki seviyenin kilidini aç
                preferenceManager.setLevelUnlocked(languageId, level + 1, true);

                // Sonraki seviye açıldı mı kontrol et
                boolean isNextLevelUnlocked = preferenceManager.isLevelUnlocked(languageId, level + 1);
                nextLevelButton.setEnabled(isNextLevelUnlocked);

                if (!isNextLevelUnlocked) {
                    nextLevelButton.setText("Sonraki Seviye (Kilitli)");
                }
            } else {
                resultTextView.setText("Üzgünüm, seviyeyi tamamlamak için en az %75 başarı gerekiyor.");
                nextLevelButton.setEnabled(false);
                nextLevelButton.setText("Sonraki Seviye (Kilitli)");
            }

            // Buton tıklama olaylarını ayarla
            nextLevelButton.setOnClickListener(v -> {
                if (preferenceManager.isLevelUnlocked(languageId, level + 1)) {
                    Intent intent = new Intent(this, QuizActivity.class);
                    intent.putExtra("languageId", languageId);
                    intent.putExtra("level", level + 1);
                    startActivity(intent);
                    finish();
                }
            });

            retryButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, QuizActivity.class);
                intent.putExtra("languageId", languageId);
                intent.putExtra("level", level);
                startActivity(intent);
                finish();
            });

            homeButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Hata: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}