package com.example.diluygulamas.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diluygulamas.CategoriesActivity;  // DEĞİŞİKLİK: LevelActivity yerine CategoriesActivity
import com.example.diluygulamas.R;
import com.example.diluygulamas.model.Language;
import com.example.diluygulamas.util.PreferenceManager;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {
    private List<Language> languages;
    private Context context;
    private PreferenceManager preferenceManager;
    // Resource ID'leri önbelleğe almak için
    private Map<String, Integer> resourceCache = new HashMap<>();

    public LanguageAdapter(List<Language> languages, Context context) {
        this.languages = languages;
        this.context = context;
        this.preferenceManager = new PreferenceManager(context);
        // Bayrak ID'lerini önceden yükle
        preloadFlags();
    }

    // Bayrak ID'lerini önceden yükleyerek her seferinde tekrar tekrar aramayı önle
    private void preloadFlags() {
        String[] flagNames = {
                "flag_turkmenistan", "flag_palestine", "flag_english", "flag_germany",
                "flag_france", "flag_spain", "flag_italy", "flag_russia", "flag_japan"
        };

        for (String flagName : flagNames) {
            int resourceId = context.getResources().getIdentifier(
                    flagName, "drawable", context.getPackageName());
            resourceCache.put(flagName, resourceId);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_language, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Language language = languages.get(position);
        holder.languageName.setText(language.getName());

        // Bayrak yükleme - önbellek kullanarak
        String flagName = language.getFlagName();
        if (flagName != null && !flagName.isEmpty()) {
            Integer resourceId = resourceCache.get(flagName);
            if (resourceId != null && resourceId != 0) {
                holder.flagImage.setImageResource(resourceId);
                holder.flagImage.setVisibility(View.VISIBLE);
            } else {
                // Önbellekte yoksa doğrudan ata (sadece ilk çalıştırmada gerçekleşecek)
                setFlagByLanguageName(holder, language.getName());
            }
        } else {
            // Bayrak adı yoksa doğrudan ata
            setFlagByLanguageName(holder, language.getName());
        }

        if (language.isUnlocked()) {
            holder.cardView.setOnClickListener(v -> {
                preferenceManager.setCurrentLanguage(language.getId());

                // DEĞİŞİKLİK: LevelActivity yerine CategoriesActivity'ye git
                Intent intent = new Intent(context, CategoriesActivity.class);
                intent.putExtra("languageId", language.getId());
                intent.putExtra("languageName", language.getName());
                context.startActivity(intent);
            });
        } else {
            holder.cardView.setAlpha(0.5f);
            holder.languageName.setText(language.getName() + " (Kilitli)");
        }
    }

    // Dil adına göre bayrak atama - try-catch blokları kaldırıldı
    private void setFlagByLanguageName(ViewHolder holder, String languageName) {
        int resourceId = 0;

        switch (languageName) {
            case "Türkmence":
                resourceId = R.drawable.flag_turkmenistan;
                break;
            case "Filistince":
                resourceId = R.drawable.flag_palestine;
                break;
            case "İngilizce":
                resourceId = R.drawable.flag_english;
                break;
            case "Almanca":
                resourceId = R.drawable.flag_germany;
                break;
            case "Fransızca":
                resourceId = R.drawable.flag_france;
                break;
            case "İspanyolca":
                resourceId = R.drawable.flag_spain;
                break;
            case "İtalyanca":
                resourceId = R.drawable.flag_italy;
                break;
            case "Rusça":
                resourceId = R.drawable.flag_russia;
                break;
            case "Japonca":
                resourceId = R.drawable.flag_japan;
                break;
        }

        if (resourceId != 0) {
            holder.flagImage.setImageResource(resourceId);
            holder.flagImage.setVisibility(View.VISIBLE);
        } else {
            holder.flagImage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView languageName;
        ImageView flagImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            languageName = itemView.findViewById(R.id.languageName);
            flagImage = itemView.findViewById(R.id.flagImage);
        }
    }
}