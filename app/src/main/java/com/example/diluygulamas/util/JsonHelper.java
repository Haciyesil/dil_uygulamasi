package com.example.diluygulamas.util;
import android.content.Context;
import android.util.Log;
import com.example.diluygulamas.model.ContentItem;
import com.example.diluygulamas.model.Language;
import com.example.diluygulamas.model.Word;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JsonHelper {
    private static final String TAG = "JsonHelper";
    private Context context;

    public JsonHelper(Context context) {
        this.context = context;
    }

    public String readJsonFile(String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            Log.d(TAG, "JSON dosyası başarıyla yüklendi: " + fileName);
        } catch (IOException ex) {
            Log.e(TAG, "Error loading JSON file: " + fileName, ex);
            return null;
        }
        return json;
    }

    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
            Log.d(TAG, "JSON dosyası başarıyla yüklendi: " + fileName);
        } catch (IOException ex) {
            Log.e(TAG, "Error loading JSON file: " + fileName, ex);
            return null;
        }
        return json;
    }

    public static List<Language> getLanguagesFromJson(Context context) {
        List<Language> languages = new ArrayList<>();
        // Varsayılan dilleri oluştur
        languages.add(new Language(1, "Türkmence", "tm", true, "flag_turkmenistan"));
        languages.add(new Language(2, "Filistince", "ps", true, "flag_palestine"));
        languages.add(new Language(3, "İngilizce", "en", true, "flag_english"));
        languages.add(new Language(4, "Almanca", "de", true, "flag_germany"));
        languages.add(new Language(5, "Fransızca", "fr", true, "flag_france"));
        languages.add(new Language(6, "İspanyolca", "es", true, "flag_spain"));
        languages.add(new Language(7, "İtalyanca", "it", true, "flag_italy"));
        languages.add(new Language(8, "Rusça", "ru", true, "flag_russia"));
        languages.add(new Language(9, "Japonca", "ja", true, "flag_japan"));
        try {
            String jsonString = loadJSONFromAsset(context, "languages.json");
            if (jsonString == null) {
                Log.e(TAG, "languages.json file not found, using default languages");
                return languages;
            }
            languages.clear();
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray languagesArray = jsonObject.getJSONArray("languages");
            Log.d(TAG, "JSON'dan okunan dil sayısı: " + languagesArray.length());
            for (int i = 0; i < languagesArray.length(); i++) {
                JSONObject languageObject = languagesArray.getJSONObject(i);
                int id = languageObject.getInt("id");
                String name = languageObject.getString("name");
                String code = languageObject.getString("code");
                boolean isUnlocked = languageObject.getBoolean("isUnlocked");
                String flagName = null;
                if (languageObject.has("flagName")) {
                    flagName = languageObject.getString("flagName");
                } else {
                    flagName = "flag_" + code.toLowerCase();
                }
                Language language = new Language(id, name, code, isUnlocked, flagName);
                languages.add(language);
                Log.d(TAG, "Dil eklendi: " + id + " - " + name + " - " + flagName);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing languages.json", e);
        }
        return languages;
    }

    // Belirli bir kategori ve seviye için içerik getiren metod
    public List<ContentItem> getContentByCategory(int languageId, String category, int level) {
        List<ContentItem> contentItems = new ArrayList<>();
        // Dosya adını oluştur
        String fileName;
        if (category.equals("words")) {
            // Kelimeler için özel dosya adı formatı
            if (languageId == 1) {
                fileName = "turkmenish_words.json";
            } else if (languageId == 2) {
                fileName = "palestine_words.json";
            } else {
                fileName = getLanguageCode(languageId) + "_words.json";
            }
        } else {
            // Diğer kategoriler için standart format
            fileName = getLanguageCode(languageId) + "_" + category + ".json";
        }
        Log.d(TAG, "Okunacak dosya: " + fileName + ", dil: " + languageId + ", seviye: " + level);
        try {
            String jsonString = readJsonFile(fileName);
            if (jsonString == null) {
                Log.e(TAG, "Content file not found: " + fileName);
                // Eğer dil dosyası yoksa ve Türkmence değilse, Türkmence içeriği göster
                if (languageId != 1) {
                    Log.d(TAG, languageId + " için " + category + " içeriği bulunamadı, Türkmence içerik gösterilecek");
                    return getContentByCategory(1, category, level);
                }
                return contentItems;
            }
            // JSON formatına göre işleme yap
            if (category.equals("words")) {
                // Kelimeler için JSON formatını analiz et
                try {
                    // Önce Türkmence formatını kontrol et
                    JSONObject jsonObject = new JSONObject(jsonString);
                    String levelKey = "level" + level;
                    if (jsonObject.has(levelKey)) {
                        // Türkmence seviye bazlı format
                        JSONArray levelWords = jsonObject.getJSONArray(levelKey);
                        Log.d(TAG, "Türkmence formatı: " + levelKey + " anahtarında " + levelWords.length() + " kelime var");

                        for (int i = 0; i < levelWords.length(); i++) {
                            JSONObject wordObj = levelWords.getJSONObject(i);
                            ContentItem contentItem = new ContentItem();

                            // Word ve translation alanlarını oku
                            contentItem.setOriginalText(wordObj.getString("word"));
                            contentItem.setTranslatedText(wordObj.getString("translation"));
                            contentItem.setLanguageId(languageId);
                            contentItem.setLevel(level);

                            // Telaffuz bilgisini al (eğer varsa)
                            if (wordObj.has("pronunciation")) {
                                contentItem.setPronunciation(wordObj.getString("pronunciation"));
                            }

                            // Telaffuz yoksa options'dan al
                            if ((contentItem.getPronunciation() == null || contentItem.getPronunciation().isEmpty())
                                    && wordObj.has("options")) {
                                JSONArray options = wordObj.getJSONArray("options");
                                if (options.length() > 0) {
                                    contentItem.setPronunciation(options.getString(0));
                                }
                            }

                            contentItems.add(contentItem);
                        }
                    } else if (jsonString.trim().startsWith("[")) {
                        // Doğrudan JSON dizisi formatı (eski format)
                        JSONArray jsonArray = new JSONArray(jsonString);
                        Log.d(TAG, "Eski JSON dizi formatı, toplam eleman: " + jsonArray.length());
                        contentItems = loadWordsFromJsonArray(jsonArray, languageId, level);
                    } else if (jsonObject.has("words")) {
                        // JSON nesne formatı - "words" alanı içinde dizi olabilir (eski format)
                        JSONArray wordsArray = jsonObject.getJSONArray("words");
                        Log.d(TAG, "Eski JSON nesne formatı, 'words' alanında toplam eleman: " + wordsArray.length());
                        contentItems = loadWordsFromJsonArray(wordsArray, languageId, level);
                    } else {
                        Log.e(TAG, "Desteklenmeyen JSON formatı");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "JSON işleme hatası: " + e.getMessage(), e);
                }
            } else {
                // Standart içerik yükleme (renkler, sayılar, mevsimler vs.)
                contentItems = loadStandardContent(jsonString, languageId);
            }
            Log.d(TAG, "Loaded " + contentItems.size() + " items from " + fileName + " for level " + level);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing content file: " + fileName, e);
        }
        return contentItems;
    }

    // JSON dizisinden seviyeye göre kelimeleri yükle (eski format için)
    private List<ContentItem> loadWordsFromJsonArray(JSONArray jsonArray, int languageId, int level) throws JSONException {
        List<ContentItem> contentItems = new ArrayList<>();
        int wordCount = 0;
        // İki yaklaşım deneyelim: seviye filtresi veya indeks bazlı
        // 1. Yaklaşım: level alanına göre filtreleme
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject wordObj = jsonArray.getJSONObject(i);
            if (wordObj.has("level") && wordObj.getInt("level") == level) {
                // Dil kontrolü
                if (!wordObj.has("languageId") || wordObj.getInt("languageId") == languageId) {
                    ContentItem contentItem = createContentItemFromJson(wordObj, languageId, level);
                    contentItems.add(contentItem);
                    wordCount++;
                }
            }
        }
        // Eğer level alanına göre hiç kelime bulunamazsa, indeks bazlı yaklaşımı deneyelim
        if (contentItems.isEmpty()) {
            Log.d(TAG, "Seviye " + level + " için kelime bulunamadı, indeks bazlı yaklaşım deneniyor");
            int startIndex = (level - 1) * 25;
            int endIndex = Math.min(startIndex + 25, jsonArray.length());
            for (int i = startIndex; i < endIndex; i++) {
                if (i < jsonArray.length()) {
                    JSONObject wordObj = jsonArray.getJSONObject(i);
                    ContentItem contentItem = createContentItemFromJson(wordObj, languageId, level);
                    contentItems.add(contentItem);
                    wordCount++;
                }
            }
        }
        Log.d(TAG, "Dil: " + languageId + ", Seviye: " + level + " için " + wordCount + " kelime yüklendi");
        return contentItems;
    }

    // JSON nesnesinden ContentItem oluştur (eski format için)
    private ContentItem createContentItemFromJson(JSONObject wordObj, int languageId, int level) throws JSONException {
        ContentItem contentItem = new ContentItem();
        // originalWord ve translatedWord alanlarını al
        contentItem.setOriginalText(wordObj.getString("originalWord"));
        contentItem.setTranslatedText(wordObj.getString("translatedWord"));
        // Dil ve seviye bilgilerini ayarla
        contentItem.setLanguageId(languageId);
        contentItem.setLevel(level);
        // Telaffuz bilgisini al (eğer varsa)
        if (wordObj.has("pronunciation")) {
            contentItem.setPronunciation(wordObj.getString("pronunciation"));
        }
        // Eğer telaffuz alanı yoksa, options dizisinden ilk öğeyi telaffuz olarak kullanabilirsiniz
        if ((contentItem.getPronunciation() == null || contentItem.getPronunciation().isEmpty())
                && wordObj.has("options")) {
            JSONArray options = wordObj.getJSONArray("options");
            if (options.length() > 0) {
                contentItem.setPronunciation(options.getString(0));
            }
        }
        return contentItem;
    }

    // Geriye uyumluluk için eski metodunu koru
    public List<ContentItem> getContentByCategory(int languageId, String category) {
        // Varsayılan seviye 1 olarak çağır
        return getContentByCategory(languageId, category, 1);
    }

    // Standart içerik yükleme metodu (renkler, sayılar, mevsimler)
    private List<ContentItem> loadStandardContent(String jsonString, int languageId) throws JSONException {
        List<ContentItem> contentItems = new ArrayList<>();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray itemsArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < itemsArray.length(); i++) {
            JSONObject item = itemsArray.getJSONObject(i);
            ContentItem contentItem = new ContentItem();
            contentItem.setOriginalText(item.optString("original", ""));
            contentItem.setTranslatedText(item.optString("translated", ""));
            contentItem.setPronunciation(item.optString("pronunciation", ""));
            contentItem.setImageUrl(item.optString("imageUrl", ""));
            // Dil ID'sini ayarla
            contentItem.setLanguageId(languageId);
            // Mevsim ayları bilgisini ekle
            if (item.has("months")) {
                contentItem.setMonths(item.optString("months", ""));
            }
            // RGB değerlerini de oku (eğer varsa)
            if (item.has("red")) contentItem.setRed(item.optInt("red", 0));
            if (item.has("green")) contentItem.setGreen(item.optInt("green", 0));
            if (item.has("blue")) contentItem.setBlue(item.optInt("blue", 0));
            contentItems.add(contentItem);
        }
        return contentItems;
    }

    // Eski metod için geriye uyumluluk
    private List<ContentItem> loadStandardContent(String jsonString) throws JSONException {
        return loadStandardContent(jsonString, 1); // Varsayılan olarak Türkmence
    }

    // Language ID'den language code'u al
    private String getLanguageCode(int languageId) {
        switch (languageId) {
            case 1: return "turkmen";
            case 2: return "palestine";
            case 3: return "english";
            case 4: return "german";
            case 5: return "french";
            case 6: return "spanish";
            case 7: return "italian";
            case 8: return "russian";
            case 9: return "japanese";
            default: return "turkmen";
        }
    }

    // QuizActivity için kelime listesini getiren metod - TÜM FORMATLAR İÇİN TEK METOD
    public List<Word> getWordsForLanguage(int languageId) {
        Log.d(TAG, "getWordsForLanguage çağrıldı: languageId=" + languageId);
        List<Word> result = new ArrayList<>();

        try {
            // Dosya adını belirle
            String fileName;
            if (languageId == 1) {
                fileName = "turkmenish_words.json";
            } else if (languageId == 2) {
                fileName = "palestine_words.json";
            } else {
                fileName = getLanguageCode(languageId) + "_words.json";
            }

            Log.d(TAG, "Okunacak dosya: " + fileName);
            String json = readJsonFile(fileName);

            if (json == null || json.isEmpty()) {
                Log.e(TAG, fileName + " dosyası boş veya okunamadı");
                return result;
            }

            // Türkmence formatını öncelikle kontrol et
            if (json.trim().startsWith("{")) {
                JSONObject jsonObj = new JSONObject(json);

                // Seviye anahtarlarını kontrol et
                boolean isTurkmenFormat = false;
                for (int i = 1; i <= 20; i++) {
                    String levelKey = "level" + i;
                    if (jsonObj.has(levelKey)) {
                        isTurkmenFormat = true;
                        JSONArray levelWords = jsonObj.getJSONArray(levelKey);

                        for (int j = 0; j < levelWords.length(); j++) {
                            JSONObject wordObj = levelWords.getJSONObject(j);

                            Word word = new Word();
                            word.setId((i - 1) * 25 + j + 1); // ID oluştur
                            word.setOriginalWord(wordObj.getString("word"));
                            word.setTranslatedWord(wordObj.getString("translation"));
                            word.setLanguageId(languageId);
                            word.setLevel(i); // Seviyeyi ayarla

                            // Seçenekleri ekle
                            if (wordObj.has("options")) {
                                JSONArray optionsArray = wordObj.getJSONArray("options");
                                String[] options = new String[optionsArray.length()];
                                for (int k = 0; k < optionsArray.length(); k++) {
                                    options[k] = optionsArray.getString(k);
                                }
                                word.setOptions(options);
                            }

                            result.add(word);
                        }
                    }
                }

                if (isTurkmenFormat) {
                    Log.d(TAG, "Türkmence formatında " + result.size() + " kelime yüklendi");
                } else if (jsonObj.has("words")) {
                    // Eski format words dizisi
                    Log.d(TAG, "Eski words dizisi formatı tespit edildi");
                    JSONArray wordsArray = jsonObj.getJSONArray("words");

                    for (int i = 0; i < wordsArray.length(); i++) {
                        JSONObject wordObj = wordsArray.getJSONObject(i);

                        if (!wordObj.has("languageId") || wordObj.getInt("languageId") == languageId) {
                            Word word = new Word();
                            word.setId(wordObj.optInt("id", i + 1));
                            word.setOriginalWord(wordObj.getString("originalWord"));
                            word.setTranslatedWord(wordObj.getString("translatedWord"));
                            word.setLanguageId(languageId);

                            // Level bilgisini ekle
                            if (wordObj.has("level")) {
                                word.setLevel(wordObj.getInt("level"));
                            } else {
                                word.setLevel((i / 25) + 1); // Her 25 kelime bir seviye
                            }

                            // Kategori bilgisini ekle
                            if (wordObj.has("category")) {
                                word.setCategory(wordObj.getString("category"));
                            }

                            // Seçenekleri ekle
                            if (wordObj.has("options")) {
                                JSONArray optionsArray = wordObj.getJSONArray("options");
                                String[] options = new String[optionsArray.length()];
                                for (int j = 0; j < optionsArray.length(); j++) {
                                    options[j] = optionsArray.getString(j);
                                }
                                word.setOptions(options);
                            }

                            result.add(word);
                        }
                    }

                    Log.d(TAG, "Eski words dizisi formatından " + result.size() + " kelime yüklendi");
                }
            } else if (json.trim().startsWith("[")) {
                // Dizi formatı (eski Filistince)
                Log.d(TAG, "Eski dizi formatı tespit edildi");
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject wordObj = jsonArray.getJSONObject(i);

                    if (!wordObj.has("languageId") || wordObj.getInt("languageId") == languageId) {
                        Word word = new Word();
                        word.setId(wordObj.optInt("id", i + 1));
                        word.setOriginalWord(wordObj.getString("originalWord"));
                        word.setTranslatedWord(wordObj.getString("translatedWord"));
                        word.setLanguageId(languageId);

                        // Level bilgisini ekle
                        if (wordObj.has("level")) {
                            word.setLevel(wordObj.getInt("level"));
                        } else {
                            word.setLevel((i / 25) + 1); // Her 25 kelime bir seviye
                        }

                        // Seçenekleri ekle
                        if (wordObj.has("options")) {
                            JSONArray optionsArray = wordObj.getJSONArray("options");
                            String[] options = new String[optionsArray.length()];
                            for (int j = 0; j < optionsArray.length(); j++) {
                                options[j] = optionsArray.getString(j);
                            }
                            word.setOptions(options);
                        }

                        result.add(word);
                    }
                }

                Log.d(TAG, "Eski dizi formatından " + result.size() + " kelime yüklendi");
            }

            Log.d(TAG, languageId + " dili için toplam " + result.size() + " kelime yüklendi");

        } catch (Exception e) {
            Log.e(TAG, "getWordsForLanguage hatası: " + e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    // Belirli bir seviye için kelimeleri getiren metod
    public List<Word> getWordsForLanguage(int languageId, int level) {
        Log.d(TAG, "getWordsForLanguage çağrıldı: languageId=" + languageId + ", level=" + level);
        List<Word> result = new ArrayList<>();

        try {
            // Dosya adını belirle
            String fileName;
            if (languageId == 1) {
                fileName = "turkmenish_words.json";
            } else if (languageId == 2) {
                fileName = "palestine_words.json";
            } else {
                fileName = getLanguageCode(languageId) + "_words.json";
            }

            Log.d(TAG, "Okunacak dosya: " + fileName);
            String json = readJsonFile(fileName);

            if (json == null || json.isEmpty()) {
                Log.e(TAG, fileName + " dosyası boş veya okunamadı");
                return result;
            }

            // Türkmence formatını öncelikle kontrol et
            if (json.trim().startsWith("{")) {
                JSONObject jsonObj = new JSONObject(json);
                String levelKey = "level" + level;

                if (jsonObj.has(levelKey)) {
                    // Türkmence seviye bazlı format
                    JSONArray levelWords = jsonObj.getJSONArray(levelKey);
                    Log.d(TAG, "Türkmence formatı, " + levelKey + " içinde " + levelWords.length() + " kelime bulundu");

                    for (int i = 0; i < levelWords.length(); i++) {
                        JSONObject wordObj = levelWords.getJSONObject(i);

                        Word word = new Word();
                        word.setId(i + 1);
                        word.setOriginalWord(wordObj.getString("word"));
                        word.setTranslatedWord(wordObj.getString("translation"));
                        word.setLanguageId(languageId);
                        word.setLevel(level);

                        // Seçenekleri ekle
                        if (wordObj.has("options")) {
                            JSONArray optionsArray = wordObj.getJSONArray("options");
                            String[] options = new String[optionsArray.length()];
                            for (int j = 0; j < optionsArray.length(); j++) {
                                options[j] = optionsArray.getString(j);
                            }
                            word.setOptions(options);
                        }

                        result.add(word);
                    }
                } else if (jsonObj.has("words")) {
                    // Eski format words dizisi - seviyeye göre filtrele
                    Log.d(TAG, "Eski words dizisi formatı, seviye " + level + " için filtreleniyor");
                    JSONArray wordsArray = jsonObj.getJSONArray("words");

                    for (int i = 0; i < wordsArray.length(); i++) {
                        JSONObject wordObj = wordsArray.getJSONObject(i);

                        int wordLevel = level;
                        if (wordObj.has("level")) {
                            wordLevel = wordObj.getInt("level");
                        } else {
                            wordLevel = (i / 25) + 1; // Her 25 kelime bir seviye
                        }

                        if (wordLevel == level && (!wordObj.has("languageId") || wordObj.getInt("languageId") == languageId)) {
                            Word word = new Word();
                            word.setId(wordObj.optInt("id", i + 1));
                            word.setOriginalWord(wordObj.getString("originalWord"));
                            word.setTranslatedWord(wordObj.getString("translatedWord"));
                            word.setLanguageId(languageId);
                            word.setLevel(level);

                            // Kategori bilgisini ekle
                            if (wordObj.has("category")) {
                                word.setCategory(wordObj.getString("category"));
                            }

                            // Seçenekleri ekle
                            if (wordObj.has("options")) {
                                JSONArray optionsArray = wordObj.getJSONArray("options");
                                String[] options = new String[optionsArray.length()];
                                for (int j = 0; j < optionsArray.length(); j++) {
                                    options[j] = optionsArray.getString(j);
                                }
                                word.setOptions(options);
                            }

                            result.add(word);
                        }
                    }
                }
            } else if (json.trim().startsWith("[")) {
                // Dizi formatı (eski Filistince) - seviyeye göre filtrele
                Log.d(TAG, "Eski dizi formatı, seviye " + level + " için filtreleniyor");
                JSONArray jsonArray = new JSONArray(json);

                // Önce level alanına göre filtrele
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject wordObj = jsonArray.getJSONObject(i);

                    if (wordObj.has("level") && wordObj.getInt("level") == level &&
                            (!wordObj.has("languageId") || wordObj.getInt("languageId") == languageId)) {

                        Word word = new Word();
                        word.setId(wordObj.optInt("id", i + 1));
                        word.setOriginalWord(wordObj.getString("originalWord"));
                        word.setTranslatedWord(wordObj.getString("translatedWord"));
                        word.setLanguageId(languageId);
                        word.setLevel(level);

                        // Seçenekleri ekle
                        if (wordObj.has("options")) {
                            JSONArray optionsArray = wordObj.getJSONArray("options");
                            String[] options = new String[optionsArray.length()];
                            for (int j = 0; j < optionsArray.length(); j++) {
                                options[j] = optionsArray.getString(j);
                            }
                            word.setOptions(options);
                        }

                        result.add(word);
                    }
                }

                // Eğer level alanına göre hiç kelime bulunamazsa, indeks bazlı yaklaşımı dene
                if (result.isEmpty()) {
                    Log.d(TAG, "Level alanına göre kelime bulunamadı, indeks bazlı yaklaşım deneniyor");
                    int startIndex = (level - 1) * 25;
                    int endIndex = Math.min(startIndex + 25, jsonArray.length());

                    for (int i = startIndex; i < endIndex; i++) {
                        if (i < jsonArray.length()) {
                            JSONObject wordObj = jsonArray.getJSONObject(i);

                            Word word = new Word();
                            word.setId(wordObj.optInt("id", i + 1));
                            word.setOriginalWord(wordObj.getString("originalWord"));
                            word.setTranslatedWord(wordObj.getString("translatedWord"));
                            word.setLanguageId(languageId);
                            word.setLevel(level);

                            // Seçenekleri ekle
                            if (wordObj.has("options")) {
                                JSONArray optionsArray = wordObj.getJSONArray("options");
                                String[] options = new String[optionsArray.length()];
                                for (int j = 0; j < optionsArray.length(); j++) {
                                    options[j] = optionsArray.getString(j);
                                }
                                word.setOptions(options);
                            }

                            result.add(word);
                        }
                    }
                }
            }

            Log.d(TAG, languageId + " dili, seviye " + level + " için " + result.size() + " kelime yüklendi");

            // Örnek olarak birkaç kelimeyi logla
            for (int i = 0; i < Math.min(3, result.size()); i++) {
                Word word = result.get(i);
                Log.d(TAG, "Kelime " + i + ": " + word.getOriginalWord() + " = " + word.getTranslatedWord());
            }

        } catch (Exception e) {
            Log.e(TAG, "getWordsForLanguage(level) hatası: " + e.getMessage(), e);
            e.printStackTrace();
        }

        return result;
    }

    // Belirli bir seviye ve kategori için kelimeleri getiren metod
    public List<Word> getWordsForLanguage(int languageId, int level, String category) {
        List<Word> result = new ArrayList<>();
        try {
            // Önce seviyedeki tüm kelimeleri al
            List<Word> levelWords = getWordsForLanguage(languageId, level);
            // Kategoriye göre filtrele (eğer kategori belirtilmişse)
            if (category != null && !category.isEmpty()) {
                for (Word word : levelWords) {
                    if (category.equals(word.getCategory())) {
                        result.add(word);
                    }
                }
            } else {
                // Kategori belirtilmemişse tüm kelimeleri döndür
                result = levelWords;
            }
            Log.d(TAG, languageId + " dili, " + level + " seviyesi, " + category + " kategorisi için " + result.size() + " kelime yüklendi");
        } catch (Exception e) {
            Log.e(TAG, "getWordsForLanguage(level, category) hatası: " + e.getMessage(), e);
        }
        return result;
    }
}