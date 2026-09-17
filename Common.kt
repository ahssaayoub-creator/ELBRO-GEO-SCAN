package com.elbro.geoscan.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.elbro.geoscan.data.AnomalyLevel
import com.elbro.geoscan.ui.theme.AccentAmber
import com.elbro.geoscan.ui.theme.AccentGreen
import com.elbro.geoscan.ui.theme.AccentRed
import com.elbro.geoscan.ui.theme.TextSecondary

/** A single labeled live-value tile used on ScanScreen (Bx, By, Bz, GPS, etc.) */
@Composable
fun MetricTile(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

fun anomalyColor(level: AnomalyLevel) = when (level) {
    AnomalyLevel.NORMAL -> AccentGreen
    AnomalyLevel.LOW -> AccentAmber
    AnomalyLevel.MEDIUM -> AccentAmber
    AnomalyLevel.HIGH -> AccentRed
}

fun anomalyLabel(level: AnomalyLevel) = when (level) {
    AnomalyLevel.NORMAL -> "NORMAL"
    AnomalyLevel.LOW -> "LOW ANOMALY"
    AnomalyLevel.MEDIUM -> "MEDIUM ANOMALY"
    AnomalyLevel.HIGH -> "HIGH ANOMALY"
}

@Composable
fun AnomalyBadge(level: AnomalyLevel, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(anomalyColor(level).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Anomalie magnétique détectée",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
        Text(
            text = anomalyLabel(level),
            style = MaterialTheme.typography.headlineLarge,
            color = anomalyColor(level),
            fontWeight = FontWeight.Bold
        )
    }
}

/** Legally/scientifically required disclaimer, shown on Home, Results and About. */
@Composable
fun ScientificDisclaimer(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "ELBRO GEO SCAN mesure les variations du champ magnétique à l'aide du magnétomètre " +
                "du smartphone. Une anomalie magnétique ne signifie pas nécessairement la présence " +
                "d'un métal. Les résultats peuvent être influencés par le fer, l'acier, les câbles, " +
                "les véhicules, les structures enterrées, certaines roches et l'environnement. Cette " +
                "application ne permet pas d'identifier directement l'or ou un métal spécifique et ne " +
                "remplace pas un détecteur professionnel ou une étude géophysique.",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}
