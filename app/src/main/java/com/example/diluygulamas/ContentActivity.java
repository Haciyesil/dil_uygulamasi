package com.example.diluygulamas;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.diluygulamas.adapter.ContentAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.diluygulamas.model.ContentItem;
import com.example.diluygulamas.util.JsonHelper;
import com.example.diluygulamas.util.NumberTranslator;
import java.util.List;

public class ContentActivity extends AppCompatActivity {
    private static final String TAG = "ContentActivity";
    private RecyclerView contentRecyclerView;
    private ContentAdapter contentAdapter;
    private TextView titleTextView;
    // Renk oluşturucu bileşenleri
    private View colorCreatorCard;
    private View colorCreatorPreview;
    private TextView colorCreatorName;
    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    private TextView redValueText, greenValueText, blueValueText;
    // Hesap makinesi bileşenleri
    private View calculatorCard;
    private EditText firstNumberInput, secondNumberInput;
    private Spinner operationSpinner;
    private Button calculateButton;
    private TextView resultNumberText, resultNumberNameText;
    // Renk değerleri
    private int currentRed = 255;
    private int currentGreen = 165;
    private int currentBlue = 0;
    // Seçilen dil
    private int languageId = 1;
    // Renk isimleri için dile özgü değişken
    private String[][] colorNames;
    // Renklerin RGB değerleri
    private int[][] colorRgbValues = {
            {255, 0, 0},      // Kırmızı
            {0, 0, 255},      // Mavi
            {0, 255, 0},      // Yeşil
            {255, 255, 0},    // Sarı
            {165, 42, 42},    // Kahverengi
            {255, 255, 255},  // Beyaz
            {0, 0, 0},        // Siyah
            {255, 165, 0},    // Turuncu
            {128, 0, 128},    // Mor
            {135, 206, 250},  // Açık Mavi
            {0, 0, 139},      // Koyu Mavi
            {128, 128, 128},  // Gri
            {255, 192, 203},  // Pembe
            {255, 215, 0}     // Altın Sarısı
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        initViews();

        // Intent'ten gelen değerleri al
        languageId = getIntent().getIntExtra("languageId", 1);
        String languageName = getIntent().getStringExtra("languageName");
        String category = getIntent().getStringExtra("category");
        String categoryName = getCategoryDisplayName(category);
        titleTextView.setText(languageName + " - " + categoryName);

        // Dile göre renk isimlerini ayarla
        setupColorNamesForLanguage(languageId);

        // Kategoriye göre uygun kartı göster
        if ("colors".equals(category)) {
            colorCreatorCard.setVisibility(View.VISIBLE);
            calculatorCard.setVisibility(View.GONE);
            setupColorCreator();
        } else if ("numbers".equals(category)) {
            colorCreatorCard.setVisibility(View.GONE);
            calculatorCard.setVisibility(View.VISIBLE);
            setupCalculator();
        } else {
            colorCreatorCard.setVisibility(View.GONE);
            calculatorCard.setVisibility(View.GONE);
        }

        loadContent(languageId, category);
    }

    private void initViews() {
        titleTextView = findViewById(R.id.contentTitle);
        contentRecyclerView = findViewById(R.id.contentRecyclerView);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Renk oluşturucu kartını bul
        colorCreatorCard = findViewById(R.id.colorCreatorCard);

        // Renk oluşturucu elemanlarını başlat
        colorCreatorPreview = findViewById(R.id.colorCreatorPreview);
        colorCreatorName = findViewById(R.id.colorCreatorName);
        redSeekBar = findViewById(R.id.redSeekBar);
        greenSeekBar = findViewById(R.id.greenSeekBar);
        blueSeekBar = findViewById(R.id.blueSeekBar);
        redValueText = findViewById(R.id.redValueText);
        greenValueText = findViewById(R.id.greenValueText);
        blueValueText = findViewById(R.id.blueValueText);

        // Hesap makinesi kartını bul
        calculatorCard = findViewById(R.id.calculatorCard);

        // Hesap makinesi elemanlarını başlat
        firstNumberInput = findViewById(R.id.firstNumberInput);
        secondNumberInput = findViewById(R.id.secondNumberInput);
        operationSpinner = findViewById(R.id.operationSpinner);
        calculateButton = findViewById(R.id.calculateButton);
        resultNumberText = findViewById(R.id.resultNumberText);
        resultNumberNameText = findViewById(R.id.resultNumberNameText);
    }

