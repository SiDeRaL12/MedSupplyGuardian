package com.gustavosanchez.medsupplyguardian.presentation.navigation

/**
 * Navigation route definitions for MedSupply Guardian application.
 * 
 * This sealed class defines all navigation destinations in the app using
 * type-safe route constants. Routes support parameter passing for dynamic
 * navigation to detail screens.
 * 
 * Route Structure:
 * - Static routes: Simple string paths
 * - Dynamic routes: Paths with parameters using curly brace syntax
 * 
 * Usage Example:
 * ```
 * navController.navigate(NavRoutes.Home.route)
 * navController.navigate("supply/5") // Navigate to supply with ID 5
 * ```
 * 
 * @author Gustavo Sanchez
 */
sealed class NavRoutes(val route: String) {
    
    /**
     * Home dashboard screen route.
     * Displays summary cards and navigation buttons.
     */
    object Home : NavRoutes("home")
    
    /**
     * Supplies list screen route.
     * Shows all inventory items with search and filter capabilities.
     */
    object Supplies : NavRoutes("supplies")
    
    /**
     * Supply detail screen route with item ID parameter.
     * 
     * @property routeWithArgs Route template with parameter placeholder
     */
    object SupplyDetail : NavRoutes("supply/{itemId}") {
        /**
         * Creates a route with the specified item ID.
         * 
         * @param itemId The unique identifier of the supply item
         * @return Formatted route string with ID substituted
         */
        fun createRoute(itemId: Int) = "supply/$itemId"
    }
    
    /**
     * Audit workflow start screen route.
     * Initializes the multi-step audit process.
     */
    object AuditStart : NavRoutes("audit/start")
    
    /**
     * Audit step screen route with step number parameter.
     * 
     * @property routeWithArgs Route template with step parameter placeholder
     */
    object AuditStep : NavRoutes("audit/step/{stepNumber}") {
        /**
         * Creates a route with the specified step number.
         * 
         * @param stepNumber The audit step index (1-5)
         * @return Formatted route string with step number substituted
         */
        fun createRoute(stepNumber: Int) = "audit/step/$stepNumber"
    }
    
    /**
     * Audit summary screen route.
     * Displays complete audit report and upload functionality.
     */
    object AuditSummary : NavRoutes("audit/summary")
    
    /**
     * Settings screen route.
     * Manages user preferences and application configuration.
     */
    object Settings : NavRoutes("settings")
}
