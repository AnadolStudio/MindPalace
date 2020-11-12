package com.anadol.rememberwords.presenter;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyPercentFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        int position = Math.round(value);
        return position + "%";
    }
}