    private void setupColorNamesForLanguage(int languageId) {
        // Varsayılan olarak Türkmence renk isimleri
        String[][] defaultColorNames = {
                {"Gyzyl", "Kırmızı"},
                {"Gök", "Mavi"},
                {"Ýaşyl", "Yeşil"},
                {"Sary", "Sarı"},
                {"Mele", "Kahverengi"},
                {"Ak", "Beyaz"},
                {"Gara", "Siyah"},
                {"Mämişi", "Turuncu"},
                {"Benewşe", "Mor"},
                {"Açyk gök", "Açık Mavi"},
                {"Goýy gök", "Koyu Mavi"},
                {"Çal", "Gri"},
                {"Pembe", "Pembe"},
                {"Ýagtylyk", "Altın Sarısı"}
        };

        // Filistince renk isimleri
        String[][] palestinianColorNames = {
                {"أحمر", "Kırmızı"},
                {"أزرق", "Mavi"},
                {"أخضر", "Yeşil"},
                {"أصفر", "Sarı"},
                {"بني", "Kahverengi"},
                {"أبيض", "Beyaz"},
                {"أسود", "Siyah"},
                {"برتقالي", "Turuncu"},
                {"بنفسجي", "Mor"},
                {"أزرق فاتح", "Açık Mavi"},
                {"أزرق داكن", "Koyu Mavi"},
                {"رمادي", "Gri"},
                {"وردي", "Pembe"},
                {"ذهبي", "Altın Sarısı"}
        };

        // İngilizce renk isimleri
        String[][] englishColorNames = {
                {"Red", "Kırmızı"},
                {"Blue", "Mavi"},
                {"Green", "Yeşil"},
                {"Yellow", "Sarı"},
                {"Brown", "Kahverengi"},
                {"White", "Beyaz"},
                {"Black", "Siyah"},
                {"Orange", "Turuncu"},
                {"Purple", "Mor"},
                {"Light Blue", "Açık Mavi"},
                {"Dark Blue", "Koyu Mavi"},
                {"Gray", "Gri"},
                {"Pink", "Pembe"},
                {"Golden", "Altın Sarısı"}
        };

        // Dile göre renk isimlerini seç
        switch (languageId) {
            case 2: // Filistince
                colorNames = palestinianColorNames;
                break;
            case 3: // İngilizce
                colorNames = englishColorNames;
                break;
            default: // Varsayılan Türkmence
                colorNames = defaultColorNames;
                break;
        }
    }

