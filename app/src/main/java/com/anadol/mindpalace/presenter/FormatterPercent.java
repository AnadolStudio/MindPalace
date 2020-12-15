package com.anadol.mindpalace.presenter;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class FormatterPercent extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        int position = Math.round(value);
        return position + "%";
    }
}
