package com.anadol.mindpalace.domain.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class PercentFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return Math.round(value) + "%";
    }
}
