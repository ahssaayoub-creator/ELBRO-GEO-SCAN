package com.elbro.geoscan.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elbro.geoscan.ui.components.anomalyColor
import com.elbro.geoscan.ui.viewmodel.HistoryViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapType
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

private enum class MapStyle(val label: String, val mapType: MapType) {
    STANDARD("STANDARD", MapType.NORMAL),
    SATELLITE("SATELLITE", MapType.SATELLITE),
    TERRAIN("TERRAIN", MapType.TERRAIN)
}

/**
 * Real Google Maps (Maps Compose) rendering of GPS track + magnetic anomaly points.
 * Requires a valid MAPS_API_KEY in local.properties (see README "Configuration Maps").
 * The screen still compiles and runs without a key — Google's SDK will show a blank
 * "for development purposes only" map watermark instead of crashing.
 */
@Composable
fun MapScreen(scanId: Long) {
    val viewModel: HistoryViewModel = viewModel()
    val measurements by viewModel.selectedScanMeasurements.collectAsState()

    androidx.compose.runtime.LaunchedEffect(scanId) {
        viewModel.loadScanDetails(scanId)
    }

    var mapStyle by remember { mutableStateOf(MapStyle.STANDARD) }
    var expanded by remember { mutableStateOf(false) }

    val points = measurements.map { LatLng(it.latitude, it.longitude) }
    val startPosition = points.firstOrNull() ?: LatLng(33.5731, -7.5898) // Casablanca fallback

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(startPosition, 17f)
    }

    Scaffold { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.padding(12.dp)
            ) {
                TextButton(onClick = { expanded = true }) {
                    Text("Style de carte : ${mapStyle.label}")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    MapStyle.entries.forEach { style ->
                        DropdownMenuItem(
                            text = { Text(style.label) },
                            onClick = { mapStyle = style; expanded = false }
                        )
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(mapType = mapStyle.mapType)
                ) {
                    if (points.size > 1) {
                        Polyline(points = points, color = MaterialTheme.colorScheme.primary)
                    }
                    measurements.forEach { m ->
                        Circle(
                            center = LatLng(m.latitude, m.longitude),
                            radius = 3.0,
                            fillColor = anomalyColor(m.anomalyLevel),
                            strokeColor = anomalyColor(m.anomalyLevel)
                        )
                    }
                }
            }
        }
    }
}
