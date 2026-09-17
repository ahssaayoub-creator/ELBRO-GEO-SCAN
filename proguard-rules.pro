# ELBRO GEO SCAN - ProGuard / R8 rules

# Room
-keep class androidx.room.** { *; }
-keep @androidx.room.Entity class * { *; }
-dontwarn androidx.room.paging.**

# Keep data model classes used by Room + JSON export
-keep class com.elbro.geoscan.data.** { *; }

# Kotlin coroutines
-dontwarn kotlinx.coroutines.**

# Google Play services / Maps
-dontwarn com.google.android.gms.**
