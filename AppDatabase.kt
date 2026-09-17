package com.elbro.geoscan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromAnomalyLevel(level: AnomalyLevel): String = level.name

    @TypeConverter
    fun toAnomalyLevel(value: String): AnomalyLevel = AnomalyLevel.valueOf(value)
}

@Database(
    entities = [Scan::class, Measurement::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanDao(): ScanDao
    abstract fun measurementDao(): MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "geoscan_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
