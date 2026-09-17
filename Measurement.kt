package com.elbro.geoscan.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class AnomalyLevel {
    NORMAL, LOW, MEDIUM, HIGH
}

/**
 * A single point measurement: real magnetometer reading (Bx, By, Bz)
 * paired with the real GPS fix at the same instant.
 */
@Entity(
    tableName = "measurement",
    foreignKeys = [
        ForeignKey(
            entity = Scan::class,
            parentColumns = ["id"],
            childColumns = ["scanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("scanId")]
)
data class Measurement(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val scanId: Long,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val bx: Float,
    val by: Float,
    val bz: Float,
    val magnitude: Double,
    val anomalyScore: Double,
    val anomalyLevel: AnomalyLevel
)
