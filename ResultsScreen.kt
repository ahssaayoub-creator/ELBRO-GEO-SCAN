package com.elbro.geoscan.ui.screens

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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elbro.geoscan.export.ExportUtils
import com.elbro.geoscan.ui.components.MetricTile
import com.elbro.geoscan.ui.components.ScientificDisclaimer
import com.elbro.geoscan.ui.viewmodel.HistoryViewModel

@Composable
fun ResultsScreen(scanId: Long, onViewMap: () -> Unit, onBackToHome: () -> Unit) {
    val context = LocalContext.current
    val viewModel: HistoryViewModel = viewModel()
    val scan by viewModel.selectedScan.collectAsState()
    val measurements by viewModel.selectedScanMeasurements.collectAsState()

    LaunchedEffect(scanId) {
        viewModel.loadScanDetails(scanId)
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
            Text("Résultats", style = MaterialTheme.typography.headlineLarge)
            Text(scan?.name ?: "", style = MaterialTheme.typography.titleMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetricTile(
                    label = "Mesures",
                    value = "${measurements.size}",
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    label = "Score max. d'anomalie",
                    value = "%.2f µT".format(scan?.maxAnomalyScore ?: 0.0),
                    modifier = Modifier.weight(1f)
                )
            }
            MetricTile(
                label = "Baseline (champ moyen)",
                value = "%.2f µT".format(scan?.baselineMagnitude ?: 0.0)
            )

            Button(onClick = onViewMap, modifier = Modifier.fillMaxWidth()) {
                Text("Voir la carte")
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        scan?.let {
                            val result = ExportUtils.exportCsv(context, it, measurements)
                            context.startActivity(result.chooserIntent)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Exporter en CSV") }

                OutlinedButton(
                    onClick = {
                        scan?.let {
                            val result = ExportUtils.exportJson(context, it, measurements)
                            context.startActivity(result.chooserIntent)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Exporter en JSON") }
            }

            ScientificDisclaimer()

            Button(onClick = onBackToHome, modifier = Modifier.fillMaxWidth()) {
                Text("Retour à l'accueil")
            }
        }
    }
}
