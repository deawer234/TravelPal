package com.example.travelpal.ui.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounter(context: Context) : SensorEventListener {
    private var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private var stepCount = 0L
    private var previousStepCount = 0L

    init {
        sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val stepsSinceBoot = event.values[0].toLong()
            stepCount = stepsSinceBoot - previousStepCount
            previousStepCount = stepsSinceBoot
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    fun getStepCount(): Long {
        return stepCount
    }
}