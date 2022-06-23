package com.anadolstudio.core.util

import com.google.android.material.slider.LabelFormatter
import kotlin.math.roundToInt

class RealFormatter : LabelFormatter {

    override fun getFormattedValue(value: Float) = value.roundToInt().toString()

}