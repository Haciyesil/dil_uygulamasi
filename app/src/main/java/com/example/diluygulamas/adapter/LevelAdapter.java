package com.example.diluygulamas.adapter;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diluygulamas.LevelActivity;
import com.example.diluygulamas.QuizActivity;
import com.example.diluygulamas.R;
import com.example.diluygulamas.model.Level;
import com.example.diluygulamas.util.PreferenceManager;
import java.util.List;

public class LevelAdapter extends RecyclerView.Adapter<LevelAdapter.ViewHolder> {
    private static final String TAG = "LevelAdapter";
    private List<Level> levels;
    private Context context;
    private int languageId;
    private PreferenceManager preferenceManager;
    private boolean isClickEnabled = true; // Çift tıklamayı önlemek için

    public LevelAdapter(List<Level> levels, Context context, int languageId) {
        this.levels = levels;
        this.context = context;
        this.languageId = languageId;
        this.preferenceManager = new PreferenceManager(context);
        Log.d(TAG, "LevelAdapter oluşturuldu, seviye sayısı: " + (levels != null ? levels.size() : 0));
    }

    // Seviyeleri güncellemek için yeni bir metod
    public void updateLevels(List<Level> newLevels) {
        this.levels = newLevels;
        Log.d(TAG, "Seviyeler güncellendi, yeni boyut: " + (newLevels != null ? newLevels.size() : 0));
        notifyDataSetChanged(); // Tüm listeyi yenile
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        try {
            Log.d(TAG, "onCreateViewHolder çağrıldı");
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_level, parent, false);
            return new ViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "onCreateViewHolder hatası: " + e.getMessage(), e);
            // Hata durumunda boş bir view döndür
            View emptyView = new View(parent.getContext());
            return new ViewHolder(emptyView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try {
            Level level = levels.get(position);

            // CardView'u tıklanabilir ve odaklanabilir yap
            holder.cardView.setClickable(true);
            holder.cardView.setFocusable(true);

            // Seviye numarasını ayarla
            holder.levelNumber.setText(String.valueOf(level.getLevelNumber()));

            boolean isUnlocked = level.isUnlocked();
            boolean isCompleted = level.isCompleted();

            if (isUnlocked) {
                // Seviye açık
                holder.cardView.setAlpha(1.0f);

                // Seviye başarı durumu
                int correctAnswers = level.getCorrectAnswers();
                int totalWords = level.getTotalWords();
                float successRate = level.getSuccessRate();
                int progressPercent = Math.round(successRate);

                holder.progressBar.setVisibility(View.VISIBLE);
                holder.progressBar.setProgress(progressPercent);

                if (isCompleted) {
                    // Tamamlanmış seviye - mavi gradient
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[] {
                                    ContextCompat.getColor(context, R.color.completed_gradient_start),
                                    ContextCompat.getColor(context, R.color.completed_gradient_end)
                            });
                    gradientDrawable.setCornerRadius(holder.cardView.getRadius());
                    holder.cardView.setBackground(gradientDrawable);

                    // İkon yerine durum metni
                    holder.statusIcon.setVisibility(View.GONE);

                    // Başarı oranına göre yıldız sayısını ayarla
                    String starsText;
                    if (successRate == 100) {
                        // 3 yıldız - %100 başarı
                        starsText = "<font color='#FFEB3B'>★★★</font>";
                    } else if (successRate >= 75) {
                        // 2 yıldız - %75-99 başarı
                        starsText = "<font color='#FFEB3B'>★★</font>☆";
                    } else {
                        // 1 yıldız - %50-74 başarı
                        starsText = "<font color='#FFEB3B'>★</font>☆☆";
                    }

                    // Başarı oranını göster
                    holder.levelProgress.setText(correctAnswers + "/" + totalWords + " doğru - %" + Math.round(successRate));

                    // Yıldızları HTML olarak göster (sarı renkte)
                    if (holder.starsTextView != null) {
                        Spanned formattedStars = Html.fromHtml(starsText, Html.FROM_HTML_MODE_COMPACT);
                        holder.starsTextView.setText(formattedStars);
                        holder.starsTextView.setVisibility(View.VISIBLE);
                    } else {
                        // starsTextView yoksa levelProgress'e ekle
                        holder.levelProgress.setText(correctAnswers + "/" + totalWords + " doğru - %" + Math.round(successRate) + "\n");
                        Spanned formattedStars = Html.fromHtml(starsText, Html.FROM_HTML_MODE_COMPACT);
                        holder.levelProgress.append(formattedStars);
                    }

                    // Metin rengini beyaz yap
                    holder.levelProgress.setTextColor(ContextCompat.getColor(context, R.color.white));
                } else {
                    // Açık ama tamamlanmamış seviye - mor-mavi gradient
                    GradientDrawable gradientDrawable = new GradientDrawable(
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            new int[] {
                                    ContextCompat.getColor(context, R.color.level_gradient_start),
                                    ContextCompat.getColor(context, R.color.level_gradient_end)
                            });
                    gradientDrawable.setCornerRadius(holder.cardView.getRadius());
                    holder.cardView.setBackground(gradientDrawable);

                    // İlerleme okunu göster
                    holder.statusIcon.setVisibility(View.VISIBLE);
                    holder.statusIcon.setImageResource(android.R.drawable.ic_media_play);
                    holder.statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));

                    // Sonuçları göster
                    holder.levelProgress.setText(correctAnswers + "/" + totalWords + " doğru");

                    // Yıldızları gizle
                    if (holder.starsTextView != null) {
                        holder.starsTextView.setVisibility(View.GONE);
                    }

                    // Metin rengini beyaz yap
                    holder.levelProgress.setTextColor(ContextCompat.getColor(context, R.color.white));
                }

