package com.example.diluygulamas;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class CategoriesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Intent'ten dil bilgisini al
        int languageId = getIntent().getIntExtra("languageId", 1);
        String languageName = getIntent().getStringExtra("languageName");

        // Toolbar başlığını ayarla
        setTitle(languageName);

        // Kategori kartlarını bul
        CardView colorsCard = findViewById(R.id.colorsCard);
        CardView numbersCard = findViewById(R.id.numbersCard);
        CardView seasonsCard = findViewById(R.id.seasonsCard);
        CardView wordsCard = findViewById(R.id.wordsCard);

        // Renk kategorisine tıklama olayı
        colorsCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, ContentActivity.class);
            intent.putExtra("languageId", languageId);
            intent.putExtra("languageName", languageName);
            intent.putExtra("category", "colors");
            startActivity(intent);
        });

        // Sayılar kategorisine tıklama olayı
        numbersCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, ContentActivity.class);
            intent.putExtra("languageId", languageId);
            intent.putExtra("languageName", languageName);
            intent.putExtra("category", "numbers");
            startActivity(intent);
        });

        // Mevsimler kategorisine tıklama olayı
        seasonsCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, ContentActivity.class);
            intent.putExtra("languageId", languageId);
            intent.putExtra("languageName", languageName);
            intent.putExtra("category", "seasons");
            startActivity(intent);
        });

        // Kelimeler kategorisine tıklama olayı (LevelActivity'ye gider)
        wordsCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoriesActivity.this, LevelActivity.class);
            intent.putExtra("languageId", languageId);
            intent.putExtra("languageName", languageName);
            intent.putExtra("category", "words");
            startActivity(intent);
        });
    }
}