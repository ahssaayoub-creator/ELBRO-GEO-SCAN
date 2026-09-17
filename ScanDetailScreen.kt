package com.elbro.geoscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elbro.geoscan.ui.components.anomalyLabel
import com.elbro.geoscan.ui.viewmodel.HistoryViewModel

@Composable
fun ScanDetailScreen(scanId: Long, onViewMap: () -> Unit) {
    val viewModel: HistoryViewModel = viewModel()
    val scan by viewModel.selectedScan.collectAsState()
    val measurements by viewModel.selectedScanMeasurements.collectAsState()

    LaunchedEffect(scanId) {
        viewModel.loadScanDetails(scanId)
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Détails d'une analyse", style = MaterialTheme.typography.headlineLarge)
            Text(scan?.name ?: "", style = MaterialTheme.typography.titleMedium)
            Text("${measurements.size} mesures enregistrées", style = MaterialTheme.typography.bodyMedium)

            Button(onClick = onViewMap, modifier = Modifier.fillMaxWidth()) {
                Text("Voir sur la carte")
            }

            Divider()

            LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                items(measurements) { m ->
                    Text(
                        "%.2f µT · %s · %.5f, %.5f".format(
                            m.magnitude, anomalyLabel(m.anomalyLevel), m.latitude, m.longitude
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
