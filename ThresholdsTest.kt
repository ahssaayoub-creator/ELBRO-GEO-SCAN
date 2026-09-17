package com.elbro.geoscan

import com.elbro.geoscan.logic.Thresholds
import org.junit.Assert.assertEquals
import org.junit.Test

class ThresholdsTest {

    @Test
    fun `default thresholds are strictly increasing`() {
        val thresholds = Thresholds()
        assertEquals(true, thresholds.lowThreshold < thresholds.mediumThreshold)
        assertEquals(true, thresholds.mediumThreshold < thresholds.highThreshold)
    }

    @Test
    fun `custom thresholds are accepted when valid`() {
        val thresholds = Thresholds(lowThreshold = 1.0, mediumThreshold = 2.0, highThreshold = 3.0)
        assertEquals(1.0, thresholds.lowThreshold, 0.0)
        assertEquals(2.0, thresholds.mediumThreshold, 0.0)
        assertEquals(3.0, thresholds.highThreshold, 0.0)
    }
}
