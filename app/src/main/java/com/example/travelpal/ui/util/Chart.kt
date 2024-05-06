package com.example.travelpal.ui.util

import android.graphics.Color
import com.example.travelpal.data.Location
import com.example.travelpal.repository.LocationRepository
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Chart{
    fun getElevationChartData(chart: LineChart, locations: List<Location>){
        setupChart(chart)
        val entries = locations.mapIndexed { index, location ->
            Entry(index.toFloat(), location.elevation.toFloat())
        }

        val dataSet = LineDataSet(entries, "Elevation")
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

    fun getTraveledChartData(chart: LineChart, locations: List<Location>){
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
    private fun setupChart(chart: LineChart) {
        chart.setBackgroundColor(Color.WHITE)  // Set the background color to white

        chart.description.isEnabled = false

        chart.xAxis.apply {
            isEnabled = true
            setDrawGridLines(false)  // Disable grid lines
            setDrawAxisLine(false)  // Disable axis line
            textColor = Color.BLACK  // Set the color of the labels to black
        }

        chart.axisLeft.isEnabled = false  // Disable left y-axis

        chart.axisRight.apply {
            isEnabled = true
            setDrawGridLines(false)  // Disable grid lines
            setDrawAxisLine(false)  // Disable axis line
            textColor = Color.BLACK  // Set the color of the labels to black
        }

        chart.legend.isEnabled = false  // Disable the legend
    }

}

class ChartValueFormater : ValueFormatter() {
    private val dateFormatter = SimpleDateFormat("HH:mm", Locale.GERMANY)

    override fun getFormattedValue(value: Float): String {
        return dateFormatter.format(Date(value.toLong() * 1000L))
    }
}