package com.elbro.geoscan.logic

import com.elbro.geoscan.data.AnomalyLevel
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Pure, unit-testable magnetic-anomaly math.
 *
 * B = sqrt(Bx² + By² + Bz²)
 * baseline = running average of magnitude over previous measurements in the scan
 * anomalyScore = abs(currentMagnitude - baseline)
 *
 * IMPORTANT: this never labels a result as "Gold detected" / "Treasure detected" /
 * "Metal detected" — only "Magnetic anomaly detected" plus a severity level, per spec.
 */
object AnomalyCalculator {

    /** B = sqrt(Bx² + By² + Bz²) */
    fun magnitude(bx: Float, by: Float, bz: Float): Double {
        val bxD = bx.toDouble()
        val byD = by.toDouble()
        val bzD = bz.toDouble()
        return sqrt(bxD * bxD + byD * byD + bzD * bzD)
    }

    /** Running baseline = simple average of all prior magnitudes in the current scan. */
    fun computeBaseline(previousMagnitudes: List<Double>): Double {
        if (previousMagnitudes.isEmpty()) return 0.0
        return previousMagnitudes.sum() / previousMagnitudes.size
    }

    /** anomalyScore = abs(currentMagnitude - baseline) */
    fun anomalyScore(currentMagnitude: Double, baseline: Double): Double {
        if (baseline == 0.0) return 0.0
        return abs(currentMagnitude - baseline)
    }

    fun classify(score: Double, thresholds: Thresholds): AnomalyLevel {
        return when {
            score >= thresholds.highThreshold -> AnomalyLevel.HIGH
            score >= thresholds.mediumThreshold -> AnomalyLevel.MEDIUM
            score >= thresholds.lowThreshold -> AnomalyLevel.LOW
            else -> AnomalyLevel.NORMAL
        }
    }

    /**
     * Haversine distance in meters between two GPS points.
     * Used for building the field grid / route length.
     */
    fun gpsDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val earthRadius = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return earthRadius * c
    }
}
