package com.anadol.mindpalace.presenter;

import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class FormatterInteger extends ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        int position = Math.round(value);
        return Integer.toString(position);
    }
}
