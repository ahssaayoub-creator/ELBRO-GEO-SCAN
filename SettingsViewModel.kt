package com.elbro.geoscan.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.elbro.geoscan.logic.Thresholds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Holds the configurable anomaly thresholds (spec: "Les seuils doivent être configurables.").
 * In-memory for v1.0.0; swap for a DataStore-backed store if persistence across process
 * death is required later — the surface (StateFlow<Thresholds>) would stay the same.
 */
class SettingsViewModel : ViewModel() {

    private val _thresholds = MutableStateFlow(Thresholds())
    val thresholds: StateFlow<Thresholds> = _thresholds

    fun updateThresholds(low: Double, medium: Double, high: Double) {
        runCatching { Thresholds(low, medium, high) }
            .onSuccess { _thresholds.value = it }
    }
}
