package com.elbro.geoscan.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.elbro.geoscan.BuildConfig
import com.elbro.geoscan.ui.components.ScientificDisclaimer

@Composable
fun AboutScreen() {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("À propos", style = MaterialTheme.typography.headlineLarge)
            Text("ELBRO GEO SCAN", style = MaterialTheme.typography.titleLarge)
            Text(
                "Version ${BuildConfig.VERSION_NAME}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                "Package : ${BuildConfig.APPLICATION_ID}",
                style = MaterialTheme.typography.bodyMedium
            )
            ScientificDisclaimer()
        }
    }
}
