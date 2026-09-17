package com.elbro.geoscan.ui.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.elbro.geoscan.ui.components.MetricTile
import com.elbro.geoscan.ui.theme.AccentGreen
import com.elbro.geoscan.ui.theme.AccentRed

/**
 * Real calibration screen: reads the live magnetometer accuracy level
 * (SensorManager.SENSOR_STATUS_*) and asks the user to perform the classic
 * figure-8 motion until SENSOR_STATUS_ACCURACY_HIGH/MEDIUM is reached, exactly
 * like a real compass calibration flow — no fake progress bar.
 */
@Composable
fun CalibrationScreen(scanName: String, onCalibrationDone: () -> Unit) {
    val context = LocalContext.current
    var accuracyLevel by remember { mutableIntStateOf(SensorManager.SENSOR_STATUS_UNRELIABLE) }
    var sensorAvailable by remember { mutableStateOf(true) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorAvailable = magnetometer != null

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                // no-op: we only need onAccuracyChanged, but the listener must implement both
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                accuracyLevel = accuracy
            }
        }

        if (magnetometer != null) {
            sensorManager.registerListener(listener, magnetometer, SensorManager.SENSOR_DELAY_UI)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    val accuracyLabel = when (accuracyLevel) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> "Élevée"
        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> "Moyenne"
        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> "Faible"
        else -> "Non fiable"
    }
    val isReady = accuracyLevel == SensorManager.SENSOR_STATUS_ACCURACY_HIGH ||
        accuracyLevel == SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
    val progress = when (accuracyLevel) {
        SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> 1f
        SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> 0.66f
        SensorManager.SENSOR_STATUS_ACCURACY_LOW -> 0.33f
        else -> 0.1f
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Calibration", style = MaterialTheme.typography.headlineLarge)

            if (!sensorAvailable) {
                Text(
                    "Magnétomètre non disponible sur cet appareil.",
                    color = AccentRed,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Text(
                    "Déplacez le téléphone en formant un « 8 » dans l'air pour calibrer " +
                        "le magnétomètre, comme pour une boussole.",
                    style = MaterialTheme.typography.bodyMedium
                )
                MetricTile(label = "Précision du magnétomètre", value = accuracyLabel)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isReady) AccentGreen else MaterialTheme.colorScheme.primary
                )
            }

            Button(
                onClick = onCalibrationDone,
                modifier = Modifier.fillMaxWidth(),
                enabled = sensorAvailable && isReady
            ) {
                Text(if (isReady) "Démarrer le scan « $scanName »" else "En attente de calibration…")
            }

            if (!isReady && sensorAvailable) {
                Text(
                    "Vous pouvez continuer sans calibration parfaite, mais la précision " +
                        "des mesures sera réduite.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = onCalibrationDone, modifier = Modifier.fillMaxWidth()) {
                    Text("Continuer quand même")
                }
            }
        }
    }
}
