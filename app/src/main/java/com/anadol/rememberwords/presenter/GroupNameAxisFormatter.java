package com.anadol.rememberwords.presenter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class GroupNameAxisFormatter extends ValueFormatter {
    ArrayList<String> mNames;
    public GroupNameAxisFormatter(ArrayList<String> names) {
        mNames = names;
    }

    @Override
    public String getFormattedValue(float value) {
        int position = Math.round(value);
        String name = mNames.get(position);
        // TODO как вариант можно сокращать слова по согласным
        return name.substring(0, Math.min(5, name.length()));
    }
}
