package com.example.diluygulamas;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;  // Bu satırı ekleyin
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diluygulamas.adapter.LevelAdapter;
import com.example.diluygulamas.model.Level;
import com.example.diluygulamas.util.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

public class LevelActivity extends AppCompatActivity {
    private static final String TAG = "LevelActivity";
    private int languageId;
    private String languageName;
    private String category;
    private RecyclerView recyclerView;
    private LevelAdapter adapter;
    private PreferenceManager preferenceManager;
    private List<Level> levels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        Log.d(TAG, "LevelActivity onCreate başladı");

        // Intent'ten gelen bilgileri al
        languageId = getIntent().getIntExtra("languageId", 1);
        languageName = getIntent().getStringExtra("languageName");
        category = getIntent().getStringExtra("category");
        Log.d(TAG, "Intent'ten gelen bilgiler: languageId=" + languageId + ", languageName=" + languageName);

        // Başlığı ayarla
        TextView titleTextView = findViewById(R.id.titleTextView);
        if (titleTextView != null) {
            titleTextView.setText(languageName + " - Seviyeler");
        }

        // PreferenceManager'ı başlat
        preferenceManager = new PreferenceManager(this);

        // Geçici reset butonu ekle - GELİŞTİRME SIRASINDA KULLANIN, SONRA KALDIR
        Button resetButton = new Button(this);
        resetButton.setText("Verileri Sıfırla");
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAppData();
            }
        });

        // Butonu layout'a ekle
        ViewGroup rootLayout = findViewById(android.R.id.content);
        if (rootLayout != null && rootLayout.getChildCount() > 0 && rootLayout.getChildAt(0) instanceof ViewGroup) {
            ViewGroup mainLayout = (ViewGroup) rootLayout.getChildAt(0);
            mainLayout.addView(resetButton, 0); // En üste ekle
        }

        // RecyclerView'ı ayarla
        recyclerView = findViewById(R.id.recyclerView);
        if (recyclerView != null) {
            // Tüm cihazlar için sabit 2 sütun kullan
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            Log.d(TAG, "RecyclerView sütun sayısı: 2 olarak ayarlandı");

            // Seviyeleri yükle
            loadLevels();

            // Adapter'ı ayarla
            adapter = new LevelAdapter(levels, this, languageId);
            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView ve adapter kuruldu, seviye sayısı: " + levels.size());
        } else {
            Log.e(TAG, "RecyclerView bulunamadı!");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "LevelActivity onResume çağrıldı");

        // Aktivite tekrar gösterildiğinde seviyeleri yenile
        loadLevels();
        if (adapter != null) {
            adapter.updateLevels(levels);
            adapter.notifyDataSetChanged();
            Log.d(TAG, "onResume: seviyeler güncellendi");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "LevelActivity onPause çağrıldı");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "LevelActivity onStop çağrıldı");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "LevelActivity onDestroy çağrıldı");
    }

    private void loadLevels() {
        try {
            levels.clear();
            // Her dil için 16 seviye (400 kelime / 25 kelime per seviye = 16 seviye)
            int maxLevels = 16;

            // İlk seviye her zaman açık olmalı
            preferenceManager.setLevelUnlocked(languageId, 1, true);

            for (int i = 1; i <= maxLevels; i++) {
                boolean isUnlocked = preferenceManager.isLevelUnlocked(languageId, i);
                boolean isCompleted = preferenceManager.isLevelCompleted(languageId, i);
                float successRate = preferenceManager.getSuccessRate(languageId, i);
                int totalWords = preferenceManager.getTotalWords(languageId, i);
                int correctAnswers = preferenceManager.getCorrectAnswers(languageId, i);

                // Her seviye için varsayılan 25 kelime olacak
                if (totalWords == 0 || totalWords == 50) { // 50 değerini de kontrol edin
                    totalWords = 25;
                    // Bu değeri kaydet
                    preferenceManager.setTotalWords(languageId, i, totalWords);
                }

                Level level = new Level();
                level.setId(i);
                level.setLevelNumber(i);
                level.setLanguageId(languageId);
                level.setUnlocked(isUnlocked);
                level.setCompleted(isCompleted);
                level.setSuccessRate(successRate);
                level.setTotalWords(totalWords);
                level.setCorrectAnswers(correctAnswers);
                levels.add(level);

                Log.d(TAG, "Seviye eklendi: " + i + ", Açık mı: " + isUnlocked +
                        ", Tamamlandı mı: " + isCompleted + ", Toplam kelime: " + totalWords);
            }
        } catch (Exception e) {
            Log.e(TAG, "loadLevels hatası: " + e.getMessage(), e);
            Toast.makeText(this, "Seviyeler yüklenirken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Tüm verileri sıfırlamak için yeni yardımcı metod
    private void resetAppData() {
        try {
            preferenceManager.resetAllData();
            Toast.makeText(this, "Tüm veriler sıfırlandı", Toast.LENGTH_SHORT).show();

            // İlk seviyeyi tekrar aç
            preferenceManager.setLevelUnlocked(languageId, 1, true);

            // Seviyeleri yeniden yükle
            loadLevels();

            // Adapter'ı güncelle
            if (adapter != null) {
                adapter.updateLevels(levels);
                adapter.notifyDataSetChanged();
            }

            Log.d(TAG, "Uygulama verileri başarıyla sıfırlandı");
        } catch (Exception e) {
            Log.e(TAG, "resetAppData hatası: " + e.getMessage(), e);
            Toast.makeText(this, "Veriler sıfırlanırken hata: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Doğrudan bir seviyeyi başlatmak için yardımcı metod (LevelAdapter'dan çağrılabilir)
    public void startQuizForLevel(int level) {
        try {
            Log.d(TAG, "startQuizForLevel çağrıldı: seviye " + level);
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("languageId", languageId);
            intent.putExtra("languageName", languageName);
            intent.putExtra("level", level);

            // Toplam kelime sayısını da gönder
            int totalWords = 25; // Varsayılan değer
            for (Level lvl : levels) {
                if (lvl.getLevelNumber() == level) {
                    totalWords = lvl.getTotalWords();
                    break;
                }
            }
            intent.putExtra("totalWords", totalWords);

            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "startQuizForLevel hatası: " + e.getMessage(), e);
            Toast.makeText(this, "Quiz başlatılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}