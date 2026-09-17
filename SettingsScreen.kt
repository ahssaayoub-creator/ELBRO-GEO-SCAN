package com.elbro.geoscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elbro.geoscan.ui.viewmodel.SettingsViewModel

/**
 * Lets the user configure the low/medium/high anomaly-score thresholds
 * (spec: "Les seuils doivent être configurables.").
 */
@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = viewModel()
    val thresholds by viewModel.thresholds.collectAsState()

    var low by remember { mutableStateOf(thresholds.lowThreshold.toString()) }
    var medium by remember { mutableStateOf(thresholds.mediumThreshold.toString()) }
    var high by remember { mutableStateOf(thresholds.highThreshold.toString()) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Paramètres", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Seuils de détection d'anomalie (en µT). Doivent être strictement croissants.",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = low, onValueChange = { low = it },
                label = { Text("Seuil FAIBLE") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = medium, onValueChange = { medium = it },
                label = { Text("Seuil MOYEN") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = high, onValueChange = { high = it },
                label = { Text("Seuil ÉLEVÉ") },
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    val l = low.toDoubleOrNull()
                    val m = medium.toDoubleOrNull()
                    val h = high.toDoubleOrNull()
                    if (l == null || m == null || h == null || !(l < m && m < h)) {
                        error = "Valeurs invalides : les seuils doivent être croissants (faible < moyen < élevé)."
                    } else {
                        error = null
                        viewModel.updateThresholds(l, m, h)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer")
            }
        }
    }
}
