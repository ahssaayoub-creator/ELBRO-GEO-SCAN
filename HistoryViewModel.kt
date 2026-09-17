package com.elbro.geoscan.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elbro.geoscan.data.AppDatabase
import com.elbro.geoscan.data.Measurement
import com.elbro.geoscan.data.Repository
import com.elbro.geoscan.data.Scan
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository(AppDatabase.getInstance(application))

    val scans: StateFlow<List<Scan>> = repository.observeScans()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedScanMeasurements = MutableStateFlow<List<Measurement>>(emptyList())
    val selectedScanMeasurements: StateFlow<List<Measurement>> = _selectedScanMeasurements

    private val _selectedScan = MutableStateFlow<Scan?>(null)
    val selectedScan: StateFlow<Scan?> = _selectedScan

    fun loadScanDetails(scanId: Long) {
        viewModelScope.launch {
            _selectedScan.value = repository.getScan(scanId)
            _selectedScanMeasurements.value = repository.getMeasurementsOnce(scanId)
        }
    }

    fun deleteScan(scan: Scan) {
        viewModelScope.launch {
            repository.deleteScan(scan)
        }
    }
}
