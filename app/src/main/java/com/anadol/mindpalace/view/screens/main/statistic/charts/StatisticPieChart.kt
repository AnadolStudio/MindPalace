package com.anadol.mindpalace.view.screens.main.statistic.charts

import android.content.Context
import android.content.res.Configuration
import android.graphics.Typeface
import com.anadol.mindpalace.R
import com.anadol.mindpalace.domain.formatters.IntegerFormatter
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend.*
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

object StatisticPieChart {

    fun init(
        context: Context,
        pieChart: PieChart,
        primaryTextColor: Int,
        primaryTypeFace: Typeface = Typeface.SERIF
    ) {
        pieChart.apply {
//            layoutParams.height = (resources.displayMetrics.heightPixels * 0.55).toInt() // TODO ?

            description.apply {
                text = ""
                textSize = 14f
                typeface = primaryTypeFace
                textColor = primaryTextColor
            }

            setNoDataTextColor(primaryTextColor)
            setNoDataText(context.getString(R.string.no_data))
            setNoDataTextTypeface(primaryTypeFace)
            setExtraOffsets(5F, 10F, 5F, 5F)
            rotationAngle = 0F
            holeRadius = 30f
            transparentCircleRadius = 36f

            legend.apply {
                verticalAlignment = LegendVerticalAlignment.TOP

                val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
                horizontalAlignment =if (isPortrait) LegendHorizontalAlignment.CENTER else LegendHorizontalAlignment.RIGHT
                orientation = if (isPortrait) LegendOrientation.HORIZONTAL else LegendOrientation.VERTICAL
                setDrawInside(isPortrait)

                textSize = 14f
                textColor = primaryTextColor
                typeface = primaryTypeFace
                xEntrySpace = 7f
                yEntrySpace = 5f
                yOffset = -5f
                isEnabled = true
            }

            setEntryLabelTextSize(14f)
            setEntryLabelTypeface(primaryTypeFace)
            setDrawEntryLabels(false)
            animateY(1400, Easing.EaseInOutQuad)
        }
    }

    fun setData(
        data: StatisticPieChartData,
        pieChart: PieChart,
        primaryTypeFace: Typeface = Typeface.SERIF
    ) {
        pieChart.apply {
            val entries = ArrayList<PieEntry>()

            for ((i, v) in data.list.withIndex()) {
                entries.add(PieEntry(v, data.labels[i]))
            }

            val dataSet = PieDataSet(entries, "").apply {
                setDrawIcons(false)
                sliceSpace = 3f
                selectionShift = 5f
                colors = data.labelColors
            }

            setData(PieData(dataSet).apply {
                setValueFormatter(IntegerFormatter())
                setValueTypeface(primaryTypeFace)
                setValueTextSize(16f)
            })

            invalidate()
        }
    }
}

