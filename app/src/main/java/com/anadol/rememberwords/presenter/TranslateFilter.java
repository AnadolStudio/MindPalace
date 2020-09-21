package com.anadol.rememberwords.presenter;

import android.text.InputFilter;
import android.text.Spanned;

public class TranslateFilter implements InputFilter {
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        //source - это новые символы, а dest - все остальные

        if (end - start > 1) {
            // Если одновременно было вставленно больше 1 символа
            return null;

        } else if (end - start == 1) {
//            if (source.charAt(start) == '\n'
//                    && dest.length() >= 1
//                    && (dest.charAt(dend - 1) != ';')) {
//                return ";" + source;
//            } else if (source.charAt(start) == '\n'
//                    && dest.length() == 0) {
//                return "";
//            }
//            if (source.charAt(start) == ';') {
//                return source + "\n";
//            }
            switch (source.charAt(start)) {
                case '\n':
                    if (dest.length() >= 1 && (dest.charAt(dend - 1) != ';')) return ";" + source;
                    if (dest.length() == 0) return "";
                    break;

                case ';':
                    if (dest.length() >= 1) {
                        return source + "\n";
                    } else {
                        return "";
                    }

            }
        }

        return null;
    }
}
