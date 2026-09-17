package com.elbro.geoscan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.elbro.geoscan.ui.screens.AboutScreen
import com.elbro.geoscan.ui.screens.CalibrationScreen
import com.elbro.geoscan.ui.screens.HistoryScreen
import com.elbro.geoscan.ui.screens.HomeScreen
import com.elbro.geoscan.ui.screens.MapScreen
import com.elbro.geoscan.ui.screens.NewAnalysisScreen
import com.elbro.geoscan.ui.screens.ResultsScreen
import com.elbro.geoscan.ui.screens.ScanDetailScreen
import com.elbro.geoscan.ui.screens.ScanScreen
import com.elbro.geoscan.ui.screens.SettingsScreen
import com.elbro.geoscan.ui.theme.ElbroGeoScanTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElbroGeoScanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GeoScanNavHost()
                }
            }
        }
    }
}

@Composable
fun GeoScanNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                onNewAnalysis = { navController.navigate(Routes.NEW_ANALYSIS) },
                onHistory = { navController.navigate(Routes.HISTORY) },
                onSettings = { navController.navigate(Routes.SETTINGS) },
                onAbout = { navController.navigate(Routes.ABOUT) }
            )
        }

        composable(Routes.NEW_ANALYSIS) {
            NewAnalysisScreen(
                onProceedToCalibration = { scanName ->
                    navController.navigate(Routes.calibration(scanName))
                }
            )
        }

        composable(
            route = Routes.CALIBRATION,
            arguments = listOf(navArgument("scanName") { type = NavType.StringType })
        ) { backStackEntry ->
            val scanName = backStackEntry.arguments?.getString("scanName") ?: "Scan"
            CalibrationScreen(
                scanName = scanName,
                onCalibrationDone = { navController.navigate(Routes.scan(scanName)) }
            )
        }

        composable(
            route = Routes.SCAN,
            arguments = listOf(navArgument("scanName") { type = NavType.StringType })
        ) { backStackEntry ->
            val scanName = backStackEntry.arguments?.getString("scanName") ?: "Scan"
            ScanScreen(
                scanName = scanName,
                onScanFinished = { scanId -> navController.navigate(Routes.results(scanId)) }
            )
        }

        composable(
            route = Routes.RESULTS,
            arguments = listOf(navArgument("scanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getLong("scanId") ?: 0L
            ResultsScreen(
                scanId = scanId,
                onViewMap = { navController.navigate(Routes.map(scanId)) },
                onBackToHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Routes.MAP,
            arguments = listOf(navArgument("scanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getLong("scanId") ?: 0L
            MapScreen(scanId = scanId)
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                onOpenScan = { scanId -> navController.navigate(Routes.scanDetails(scanId)) }
            )
        }

        composable(
            route = Routes.SCAN_DETAILS,
            arguments = listOf(navArgument("scanId") { type = NavType.LongType })
        ) { backStackEntry ->
            val scanId = backStackEntry.arguments?.getLong("scanId") ?: 0L
            ScanDetailScreen(
                scanId = scanId,
                onViewMap = { navController.navigate(Routes.map(scanId)) }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen()
        }

        composable(Routes.ABOUT) {
            AboutScreen()
        }
    }
}
