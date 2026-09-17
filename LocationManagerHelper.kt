package com.elbro.geoscan.sensors

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

data class GpsReading(
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val accuracy: Float,
    val timestamp: Long
)

/**
 * Wraps the REAL device GPS via FusedLocationProviderClient. Requires
 * ACCESS_FINE_LOCATION to be granted by the caller before use — see
 * ScanViewModel / ScanScreen for the runtime-permission flow.
 */
class LocationManagerHelper(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    fun isGpsProviderEnabled(): Boolean {
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    @SuppressLint("MissingPermission") // permission is checked by the caller (ScanScreen) before collecting
    fun locationUpdates(intervalMillis: Long = 2000L): Flow<GpsReading> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMillis)
            .setMinUpdateIntervalMillis(intervalMillis / 2)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val location = result.lastLocation ?: return
                trySend(
                    GpsReading(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        altitude = location.altitude,
                        accuracy = location.accuracy,
                        timestamp = location.time
                    )
                )
            }
        }

        fusedClient.requestLocationUpdates(request, callback, context.mainLooper)

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
        }
    }
}
