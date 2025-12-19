package com.example.project_design.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StepCounterManager(
    context: Context
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    private val stepSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private val _totalStepsSinceBoot = MutableStateFlow(0L)
    val totalStepsSinceBoot: StateFlow<Long> = _totalStepsSinceBoot

    // センサー有無を外から判定できるようにする
    val hasStepSensor: Boolean = stepSensor != null

    fun start() {
        if (stepSensor == null) {
            Log.w("StepCounter", "TYPE_STEP_COUNTER not available on this device")
            return
        }
        try {
            sensorManager.registerListener(
                this,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI
            )
        } catch (e: Exception) {
            Log.e("StepCounter", "Failed to register step sensor", e)
        }
    }

    fun stop() {
        try {
            sensorManager.unregisterListener(this)
        } catch (e: Exception) {
            Log.e("StepCounter", "Failed to unregister step sensor", e)
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            _totalStepsSinceBoot.value = event.values[0].toLong()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // no-op
    }
}
