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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * "Nouvelle analyse": lets the user name the survey before moving on to Calibration.
 * A sensible default name (site + timestamp) is pre-filled so the flow never blocks
 * on a required field.
 */
@Composable
fun NewAnalysisScreen(onProceedToCalibration: (String) -> Unit) {
    val defaultName = remember {
        "Analyse " + SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
    }
    var scanName by remember { mutableStateOf(defaultName) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Nouvelle analyse", style = MaterialTheme.typography.headlineLarge)
            Text(
                "Donnez un nom à cette analyse de terrain. Vous passerez ensuite par " +
                    "l'étape de calibration avant de démarrer le scan réel.",
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(
                value = scanName,
                onValueChange = { scanName = it },
                label = { Text("Nom de l'analyse") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { onProceedToCalibration(scanName) },
                modifier = Modifier.fillMaxWidth(),
                enabled = scanName.isNotBlank()
            ) {
                Text("Continuer vers la calibration")
            }
        }
    }
}
