package com.elbro.geoscan

object Routes {
    const val HOME = "home"
    const val NEW_ANALYSIS = "new_analysis"
    const val CALIBRATION = "calibration/{scanName}"
    const val SCAN = "scan/{scanName}"
    const val MAP = "map/{scanId}"
    const val RESULTS = "results/{scanId}"
    const val HISTORY = "history"
    const val SCAN_DETAILS = "scan_details/{scanId}"
    const val SETTINGS = "settings"
    const val ABOUT = "about"

    fun calibration(scanName: String) = "calibration/$scanName"
    fun scan(scanName: String) = "scan/$scanName"
    fun map(scanId: Long) = "map/$scanId"
    fun results(scanId: Long) = "results/$scanId"
    fun scanDetails(scanId: Long) = "scan_details/$scanId"
}
