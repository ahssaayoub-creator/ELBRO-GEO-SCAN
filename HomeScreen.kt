package com.elbro.geoscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elbro.geoscan.ui.components.ScientificDisclaimer
import com.elbro.geoscan.ui.theme.AccentGreen

@Composable
fun HomeScreen(
    onNewAnalysis: () -> Unit,
    onHistory: () -> Unit,
    onSettings: () -> Unit,
    onAbout: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "ELBRO GEO SCAN",
                style = MaterialTheme.typography.headlineLarge,
                color = AccentGreen,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Détection de variations magnétiques de terrain",
                style = MaterialTheme.typography.bodyLarge
            )

            Button(onClick = onNewAnalysis, modifier = Modifier) {
                Text("Nouvelle analyse")
            }
            OutlinedButton(onClick = onHistory) {
                Icon(Icons.Filled.History, contentDescription = null)
                Text("  Historique")
            }
            OutlinedButton(onClick = onSettings) {
                Icon(Icons.Filled.Settings, contentDescription = null)
                Text("  Paramètres")
            }
            OutlinedButton(onClick = onAbout) {
                Icon(Icons.Filled.Info, contentDescription = null)
                Text("  À propos")
            }

            ScientificDisclaimer()
        }
    }
}
