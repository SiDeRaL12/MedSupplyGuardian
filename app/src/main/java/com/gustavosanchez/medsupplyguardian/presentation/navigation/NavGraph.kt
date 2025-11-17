package com.gustavosanchez.medsupplyguardian.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gustavosanchez.medsupplyguardian.presentation.screens.home.HomeScreen
import com.gustavosanchez.medsupplyguardian.presentation.screens.supplies.SuppliesListScreen
import com.gustavosanchez.medsupplyguardian.presentation.screens.details.SupplyDetailScreen
import com.gustavosanchez.medsupplyguardian.presentation.screens.audit.AuditStartScreen
import com.gustavosanchez.medsupplyguardian.presentation.screens.audit.AuditStepScreen
import com.gustavosanchez.medsupplyguardian.presentation.screens.audit.AuditSummaryScreen
import com.gustavosanchez.medsupplyguardian.presentation.screens.settings.SettingsScreen

/**
 * Navigation graph configuration for MedSupply Guardian application.
 * 
 * This composable defines the complete navigation structure including all
 * destinations, route parameters, and screen compositions. It uses Jetpack
 * Navigation Compose for type-safe navigation between screens.
 * 
 * Navigation Features:
 * - Type-safe route definitions
 * - Parameter passing for detail screens
 * - Centralized navigation configuration
 * - Support for deep linking (future enhancement)
 * 
 * Route Structure:
 * - Home: Dashboard with navigation
 * - Supplies: Inventory list with search/filter
 * - SupplyDetail: Individual item details with ID parameter
 * - AuditStart: Audit workflow initialization
 * - AuditStep: Multi-step audit with step number parameter
 * - AuditSummary: Audit completion and upload
 * - Settings: User preferences management
 * 
 * @param navController NavHostController for navigation operations
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route
    ) {
        // Home Screen
        composable(route = NavRoutes.Home.route) {
            HomeScreen(navController = navController)
        }
        
        // Supplies List Screen
        composable(route = NavRoutes.Supplies.route) {
            SuppliesListScreen(navController = navController)
        }
        
        // Supply Detail Screen with ID parameter
        composable(
            route = NavRoutes.SupplyDetail.route,
            arguments = listOf(
                navArgument("itemId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
            SupplyDetailScreen(
                navController = navController,
                itemId = itemId
            )
        }
        
        // Audit Start Screen
        composable(route = NavRoutes.AuditStart.route) {
            AuditStartScreen(navController = navController)
        }
        
        // Audit Step Screen with step number parameter
        composable(
            route = NavRoutes.AuditStep.route,
            arguments = listOf(
                navArgument("stepNumber") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val stepNumber = backStackEntry.arguments?.getInt("stepNumber") ?: 1
            AuditStepScreen(
                navController = navController,
                stepNumber = stepNumber
            )
        }
        
        // Audit Summary Screen
        composable(route = NavRoutes.AuditSummary.route) {
            AuditSummaryScreen(navController = navController)
        }
        
        // Settings Screen
        composable(route = NavRoutes.Settings.route) {
            SettingsScreen(navController = navController)
        }
    }
}
