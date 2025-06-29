package com.example.diluygulamas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import com.example.diluygulamas.adapter.LanguageAdapter;
import com.example.diluygulamas.model.Language;
import com.example.diluygulamas.util.PreferenceManager;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private LanguageAdapter adapter;
    private List<Language> languages;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferenceManager = new PreferenceManager(this);

        // Dilleri oluştur
        createLanguages();

        // Dil listesini logla
        Log.d(TAG, "Oluşturulan dil sayısı: " + languages.size());
        for (Language lang : languages) {
            Log.d(TAG, "Dil: " + lang.getName() + ", Bayrak: " + lang.getFlagName());
        }

        // RecyclerView'ı ayarla
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new LanguageAdapter(languages, this);
        recyclerView.setAdapter(adapter);
    }

    private void createLanguages() {
        languages = new ArrayList<>();

        // Bayrak adları ile birlikte dilleri ekle
        languages.add(new Language(1, "Türkmence", "tm", true, "flag_turkmenistan"));
        languages.add(new Language(2, "Filistince", "ps", true, "flag_palestine"));
        languages.add(new Language(3, "İngilizce", "en", true, "flag_english"));
        languages.add(new Language(4, "Almanca", "de", true, "flag_germany"));
        languages.add(new Language(5, "Fransızca", "fr", true, "flag_france"));
        languages.add(new Language(6, "İspanyolca", "es", true, "flag_spain"));
        languages.add(new Language(7, "İtalyanca", "it", true, "flag_italy"));
        languages.add(new Language(8, "Rusça", "ru", true, "flag_russia"));
        languages.add(new Language(9, "Japonca", "ja", true, "flag_japan"));
    }
}