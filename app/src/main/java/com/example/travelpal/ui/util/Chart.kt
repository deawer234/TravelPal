package com.example.travelpal.ui.util

import android.graphics.Color
import com.example.travelpal.data.Location
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class Chart {
    private var max = 0.0f
    private var min = 0.0f
    fun getElevationChartData(chart: LineChart, locations: List<Location>) {
        max = locations.maxOf { it.elevation.toFloat() }
        min = locations.minOf { it.elevation.toFloat() }

        setupChart(chart)
        val entries = locations.mapIndexed { index, location ->
            Entry(index.toFloat(), location.elevation.toFloat())
        }

        val dataSet = LineDataSet(entries, "Elevation")
        dataSet.color = Color.DKGRAY
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.fillColor = Color.BLUE
        dataSet.setDrawFilled(true)


        chart.data = LineData(dataSet)
        chart.animateX(1500)
        chart.invalidate()
    }

    private fun setupChart(chart: LineChart) {
        chart.setBackgroundColor(Color.WHITE)

        chart.description.isEnabled = false

        chart.xAxis.isEnabled = false

        chart.axisLeft.isEnabled = false

        chart.axisRight.apply {
            isEnabled = true
            setDrawGridLines(false)
            setDrawAxisLine(false)
            setLabelCount(2, true)
            textColor = Color.BLACK
            valueFormatter = MaxMinValueFormatter(max, min)
        }

        chart.legend.isEnabled = false

    }

}

class MaxMinValueFormatter(
    private val max: Float,
    private val min: Float,
    private val tolerance: Float = 10f
) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        println(value)
        println(max)
        println(min)
        return when {
            value >= max - tolerance -> String.format("%d", max.toInt())
            value <= min + tolerance -> String.format("%d", min.toInt())
            else -> ""
        }
    }
}