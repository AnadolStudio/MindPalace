package com.anadol.mindpalace.presenter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class FormatterGroupNameAxis extends ValueFormatter {
    ArrayList<String> mNames;
    public FormatterGroupNameAxis(ArrayList<String> names) {
        mNames = names;
    }

    @Override
    public String getFormattedValue(float value) {
        int position = Math.round(value);
        String name = mNames.get(Math.min(mNames.size() - 1, position));
        // TODO как вариант можно сокращать слова по согласным
        return name.substring(0, Math.min(5, name.length()));
    }
}
