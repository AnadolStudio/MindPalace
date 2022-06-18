package com.anadol.mindpalace.domain.formatters;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class IntegerFormatter extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return Integer.toString(Math.round(value));
    }
}
