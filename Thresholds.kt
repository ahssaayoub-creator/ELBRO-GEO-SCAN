package com.elbro.geoscan.logic

/**
 * Configurable anomaly-score thresholds (in microtesla, µT), as required by the spec:
 * "Les seuils doivent être configurables." Persisted via SettingsViewModel / DataStore-style
 * in-memory state exposed through Settings screen; defaults chosen from typical smartphone
 * magnetometer noise floors (~0.5-1 µT) so NORMAL covers ordinary sensor jitter.
 */
data class Thresholds(
    val lowThreshold: Double = 3.0,
    val mediumThreshold: Double = 8.0,
    val highThreshold: Double = 15.0
) {
    init {
        require(lowThreshold < mediumThreshold && mediumThreshold < highThreshold) {
            "Thresholds must be strictly increasing: low < medium < high"
        }
    }
}
