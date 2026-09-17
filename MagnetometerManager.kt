package com.elbro.geoscan.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class MagneticReading(
    val bx: Float,
    val by: Float,
    val bz: Float,
    val timestamp: Long
)

/**
 * Wraps the REAL device magnetometer: android.hardware.Sensor.TYPE_MAGNETIC_FIELD.
 * No simulated data — if the sensor is absent, [isAvailable] is false and the UI
 * must show "Magnétomètre non disponible sur cet appareil." instead of a fake scan.
 *
 * SENSOR_DELAY_NORMAL / UI is used rather than FASTEST to keep battery consumption low,
 * per the "Ne pas utiliser une fréquence de capteur inutilement élevée" requirement.
 */
class MagnetometerManager(context: Context) {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    val isAvailable: Boolean get() = magnetometer != null

    fun readings(): Flow<MagneticReading> = callbackFlow {
        if (magnetometer == null) {
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    trySend(
                        MagneticReading(
                            bx = event.values[0],
                            by = event.values[1],
                            bz = event.values[2],
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Accuracy changes (SENSOR_STATUS_*) can be surfaced to the UI later
                // if we want to warn the user the magnetometer needs recalibration
                // (the classic figure-8 gesture) — handled in CalibrationScreen.
            }
        }

        sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}
