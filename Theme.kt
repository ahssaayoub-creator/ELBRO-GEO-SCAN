package com.elbro.geoscan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val GeoScanDarkColorScheme = darkColorScheme(
    primary = AccentGreen,
    secondary = AccentAmber,
    error = AccentRed,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceElevated,
    onPrimary = BackgroundDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

/**
 * ELBRO GEO SCAN always renders in the dark, technical theme described in the spec,
 * regardless of system theme — this is a field-instrument look, not a general app.
 */
@Composable
fun ElbroGeoScanTheme(
    dynamicColor: Boolean = false, // intentionally ignored: always dark technical theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GeoScanDarkColorScheme,
        typography = GeoScanTypography,
        content = content
    )
}
