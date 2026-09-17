package com.elbro.geoscan.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * One field survey session. A Scan groups all Measurement rows taken
 * between "Démarrer le scan" and "Arrêter le scan".
 */
@Entity(tableName = "scan")
data class Scan(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val startTimestamp: Long,
    var endTimestamp: Long? = null,
    var baselineMagnitude: Double? = null,
    var measurementCount: Int = 0,
    var maxAnomalyScore: Double = 0.0,
    var notes: String = ""
)
