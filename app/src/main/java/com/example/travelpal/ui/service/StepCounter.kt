package com.example.travelpal.ui.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounter(context: Context) : SensorEventListener {
    private var sensorManager: SensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepCounterSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var stepCount = 0
    private var previousStepCount = 0

    init {
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val stepsSinceBoot = event.values[0].toInt()
            if (previousStepCount == 0) previousStepCount = stepsSinceBoot
            stepCount = stepsSinceBoot - previousStepCount
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun getStepCount(): Int {
        return stepCount
    }

    fun startTrackingSteps() {
        previousStepCount = 0
        stepCount = 0
    }
}