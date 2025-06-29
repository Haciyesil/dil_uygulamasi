package com.example.diluygulamas.adapter;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.diluygulamas.LevelActivity;
import com.example.diluygulamas.R;
import com.example.diluygulamas.model.ContentItem;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ContentAdapter extends RecyclerView.Adapter<ContentAdapter.ContentViewHolder> {
    private List<ContentItem> contentItems;
    private String category; // kategori bilgisi
    private Map<String, Integer> colorMap; // Renk adı - RGB renk eşleştirme haritası

    public ContentAdapter(List<ContentItem> contentItems, String category) {
        this.contentItems = contentItems;
        this.category = category;
        initColorMap();
    }

    // Eski constructor - geriye uyumluluk için
    public ContentAdapter(List<ContentItem> contentItems, boolean isColorCategory) {
        this.contentItems = contentItems;
        this.category = isColorCategory ? "colors" : "";
        initColorMap();
    }

    private void initColorMap() {
        colorMap = new HashMap<>();
        colorMap.put("kırmızı", Color.rgb(255, 0, 0));
        colorMap.put("mavi", Color.rgb(0, 0, 255));
        colorMap.put("yeşil", Color.rgb(0, 128, 0));
        colorMap.put("sarı", Color.rgb(255, 255, 0));
        colorMap.put("beyaz", Color.rgb(255, 255, 255));
        colorMap.put("siyah", Color.rgb(0, 0, 0));
        // Diğer renkler ihtiyaca göre eklenebilir
    }

    @NonNull
    @Override
    public ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_content, parent, false);
        return new ContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {
        ContentItem item = contentItems.get(position);
        holder.bind(item, category, colorMap);

        // Kelimelere tıklanınca LevelActivity'e yönlendirme yapacak
        if ("words".equals(category)) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Kullanıcı herhangi bir kelimeye tıkladığında, bu kelimenin diline ait seviyeler sayfasına gitmeli
                    Intent intent = new Intent(holder.itemView.getContext(), LevelActivity.class);

                    // Dil bilgisini aktar
                    int languageId = item.getLanguageId() > 0 ? item.getLanguageId() : 1; // Varsayılan 1 (Türkmence)
                    intent.putExtra("languageId", languageId);

                    // Dil adı genellikle ContentActivity'den gelir, eğer varsa al
                    String languageName = getLanguageName(languageId);
                    if (languageName != null && !languageName.isEmpty()) {
                        intent.putExtra("languageName", languageName);
                    }

                    // Kategori bilgisini de aktar (seviye sayfasında filtre olarak kullanılabilir)
                    intent.putExtra("category", "words");

                    // Aktiviteyi başlat
                    holder.itemView.getContext().startActivity(intent);
                }
            });

            // Tıklanabilir öğe için görsel ipucu
            if (holder.itemView instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) holder.itemView).setCardElevation(4f);
                ((com.google.android.material.card.MaterialCardView) holder.itemView).setStrokeWidth(1);
                ((com.google.android.material.card.MaterialCardView) holder.itemView).setStrokeColor(
                        Color.parseColor("#DDDDDD"));
            }
        }
    }

    // Dil ID'sine göre dil adını döndüren yardımcı metot
    private String getLanguageName(int languageId) {
        switch (languageId) {
            case 1: return "Türkmence";
            case 2: return "Filistince";
            case 3: return "İngilizce";
            case 4: return "Almanca";
            case 5: return "Fransızca";
            case 6: return "İspanyolca";
            case 7: return "İtalyanca";
            case 8: return "Rusça";
            case 9: return "Japonca";
            default: return "Bilinmeyen Dil";
        }
    }

    @Override
    public int getItemCount() {
        return contentItems != null ? contentItems.size() : 0;
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView originalTextView;
        private TextView translatedTextView;
        private TextView pronunciationTextView;
        private TextView monthsTextView;
        private ImageView seasonImageView;
        private View colorPreview;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);
            originalTextView = itemView.findViewById(R.id.originalText);
            translatedTextView = itemView.findViewById(R.id.translatedText);
            pronunciationTextView = itemView.findViewById(R.id.pronunciationText);
            monthsTextView = itemView.findViewById(R.id.monthsText);
            seasonImageView = itemView.findViewById(R.id.seasonImageView);
            colorPreview = itemView.findViewById(R.id.colorPreview);
        }

        public void bind(ContentItem item, String category, Map<String, Integer> colorMap) {
            originalTextView.setText(item.getOriginalText());
            translatedTextView.setText(item.getTranslatedText());
            if (item.getPronunciation() != null && !item.getPronunciation().isEmpty()) {
                pronunciationTextView.setText(item.getPronunciation());
                pronunciationTextView.setVisibility(View.VISIBLE);
            } else {
                pronunciationTextView.setVisibility(View.GONE);
            }

            // Kategori tipine göre özel bileşenleri göster/gizle
            boolean isColorCategory = "colors".equals(category);
            boolean isSeasonCategory = "seasons".equals(category);
            boolean isWordCategory = "words".equals(category);

            if (isColorCategory) {
                // Renk kategorisi
                showColorPreview(item, colorMap);
                hideSeasonContent();
            }
            else if (isSeasonCategory) {
                // Mevsim kategorisi
                hideColorPreview();
                showSeasonContent(item);
            }
            else if (isWordCategory) {
                // Kelimeler kategorisi - Burada kelimeler için özel görünüm
                hideColorPreview();
                hideSeasonContent();

                // Kelimelerin tıklanabilir olduğunu belirtmek için görsel ipucu
                itemView.setBackgroundResource(android.R.drawable.list_selector_background);
            }
            else {
                // Diğer kategoriler (sayılar vb.)
                hideColorPreview();
                hideSeasonContent();
            }
        }

        private void showColorPreview(ContentItem item, Map<String, Integer> colorMap) {
            // Renk önizlemesini göster
            colorPreview.setVisibility(View.VISIBLE);
            // Renk değerini belirle - önce haritadan bakıp bulamazsa mevcut değerleri kullan
            int color;
            String translatedLower = item.getTranslatedText().toLowerCase();
            if (colorMap.containsKey(translatedLower)) {
                // Haritadaki önceden tanımlanmış rengi kullan
                color = colorMap.get(translatedLower);
                // Ayrıca item'in RGB değerlerini de güncelle
                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                item.setRed(red);
                item.setGreen(green);
                item.setBlue(blue);
            } else {
                // Item'ın kendi RGB değerlerini kullan
                if (item.getRed() == 0 && item.getGreen() == 0 && item.getBlue() == 0) {
                    // Henüz bir renk atanmamış, varsayılan değerleri ayarla
                    item.setColorByTranslation();
                }
                color = Color.rgb(item.getRed(), item.getGreen(), item.getBlue());
            }
            // Renk önizlemesine hafif yuvarlatılmış köşeler ver
            GradientDrawable shape = new GradientDrawable();
            shape.setColor(color);
            shape.setCornerRadii(new float[] {0, 0, 0, 0, 24, 24, 24, 24}); // sadece alt köşeleri yuvarla
            colorPreview.setBackground(shape);
            // Kartın stilini güzelleştir
            if (itemView instanceof com.google.android.material.card.MaterialCardView) {
                ((com.google.android.material.card.MaterialCardView) itemView).setCardBackgroundColor(Color.WHITE);
            }
        }

        private void hideColorPreview() {
            colorPreview.setVisibility(View.GONE);
        }

        private void showSeasonContent(ContentItem item) {
            // Mevsim aylarını göster
            if (monthsTextView != null) {
                if (item.getMonths() != null && !item.getMonths().isEmpty()) {
                    monthsTextView.setText(item.getMonths());
                    monthsTextView.setVisibility(View.VISIBLE);
                } else {
                    monthsTextView.setVisibility(View.GONE);
                }
            }
            // Mevsim görselini göster
            if (seasonImageView != null) {
                if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
                    try {
                        Glide.with(itemView.getContext())
                                .load(item.getImageUrl())
                                .override(800, 400)
                                .centerCrop()
                                .into(seasonImageView);
                        seasonImageView.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        seasonImageView.setVisibility(View.GONE);
                    }
                } else {
                    seasonImageView.setVisibility(View.GONE);
                }
            }
        }

        private void hideSeasonContent() {
            if (monthsTextView != null) monthsTextView.setVisibility(View.GONE);
            if (seasonImageView != null) seasonImageView.setVisibility(View.GONE);
        }
    }
}