                // Kartın click olayı - ContentActivity yerine QuizActivity'ye yönlendir
                final int levelNumber = level.getLevelNumber(); // final değişken kullan

                // Tüm satıra tıklama olayını ekle (daha geniş tıklama alanı için)
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleCardClick(levelNumber, level);
                    }
                });

                // CardView'a da ayrı tıklama olayı ekle
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleCardClick(levelNumber, level);
                    }
                });

                // Tıklama geri bildirimini görsel olarak iyileştirme
                holder.cardView.setBackgroundTintList(null); // tint'i temizle

                // API seviyesine göre foreground ekle
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    try {
                        holder.cardView.setForeground(ContextCompat.getDrawable(context, android.R.drawable.list_selector_background));
                    } catch (Exception e) {
                        Log.e(TAG, "Foreground ayarlanamadı: " + e.getMessage());
                    }
                }
            } else {
                // Seviye kilitli - modern gri gradient
                GradientDrawable gradientDrawable = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {
                                ContextCompat.getColor(context, R.color.locked_gradient_start),
                                ContextCompat.getColor(context, R.color.locked_gradient_end)
                        });
                gradientDrawable.setCornerRadius(holder.cardView.getRadius());
                holder.cardView.setBackground(gradientDrawable);

                // Seviye numarası için özel arka plan (mor daire) - header_background rengini kullan
                GradientDrawable levelCircle = new GradientDrawable();
                levelCircle.setShape(GradientDrawable.OVAL);
                levelCircle.setColor(ContextCompat.getColor(context, R.color.header_background));
                holder.levelNumber.setBackground(levelCircle);

                // Kilit ikonu göster
                holder.statusIcon.setVisibility(View.VISIBLE);
                holder.statusIcon.setImageResource(android.R.drawable.ic_lock_idle_lock);
                holder.statusIcon.setColorFilter(ContextCompat.getColor(context, R.color.white));

                // Progress bilgisini gizle veya mesaj göster
                holder.progressBar.setVisibility(View.GONE);
                holder.levelProgress.setText("Önceki seviyeyi tamamlayın");
                holder.levelProgress.setTextColor(ContextCompat.getColor(context, R.color.white));

                // Yıldızları gizle
                if (holder.starsTextView != null) {
                    holder.starsTextView.setVisibility(View.GONE);
                }

                // Kart tıklanabilir değil veya uyarı verir
                holder.cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, "Kilitli seviye kartına tıklandı");
                        Toast.makeText(context, "Bu seviyeyi açmak için önceki seviyeyi tamamlayın", Toast.LENGTH_SHORT).show();
                    }
                });

                // Kilitli görünüm için saydamlık (biraz daha opak)
                holder.cardView.setAlpha(0.95f);

                // Hafif gölge efekti
                holder.cardView.setCardElevation(4f);
            }
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder hatası: " + e.getMessage(), e);
        }
    }

    // Kart tıklama olayını yönetmek için yardımcı metod
    private void handleCardClick(int levelNumber, Level level) {
        if (!isClickEnabled) {
            Log.d(TAG, "Tıklama şu anda devre dışı");
            return; // Tıklama engellenmiş ise işlem yapma
        }

        // Çift tıklamayı önlemek için
        isClickEnabled = false;

        Log.d(TAG, "Seviye " + levelNumber + " kartına tıklandı");

        try {
            // Öncelikle seviyeyi kaydet
            preferenceManager.setCurrentLevel(languageId, levelNumber);

            // LevelActivity üzerinden çağırma
            if (context instanceof LevelActivity) {
                Log.d(TAG, "LevelActivity üzerinden QuizActivity başlatılıyor");
                ((LevelActivity) context).startQuizForLevel(levelNumber);

                // Gecikmeyle tıklama yeteneğini geri ver
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isClickEnabled = true;
                        Log.d(TAG, "Tıklama yeteneği tekrar aktif");
                    }
                }, 500);

                return; // Metodu burada sonlandır
            }

            // Doğrudan Intent ile başlatma (yedek yöntem)
            Log.d(TAG, "Doğrudan Intent ile QuizActivity başlatılıyor");
            Intent intent = new Intent(context, QuizActivity.class);

            // Fazladan FLAG_ACTIVITY_NEW_TASK ekleyelim
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Intent ekstralarını ayarla
            intent.putExtra("languageId", languageId);
            intent.putExtra("languageName", getLanguageName(languageId));
            intent.putExtra("level", levelNumber);

            // Ekstra quiz bilgileri
            intent.putExtra("totalWords", level.getTotalWords());
            intent.putExtra("correctAnswers", level.getCorrectAnswers());
            intent.putExtra("successRate", level.getSuccessRate());

            // Intent'i logla
            Log.d(TAG, "Intent oluşturuldu: " + intent.toString());
            Log.d(TAG, "Intent extras: languageId=" + languageId + ", level=" + levelNumber);

            // Aktiviteyi başlat
            context.startActivity(intent);
            Log.d(TAG, "startActivity çağrıldı");

            // Gecikmeyle tıklama yeteneğini geri ver
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClickEnabled = true;
                    Log.d(TAG, "Tıklama yeteneği tekrar aktif");
                }
            }, 500);

        } catch (Exception e) {
            Log.e(TAG, "Kart tıklama hatası: " + e.getMessage(), e);
            e.printStackTrace();
            Toast.makeText(context, "Seviye açılamadı: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            // Hata durumunda hemen tıklama engelini kaldır
            isClickEnabled = true;
        }
    }

    // Dil ID'sinden dil adını getiren yardımcı metod
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

    // Karta gradient arka plan uygulama
    private void setCardGradientBackground(CardView cardView, int startColor, int endColor) {
        try {
            GradientDrawable gradientDrawable = new GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    new int[] {startColor, endColor});
            gradientDrawable.setCornerRadius(cardView.getRadius());
            cardView.setBackground(gradientDrawable);
            Log.d(TAG, "Gradient background ayarlandı");
        } catch (Exception e) {
            Log.e(TAG, "setCardGradientBackground hatası: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        int size = levels != null ? levels.size() : 0;
        Log.d(TAG, "getItemCount: " + size);
        return size;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView levelNumber;
        TextView levelProgress;
        TextView starsTextView;
        ProgressBar progressBar;
        ImageView statusIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                Log.d(TAG, "ViewHolder constructor başladı");
                cardView = itemView.findViewById(R.id.cardView);
                // CardView'u tıklanabilir ve odaklanabilir yap
                cardView.setClickable(true);
                cardView.setFocusable(true);

                levelNumber = itemView.findViewById(R.id.levelNumber);
                levelProgress = itemView.findViewById(R.id.levelProgress);
                starsTextView = itemView.findViewById(R.id.starsTextView); // Yeni eklenen alan
                progressBar = itemView.findViewById(R.id.progressBar);
                statusIcon = itemView.findViewById(R.id.statusIcon);
            } catch (Exception e) {
                Log.e(TAG, "ViewHolder constructor hatası: " + e.getMessage(), e);
                e.printStackTrace();
            }
        }
    }
}