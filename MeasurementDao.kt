package com.elbro.geoscan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Insert
    suspend fun insert(measurement: Measurement): Long

    @Insert
    suspend fun insertAll(measurements: List<Measurement>)

    @Query("SELECT * FROM measurement WHERE scanId = :scanId ORDER BY timestamp ASC")
    fun getMeasurementsForScan(scanId: Long): Flow<List<Measurement>>

    @Query("SELECT * FROM measurement WHERE scanId = :scanId ORDER BY timestamp ASC")
    suspend fun getMeasurementsForScanOnce(scanId: Long): List<Measurement>

    @Query("SELECT AVG(magnitude) FROM measurement WHERE scanId = :scanId")
    suspend fun getAverageMagnitude(scanId: Long): Double?

    @Query("SELECT COUNT(*) FROM measurement WHERE scanId = :scanId")
    suspend fun getMeasurementCount(scanId: Long): Int

    @Query("DELETE FROM measurement WHERE scanId = :scanId")
    suspend fun deleteForScan(scanId: Long)
}
