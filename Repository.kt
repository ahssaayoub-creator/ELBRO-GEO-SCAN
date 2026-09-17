package com.elbro.geoscan.data

import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for Scan/Measurement persistence.
 * Wraps the Room DAOs so ViewModels never touch AppDatabase directly.
 */
class Repository(private val db: AppDatabase) {

    private val scanDao = db.scanDao()
    private val measurementDao = db.measurementDao()

    fun observeScans(): Flow<List<Scan>> = scanDao.getAllScans()

    fun observeScan(scanId: Long): Flow<Scan?> = scanDao.observeScanById(scanId)

    fun observeMeasurements(scanId: Long): Flow<List<Measurement>> =
        measurementDao.getMeasurementsForScan(scanId)

    suspend fun createScan(name: String): Long {
        return scanDao.insert(
            Scan(name = name, startTimestamp = System.currentTimeMillis())
        )
    }

    suspend fun finishScan(scan: Scan) {
        scanDao.update(scan)
    }

    suspend fun addMeasurement(measurement: Measurement) {
        measurementDao.insert(measurement)
    }

    suspend fun getMeasurementsOnce(scanId: Long): List<Measurement> =
        measurementDao.getMeasurementsForScanOnce(scanId)

    suspend fun getScan(scanId: Long): Scan? = scanDao.getScanById(scanId)

    suspend fun deleteScan(scan: Scan) {
        measurementDao.deleteForScan(scan.id)
        scanDao.delete(scan)
    }

    suspend fun averageMagnitude(scanId: Long): Double? =
        measurementDao.getAverageMagnitude(scanId)
}
