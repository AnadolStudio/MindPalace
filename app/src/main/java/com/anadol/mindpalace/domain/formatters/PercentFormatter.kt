package com.anadol.mindpalace.domain.formatters

import com.github.mikephil.charting.formatter.ValueFormatter
import kotlin.math.roundToInt

class PercentFormatter : ValueFormatter() {

    override fun getFormattedValue(value: Float): String = "${value.roundToInt()} %"

}