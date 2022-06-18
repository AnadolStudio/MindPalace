package com.anadol.mindpalace.domain.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class NameAxisFormatter extends ValueFormatter {
    private final ArrayList<String> names;

    public NameAxisFormatter(ArrayList<String> names) {
        this.names = names;
    }

    @Override
    public String getFormattedValue(float value) {
        int position = Math.round(value);
        String name = names.get(Math.min(names.size() - 1, position));
        // TODO как вариант можно сокращать слова по согласным
        return name.substring(0, Math.min(5, name.length()));
    }
}
