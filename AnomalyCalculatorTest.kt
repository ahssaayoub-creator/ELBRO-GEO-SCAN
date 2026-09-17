package com.elbro.geoscan

import com.elbro.geoscan.data.AnomalyLevel
import com.elbro.geoscan.logic.AnomalyCalculator
import com.elbro.geoscan.logic.Thresholds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class AnomalyCalculatorTest {

    @Test
    fun `magnitude computes sqrt of sum of squares`() {
        val result = AnomalyCalculator.magnitude(3f, 4f, 0f)
        assertEquals(5.0, result, 0.0001)
    }

    @Test
    fun `magnitude handles negative components`() {
        val result = AnomalyCalculator.magnitude(-3f, -4f, 0f)
        assertEquals(5.0, result, 0.0001)
    }

    @Test
    fun `magnitude with three nonzero axes matches manual calculation`() {
        val bx = 10f; val by = 20f; val bz = 30f
        val expected = sqrt((bx * bx + by * by + bz * bz).toDouble())
        assertEquals(expected, AnomalyCalculator.magnitude(bx, by, bz), 0.0001)
    }

    @Test
    fun `baseline is average of previous magnitudes`() {
        val baseline = AnomalyCalculator.computeBaseline(listOf(10.0, 20.0, 30.0))
        assertEquals(20.0, baseline, 0.0001)
    }

    @Test
    fun `baseline is zero when no previous measurements`() {
        val baseline = AnomalyCalculator.computeBaseline(emptyList())
        assertEquals(0.0, baseline, 0.0001)
    }

    @Test
    fun `anomaly score is absolute difference from baseline`() {
        val score = AnomalyCalculator.anomalyScore(currentMagnitude = 50.0, baseline = 40.0)
        assertEquals(10.0, score, 0.0001)
    }

    @Test
    fun `anomaly score is zero when baseline is zero (no history yet)`() {
        val score = AnomalyCalculator.anomalyScore(currentMagnitude = 50.0, baseline = 0.0)
        assertEquals(0.0, score, 0.0001)
    }

    @Test
    fun `classify returns NORMAL below low threshold`() {
        val level = AnomalyCalculator.classify(1.0, Thresholds())
        assertEquals(AnomalyLevel.NORMAL, level)
    }

    @Test
    fun `classify returns LOW at low threshold`() {
        val thresholds = Thresholds()
        val level = AnomalyCalculator.classify(thresholds.lowThreshold, thresholds)
        assertEquals(AnomalyLevel.LOW, level)
    }

    @Test
    fun `classify returns MEDIUM at medium threshold`() {
        val thresholds = Thresholds()
        val level = AnomalyCalculator.classify(thresholds.mediumThreshold, thresholds)
        assertEquals(AnomalyLevel.MEDIUM, level)
    }

    @Test
    fun `classify returns HIGH at and above high threshold`() {
        val thresholds = Thresholds()
        assertEquals(AnomalyLevel.HIGH, AnomalyCalculator.classify(thresholds.highThreshold, thresholds))
        assertEquals(AnomalyLevel.HIGH, AnomalyCalculator.classify(thresholds.highThreshold + 100, thresholds))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `thresholds reject non-increasing values`() {
        Thresholds(lowThreshold = 10.0, mediumThreshold = 5.0, highThreshold = 20.0)
    }

    @Test
    fun `gps distance between identical points is zero`() {
        val distance = AnomalyCalculator.gpsDistanceMeters(33.5731, -7.5898, 33.5731, -7.5898)
        assertEquals(0.0, distance, 0.001)
    }

    @Test
    fun `gps distance between known points is approximately correct`() {
        // Casablanca city center to Rabat city center, ~85-90 km apart
        val distance = AnomalyCalculator.gpsDistanceMeters(33.5731, -7.5898, 34.0209, -6.8416)
        assertTrue("expected roughly 85-95km, got ${distance / 1000} km", distance in 80000.0..100000.0)
    }
}