    private void setupColorCreator() {
        // Başlangıç değerlerini ayarla
        redSeekBar.setProgress(currentRed);
        greenSeekBar.setProgress(currentGreen);
        blueSeekBar.setProgress(currentBlue);
        redValueText.setText(String.valueOf(currentRed));
        greenValueText.setText(String.valueOf(currentGreen));
        blueValueText.setText(String.valueOf(currentBlue));
        updateColorCreatorPreview();

        // Seekbar dinleyicileri
        SeekBar.OnSeekBarChangeListener seekBarListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getId() == R.id.redSeekBar) {
                    currentRed = progress;
                    redValueText.setText(String.valueOf(progress));
                } else if (seekBar.getId() == R.id.greenSeekBar) {
                    currentGreen = progress;
                    greenValueText.setText(String.valueOf(progress));
                } else if (seekBar.getId() == R.id.blueSeekBar) {
                    currentBlue = progress;
                    blueValueText.setText(String.valueOf(progress));
                }
                updateColorCreatorPreview();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        redSeekBar.setOnSeekBarChangeListener(seekBarListener);
        greenSeekBar.setOnSeekBarChangeListener(seekBarListener);
        blueSeekBar.setOnSeekBarChangeListener(seekBarListener);
    }

    private void setupCalculator() {
        // İşlem spinner'ını ayarla
        String[] operations = {"+", "-", "×", "÷"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, operations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        operationSpinner.setAdapter(adapter);

        // Hesaplama butonu dinleyicisi
        calculateButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(firstNumberInput.getText()) || TextUtils.isEmpty(secondNumberInput.getText())) {
                Toast.makeText(ContentActivity.this, "Lütfen iki sayı girin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                int num1 = Integer.parseInt(firstNumberInput.getText().toString());
                int num2 = Integer.parseInt(secondNumberInput.getText().toString());
                int result = 0;
                String operation = operationSpinner.getSelectedItem().toString();

                switch (operation) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "×":
                        result = num1 * num2;
                        break;
                    case "÷":
                        if (num2 == 0) {
                            Toast.makeText(ContentActivity.this, "Sıfıra bölme hatası!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        result = num1 / num2;
                        break;
                }

                // Sonucu göster
                resultNumberText.setText(String.valueOf(result));

                // NumberTranslator kullanarak sayı adını bul ve göster
                String[] numberName;
                if (languageId == 2) {
                    // Filistince sayı çevirisi için özel metot kullanın
                    numberName = getArabicNumberName(result);
                } else if (languageId == 3) {
                    // İngilizce sayı çevirisi için özel metot kullanın
                    numberName = getEnglishNumberName(result);
                } else {
                    // Varsayılan Türkmence
                    numberName = NumberTranslator.getNumberName(result);
                }
                resultNumberNameText.setText(numberName[0] + " (" + numberName[1] + ")");
            } catch (NumberFormatException e) {
                Toast.makeText(ContentActivity.this, "Geçerli sayılar girin", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Arapça sayı adları için yardımcı metot
    private String[] getArabicNumberName(int number) {
        // Bu metot basit bir örnek, gerçek uygulamada daha kapsamlı olmalıdır
        String arabicName = "";
        String turkishName = "";

        if (number >= 0 && number <= 20) {
            switch (number) {
                case 0: arabicName = "صفر"; turkishName = "Sıfır"; break;
                case 1: arabicName = "واحد"; turkishName = "Bir"; break;
                case 2: arabicName = "اثنان"; turkishName = "İki"; break;
                case 3: arabicName = "ثلاثة"; turkishName = "Üç"; break;
                case 4: arabicName = "أربعة"; turkishName = "Dört"; break;
                case 5: arabicName = "خمسة"; turkishName = "Beş"; break;
                case 6: arabicName = "ستة"; turkishName = "Altı"; break;
                case 7: arabicName = "سبعة"; turkishName = "Yedi"; break;
                case 8: arabicName = "ثمانية"; turkishName = "Sekiz"; break;
                case 9: arabicName = "تسعة"; turkishName = "Dokuz"; break;
                case 10: arabicName = "عشرة"; turkishName = "On"; break;
                case 11: arabicName = "أحد عشر"; turkishName = "On bir"; break;
                case 12: arabicName = "اثنا عشر"; turkishName = "On iki"; break;
                case 13: arabicName = "ثلاثة عشر"; turkishName = "On üç"; break;
                case 14: arabicName = "أربعة عشر"; turkishName = "On dört"; break;
                case 15: arabicName = "خمسة عشر"; turkishName = "On beş"; break;
                case 16: arabicName = "ستة عشر"; turkishName = "On altı"; break;
                case 17: arabicName = "سبعة عشر"; turkishName = "On yedi"; break;
                case 18: arabicName = "ثمانية عشر"; turkishName = "On sekiz"; break;
                case 19: arabicName = "تسعة عشر"; turkishName = "On dokuz"; break;
                case 20: arabicName = "عشرون"; turkishName = "Yirmi"; break;
            }
        } else if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;
            String tensName = "";

            switch (tens) {
                case 2: tensName = "عشرون"; break;
                case 3: tensName = "ثلاثون"; break;
                case 4: tensName = "أربعون"; break;
                case 5: tensName = "خمسون"; break;
                case 6: tensName = "ستون"; break;
                case 7: tensName = "سبعون"; break;
                case 8: tensName = "ثمانون"; break;
                case 9: tensName = "تسعون"; break;
            }

            if (ones == 0) {
                arabicName = tensName;
                turkishName = "..."; // Türkçe karşılığını ekleyin
            } else {
                String[] onesName = getArabicNumberName(ones);
                arabicName = onesName[0] + " و " + tensName;
                turkishName = "..."; // Türkçe karşılığını ekleyin
            }
        } else {
            arabicName = "كبير جدا";
            turkishName = "Çok büyük";
        }

        return new String[]{arabicName, turkishName};
    }

    // İngilizce sayı adları için yardımcı metot
    private String[] getEnglishNumberName(int number) {
        // Bu metot basit bir örnek, gerçek uygulamada daha kapsamlı olmalıdır
        String englishName = "";
        String turkishName = "";

        if (number >= 0 && number <= 20) {
            switch (number) {
                case 0: englishName = "Zero"; turkishName = "Sıfır"; break;
                case 1: englishName = "One"; turkishName = "Bir"; break;
                case 2: englishName = "Two"; turkishName = "İki"; break;
                case 3: englishName = "Three"; turkishName = "Üç"; break;
                case 4: englishName = "Four"; turkishName = "Dört"; break;
                case 5: englishName = "Five"; turkishName = "Beş"; break;
                case 6: englishName = "Six"; turkishName = "Altı"; break;
                case 7: englishName = "Seven"; turkishName = "Yedi"; break;
                case 8: englishName = "Eight"; turkishName = "Sekiz"; break;
                case 9: englishName = "Nine"; turkishName = "Dokuz"; break;
                case 10: englishName = "Ten"; turkishName = "On"; break;
                case 11: englishName = "Eleven"; turkishName = "On bir"; break;
                case 12: englishName = "Twelve"; turkishName = "On iki"; break;
                case 13: englishName = "Thirteen"; turkishName = "On üç"; break;
                case 14: englishName = "Fourteen"; turkishName = "On dört"; break;
                case 15: englishName = "Fifteen"; turkishName = "On beş"; break;
                case 16: englishName = "Sixteen"; turkishName = "On altı"; break;
                case 17: englishName = "Seventeen"; turkishName = "On yedi"; break;
                case 18: englishName = "Eighteen"; turkishName = "On sekiz"; break;
                case 19: englishName = "Nineteen"; turkishName = "On dokuz"; break;
                case 20: englishName = "Twenty"; turkishName = "Yirmi"; break;
            }
        } else if (number < 100) {
            int tens = number / 10;
            int ones = number % 10;
            String tensName = "";

            switch (tens) {
                case 2: tensName = "Twenty"; break;
                case 3: tensName = "Thirty"; break;
                case 4: tensName = "Forty"; break;
                case 5: tensName = "Fifty"; break;
                case 6: tensName = "Sixty"; break;
                case 7: tensName = "Seventy"; break;
                case 8: tensName = "Eighty"; break;
                case 9: tensName = "Ninety"; break;
            }

            if (ones == 0) {
                englishName = tensName;
                turkishName = "..."; // Türkçe karşılığını ekleyin
            } else {
                String[] onesName = getEnglishNumberName(ones);
                englishName = tensName + "-" + onesName[0].toLowerCase();
                turkishName = "..."; // Türkçe karşılığını ekleyin
            }
        } else {
            englishName = "Too large";
            turkishName = "Çok büyük";
        }

        return new String[]{englishName, turkishName};
    }

    private void updateColorCreatorPreview() {
        int color = Color.rgb(currentRed, currentGreen, currentBlue);
        colorCreatorPreview.setBackgroundColor(color);
        // En yakın rengi bul ve adını güncelle
        String[] nearestColor = findNearestColor(color);
        colorCreatorName.setText(nearestColor[0] + " (" + nearestColor[1] + ")");
    }

    private String[] findNearestColor(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        int minDistance = Integer.MAX_VALUE;
        int closestIndex = 0;

        // En yakın rengi bul
        for (int i = 0; i < colorRgbValues.length; i++) {
            int[] rgb = colorRgbValues[i];
            int distance = calculateColorDistance(red, green, blue, rgb[0], rgb[1], rgb[2]);
            if (distance < minDistance) {
                minDistance = distance;
                closestIndex = i;
            }
        }

        return colorNames[closestIndex];
    }

    // İki renk arasındaki mesafeyi hesapla
    private int calculateColorDistance(int r1, int g1, int b1, int r2, int g2, int b2) {
        return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
    }

    private void loadContent(int languageId, String category) {
        // Intent'ten dil adı ve seviye bilgisini al
        String languageName = getIntent().getStringExtra("languageName");
        int level = getIntent().getIntExtra("level", 1); // Varsayılan seviye 1

        // Hata ayıklama için log ekleyelim
        Log.d(TAG, "loadContent: languageId=" + languageId + ", category=" + category + ", level=" + level);

        // JsonHelper kullanarak içeriği yükle
        JsonHelper jsonHelper = new JsonHelper(this);
        List<ContentItem> contentItems;

        if (category.equals("words")) {
            // Kelimeler için seviye bilgisini kullan
            contentItems = jsonHelper.getContentByCategory(languageId, category, level);

            // Hata ayıklama için kelime sayısını logla
            Log.d(TAG, "Yüklenen kelime sayısı: " + contentItems.size());

            // Eğer kelimeler yüklenemezse, boş görünüm göster
            if (contentItems.isEmpty()) {
                // Boş görünüm için bir TextView ekle
                TextView emptyView = new TextView(this);
                emptyView.setText("Bu seviye için kelimeler bulunamadı. Lütfen JSON dosyasını kontrol edin.");
                emptyView.setTextSize(16);
                emptyView.setGravity(android.view.Gravity.CENTER);
                emptyView.setPadding(32, 100, 32, 32);

                // RecyclerView'ı gizle ve boş görünümü ekle
                contentRecyclerView.setVisibility(View.GONE);
                ViewGroup parent = (ViewGroup) contentRecyclerView.getParent();
                parent.addView(emptyView);

                Toast.makeText(this, "Kelimeler yüklenemedi.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Kelimeler yüklenemedi: languageId=" + languageId + ", level=" + level);
                return;
            }

            // Her seviye için maksimum 25 kelime göster
            if (contentItems.size() > 25) {
                contentItems = contentItems.subList(0, 25);
            }

            // Başlığı güncelle
            if (languageName != null) {
                titleTextView.setText(languageName + " - " + getCategoryDisplayName(category) + " (Seviye " + level + ")");
            } else {
                titleTextView.setText(getCategoryDisplayName(category) + " (Seviye " + level + ")");
            }
        } else {
            // Diğer kategoriler için seviye kullanma
            contentItems = jsonHelper.getContentByCategory(languageId, category);
        }

        // Adapter ile RecyclerView'a bağla
        contentAdapter = new ContentAdapter(contentItems, category);
        contentRecyclerView.setAdapter(contentAdapter);
    }

    private String getCategoryDisplayName(String category) {
        switch (category) {
            case "colors": return "Renkler";
            case "numbers": return "Sayılar";
            case "seasons": return "Mevsimler";
            case "words": return "Kelimeler";
            default: return category;
        }
    }
}