package com.example.diluygulamas.util;

public class NumberTranslator {
    // Türkmence sayı karşılıkları
    private static final String[] TURKMEN_ONES = {
            "nol", "bir", "iki", "üç", "dört", "bäş", "alty", "ýedi", "sekiz", "dokuz"
    };

    private static final String[] TURKMEN_TEENS = {
            "on", "on bir", "on iki", "on üç", "on dört", "on bäş", "on alty", "on ýedi", "on sekiz", "on dokuz"
    };

    private static final String[] TURKMEN_TENS = {
            "", "on", "ýigrimi", "otuz", "kyrk", "elli", "altmyş", "ýetmiş", "segsen", "togsan"
    };

    // Türkçe sayı karşılıkları (çevirinin anlaşılır olması için)
    private static final String[] TURKISH_ONES = {
            "nol", "bir", "iki", "üç", "dört", "beş", "altı", "yedi", "sekiz", "dokuz"
    };

    private static final String[] TURKISH_TEENS = {
            "on", "on bir", "on iki", "on üç", "on dört", "on beş", "on altı", "on yedi", "on sekiz", "on dokuz"
    };

    private static final String[] TURKISH_TENS = {
            "", "on", "yirmi", "otuz", "kırk", "elli", "altmış", "yetmiş", "seksen", "doksan"
    };

    // Büyük sayılar için
    private static final String[] TURKMEN_THOUSANDS = {
            "", "müň", "million", "milliard", "trillion"
    };

    private static final String[] TURKISH_THOUSANDS = {
            "", "bin", "milyon", "milyar", "trilyon"
    };

    public static String[] getNumberName(int number) {
        if (number == 0) {
            return new String[]{"Syfyr", "Sıfır"};
        }

        String turkmen = convertToWords(number, true);
        String turkish = convertToWords(number, false);

        return new String[]{turkmen, turkish};
    }

    private static String convertToWords(int number, boolean isTurkmen) {
        if (number < 0) {
            return (isTurkmen ? "Minus " : "Eksi ") + convertToWords(-number, isTurkmen);
        }

        if (number < 10) {
            return isTurkmen ? TURKMEN_ONES[number] : TURKISH_ONES[number];
        }

        if (number < 20) {
            return isTurkmen ? TURKMEN_TEENS[number - 10] : TURKISH_TEENS[number - 10];
        }

        if (number < 100) {
            String[] tens = isTurkmen ? TURKMEN_TENS : TURKISH_TENS;
            String[] ones = isTurkmen ? TURKMEN_ONES : TURKISH_ONES;

            int tensDigit = number / 10;
            int onesDigit = number % 10;

            if (onesDigit == 0) {
                return tens[tensDigit];
            } else {
                return tens[tensDigit] + " " + ones[onesDigit];
            }
        }

        if (number < 1000) {
            String[] ones = isTurkmen ? TURKMEN_ONES : TURKISH_ONES;

            int hundredsDigit = number / 100;
            int remainder = number % 100;

            String prefix = hundredsDigit == 1 ? "" : ones[hundredsDigit] + " ";
            String hundred = isTurkmen ? "ýüz" : "yüz";

            if (remainder == 0) {
                return prefix + hundred;
            } else {
                return prefix + hundred + " " + convertToWords(remainder, isTurkmen);
            }
        }

        // 1000 ve üzeri sayılar için
        for (int i = 4; i > 0; i--) {
            int threshold = (int) Math.pow(1000, i);
            if (number >= threshold) {
                int quotient = number / threshold;
                int remainder = number % threshold;

                String[] thousands = isTurkmen ? TURKMEN_THOUSANDS : TURKISH_THOUSANDS;

                String prefix = quotient == 1 && i == 1 ? "" : convertToWords(quotient, isTurkmen) + " ";

                if (remainder == 0) {
                    return prefix + thousands[i];
                } else {
                    return prefix + thousands[i] + " " + convertToWords(remainder, isTurkmen);
                }
            }
        }

        return "";
    }
}