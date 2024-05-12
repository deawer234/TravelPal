package com.example.travelpal.ui.util

import android.graphics.Color
import com.example.travelpal.data.Location
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
class Chart{
    var max = 0.0f
    var min = 0.0f
    fun getElevationChartData(chart: LineChart, locations: List<Location>){
        max = locations.maxOf { it.elevation.toFloat() }
        min = locations.minOf { it.elevation.toFloat() }

        setupChart(chart)
        val entries = locations.mapIndexed { index, location ->
            Entry(index.toFloat(), location.elevation.toFloat())
        }

        val dataSet = LineDataSet(entries, "Elevation")
        dataSet.color = Color.DKGRAY  // Set the color of the line to dark gray
        dataSet.lineWidth = 2f  // Set the line width
        dataSet.setDrawCircles(false)  // Disable circles at data points
        dataSet.setDrawValues(false)  // Disable value labels
        dataSet.fillColor = Color.BLUE  // Set the fill color below the line to light gray
        dataSet.setDrawFilled(true)  // Enable fill color below the line


        chart.data = LineData(dataSet)
        chart.animateX(1500)
        chart.invalidate()
    }

    fun getTraveledChartData(chart: LineChart, locations: List<Location>){
        max = locations.maxOf { it.traveled}
        min = locations.minOf { it.traveled }

        setupChart(chart)
        val entries = locations.mapIndexed { index, location ->
            Entry(index.toFloat(), location.traveled)
        }

        val dataSet = LineDataSet(entries, "Traveled")
        dataSet.color = Color.DKGRAY  // Set the color of the line to dark gray
        dataSet.lineWidth = 2f  // Set the line width
        dataSet.setDrawCircles(false)  // Disable circles at data points
        dataSet.setDrawValues(false)  // Disable value labels
        dataSet.fillColor = Color.LTGRAY  // Set the fill color below the line to light gray
        dataSet.setDrawFilled(true)  // Enable fill color below the line

        chart.data = LineData(dataSet)
        chart.animateX(1500)
        chart.invalidate()
    }

    fun getSpeedChartData(chart: LineChart, locations: List<Location>){
        setupChart(chart)
        val entries = locations.mapIndexed { index, location ->
            Entry(index.toFloat(), location.speed)
        }

        val dataSet = LineDataSet(entries, "Speed")
        dataSet.color = Color.DKGRAY
        dataSet.lineWidth = 2f
        dataSet.setDrawCircles(false)
        dataSet.setDrawValues(false)
        dataSet.fillColor = Color.LTGRAY
        dataSet.setDrawFilled(true)

        chart.data = LineData(dataSet)
        chart.animateX(1500)
        chart.invalidate()
    }
    private fun setupChart(chart: LineChart) {
        chart.setBackgroundColor(Color.WHITE)

        chart.description.isEnabled = false

        chart.xAxis.isEnabled = false

        chart.axisLeft.isEnabled = false  // Disable left y-axis

        chart.axisRight.apply {
            isEnabled = true
            setDrawGridLines(false)  // Disable grid lines
            setDrawAxisLine(false)  // Disable axis line
            setLabelCount(2, true)
            textColor = Color.BLACK  // Set the color of the labels to black
            valueFormatter = MaxMinValueFormatter(max, min)
        }

        chart.legend.isEnabled = false  // Disable the legend

    }

}

class MaxMinValueFormatter(private val max: Float, private val min: Float, val tolerance: Float = 10f) : ValueFormatter() {
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