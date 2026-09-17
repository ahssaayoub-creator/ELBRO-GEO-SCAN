package com.elbro.geoscan.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Insert
    suspend fun insert(scan: Scan): Long

    @Update
    suspend fun update(scan: Scan)

    @Delete
    suspend fun delete(scan: Scan)

    @Query("SELECT * FROM scan ORDER BY startTimestamp DESC")
    fun getAllScans(): Flow<List<Scan>>

    @Query("SELECT * FROM scan WHERE id = :scanId")
    suspend fun getScanById(scanId: Long): Scan?

    @Query("SELECT * FROM scan WHERE id = :scanId")
    fun observeScanById(scanId: Long): Flow<Scan?>
}
