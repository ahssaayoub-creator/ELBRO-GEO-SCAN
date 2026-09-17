package com.elbro.geoscan.export

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.elbro.geoscan.data.Measurement
import com.elbro.geoscan.data.Scan
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileWriter

/**
 * Builds real CSV / JSON files for a scan and launches the Android Share Sheet,
 * as required ("Utiliser Android Share Sheet").
 */
object ExportUtils {

    fun exportCsv(context: Context, scan: Scan, measurements: List<Measurement>): Uri2Intent {
        val file = File(context.cacheDir, "elbro_geoscan_${scan.id}.csv")
        FileWriter(file).use { writer ->
            writer.append("timestamp,latitude,longitude,altitude,accuracy,bx,by,bz,magnitude,anomalyScore,anomalyLevel\n")
            measurements.forEach { m ->
                writer.append(
                    "${m.timestamp},${m.latitude},${m.longitude},${m.altitude},${m.accuracy}," +
                        "${m.bx},${m.by},${m.bz},${m.magnitude},${m.anomalyScore},${m.anomalyLevel}\n"
                )
            }
        }
        return shareFile(context, file, "text/csv")
    }

    fun exportJson(context: Context, scan: Scan, measurements: List<Measurement>): Uri2Intent {
        val root = JSONObject()
        root.put("scanId", scan.id)
        root.put("scanName", scan.name)
        root.put("startTimestamp", scan.startTimestamp)
        root.put("endTimestamp", scan.endTimestamp)
        root.put("baselineMagnitude", scan.baselineMagnitude)

        val array = JSONArray()
        measurements.forEach { m ->
            val obj = JSONObject()
            obj.put("timestamp", m.timestamp)
            obj.put("latitude", m.latitude)
            obj.put("longitude", m.longitude)
            obj.put("altitude", m.altitude)
            obj.put("accuracy", m.accuracy)
            obj.put("bx", m.bx)
            obj.put("by", m.by)
            obj.put("bz", m.bz)
            obj.put("magnitude", m.magnitude)
            obj.put("anomalyScore", m.anomalyScore)
            obj.put("anomalyLevel", m.anomalyLevel.name)
            array.put(obj)
        }
        root.put("measurements", array)

        val file = File(context.cacheDir, "elbro_geoscan_${scan.id}.json")
        file.writeText(root.toString(2))
        return shareFile(context, file, "application/json")
    }

    private fun shareFile(context: Context, file: File, mimeType: String): Uri2Intent {
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return Uri2Intent(uri.toString(), Intent.createChooser(intent, "Partager l'export ELBRO GEO SCAN"))
    }
}

/** Small holder so callers can both display the file path and launch the chooser. */
data class Uri2Intent(val uriString: String, val chooserIntent: Intent)
