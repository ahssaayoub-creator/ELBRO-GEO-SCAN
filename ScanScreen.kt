package com.elbro.geoscan.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elbro.geoscan.ui.components.AnomalyBadge
import com.elbro.geoscan.ui.components.MetricTile
import com.elbro.geoscan.ui.viewmodel.ScanViewModel

@Composable
fun ScanScreen(scanName: String, onScanFinished: (Long) -> Unit) {
    val context = LocalContext.current
    val viewModel: ScanViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        if (!granted) viewModel.reportPermissionDenied()
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Scan terrain — $scanName", style = MaterialTheme.typography.headlineLarge)

            if (!state.magnetometerAvailable) {
                Text(
                    "Magnétomètre non disponible sur cet appareil.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                return@Column
            }

            if (!hasLocationPermission) {
                Text(
                    "L'accès à la localisation est requis pour associer chaque mesure " +
                        "magnétique à une position GPS réelle.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Button(onClick = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text("Autoriser la localisation")
                }
                return@Column
            }

            state.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            if (!state.isScanning) {
                Button(
                    onClick = { viewModel.startScan(scanName) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Démarrer le scan")
                }
            } else {
                AnomalyBadge(level = state.anomalyLevel)

                Text("Magnetic field", style = MaterialTheme.typography.titleMedium)
                MetricTile(label = "Magnitude totale", value = "${format(state.currentMagnitude)} µT")

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricTile(
                        label = "Bx",
                        value = "${state.lastMagnetic?.bx?.let { format(it.toDouble()) } ?: "--"} µT",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "By",
                        value = "${state.lastMagnetic?.by?.let { format(it.toDouble()) } ?: "--"} µT",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Bz",
                        value = "${state.lastMagnetic?.bz?.let { format(it.toDouble()) } ?: "--"} µT",
                        modifier = Modifier.weight(1f)
                    )
                }

                MetricTile(
                    label = "GPS (latitude / longitude)",
                    value = state.gpsFix?.let { "${it.latitude}, ${it.longitude}" } ?: "En attente…"
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MetricTile(
                        label = "Accuracy",
                        value = state.gpsFix?.let { "± ${format(it.accuracy.toDouble())} m" } ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                    MetricTile(
                        label = "Altitude",
                        value = state.gpsFix?.let { "${format(it.altitude)} m" } ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                }
                MetricTile(label = "Nombre de mesures", value = "${state.measurementCount}")

                Button(
                    onClick = {
                        viewModel.stopScan()
                        viewModel.currentScanId?.let { onScanFinished(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Arrêter le scan et voir les résultats")
                }
            }
        }
    }
}

private fun format(value: Double): String = "%.2f".format(value)
