package com.gustavosanchez.medsupplyguardian.presentation.screens.supplies

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Supplies list screen placeholder.
 * 
 * @param navController Navigation controller
 * @author Gustavo Sanchez
 */
@Composable
fun SuppliesListScreen(navController: NavController) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Supplies List Screen - To be implemented")
    }
}
