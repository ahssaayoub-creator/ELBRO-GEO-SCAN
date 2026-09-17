package com.elbro.geoscan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elbro.geoscan.data.AnomalyLevel
import com.elbro.geoscan.data.AppDatabase
import com.elbro.geoscan.data.Measurement
import com.elbro.geoscan.data.Repository
import com.elbro.geoscan.data.Scan
import com.elbro.geoscan.logic.AnomalyCalculator
import com.elbro.geoscan.logic.Thresholds
import com.elbro.geoscan.sensors.GpsReading
import com.elbro.geoscan.sensors.LocationManagerHelper
import com.elbro.geoscan.sensors.MagneticReading
import com.elbro.geoscan.sensors.MagnetometerManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * State shown live on the ScanScreen while a field scan is running.
 * All values here come from real sensor callbacks — nothing here is simulated.
 */
data class ScanUiState(
    val isScanning: Boolean = false,
    val magnetometerAvailable: Boolean = true,
    val gpsFix: GpsReading? = null,
    val lastMagnetic: MagneticReading? = null,
    val currentMagnitude: Double = 0.0,
    val baseline: Double = 0.0,
    val anomalyScore: Double = 0.0,
    val anomalyLevel: AnomalyLevel = AnomalyLevel.NORMAL,
    val measurementCount: Int = 0,
    val errorMessage: String? = null
)

class ScanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(AppDatabase.getInstance(application))
    private val magnetometerManager = MagnetometerManager(application)
    private val locationHelper = LocationManagerHelper(application)

    private val _uiState = MutableStateFlow(
        ScanUiState(magnetometerAvailable = magnetometerManager.isAvailable)
    )
    val uiState: StateFlow<ScanUiState> = _uiState

    var thresholds: Thresholds = Thresholds()

    var currentScanId: Long? = null
        private set
    private var scanJob: Job? = null
    private val magnitudeHistory = mutableListOf<Double>()
    private var lastGpsReading: GpsReading? = null

    fun startScan(scanName: String) {
        if (!magnetometerManager.isAvailable) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Magnétomètre non disponible sur cet appareil."
            )
            return
        }

        magnitudeHistory.clear()
        _uiState.value = ScanUiState(
            isScanning = true,
            magnetometerAvailable = true
        )

        scanJob = viewModelScope.launch {
            val scanId = repository.createScan(scanName)
            currentScanId = scanId

            combine(
                magnetometerManager.readings(),
                locationHelper.locationUpdates()
            ) { mag, gps -> Pair(mag, gps) }
                .collect { (mag, gps) ->
                    lastGpsReading = gps
                    processReading(scanId, mag, gps)
                }
        }
    }

    private suspend fun processReading(scanId: Long, mag: MagneticReading, gps: GpsReading) {
        val magnitude = AnomalyCalculator.magnitude(mag.bx, mag.by, mag.bz)
        val baseline = AnomalyCalculator.computeBaseline(magnitudeHistory)
        val score = AnomalyCalculator.anomalyScore(magnitude, baseline)
        val level = AnomalyCalculator.classify(score, thresholds)

        magnitudeHistory.add(magnitude)

        val measurement = Measurement(
            scanId = scanId,
            timestamp = mag.timestamp,
            latitude = gps.latitude,
            longitude = gps.longitude,
            altitude = gps.altitude,
            accuracy = gps.accuracy,
            bx = mag.bx,
            by = mag.by,
            bz = mag.bz,
            magnitude = magnitude,
            anomalyScore = score,
            anomalyLevel = level
        )
        repository.addMeasurement(measurement)

        _uiState.value = _uiState.value.copy(
            gpsFix = gps,
            lastMagnetic = mag,
            currentMagnitude = magnitude,
            baseline = baseline,
            anomalyScore = score,
            anomalyLevel = level,
            measurementCount = magnitudeHistory.size,
            errorMessage = null
        )
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
        _uiState.value = _uiState.value.copy(isScanning = false)

        val scanId = currentScanId ?: return
        viewModelScope.launch {
            val scan = repository.getScan(scanId) ?: return@launch
            scan.endTimestamp = System.currentTimeMillis()
            scan.baselineMagnitude = AnomalyCalculator.computeBaseline(magnitudeHistory)
            scan.measurementCount = magnitudeHistory.size
            scan.maxAnomalyScore = _uiState.value.anomalyScore.coerceAtLeast(scan.maxAnomalyScore)
            repository.finishScan(scan)
        }
    }

    fun reportGpsUnavailable() {
        _uiState.value = _uiState.value.copy(errorMessage = "GPS indisponible.")
    }

    fun reportPermissionDenied() {
        _uiState.value = _uiState.value.copy(
            errorMessage = "Permission de localisation refusée. Veuillez l'activer dans les paramètres pour utiliser le scan."
        )
    }
}
