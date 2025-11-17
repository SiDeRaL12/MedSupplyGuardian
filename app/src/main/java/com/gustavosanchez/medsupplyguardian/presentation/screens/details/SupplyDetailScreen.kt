package com.gustavosanchez.medsupplyguardian.presentation.screens.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Supply detail screen placeholder.
 * 
 * @param navController Navigation controller
 * @param itemId Supply item identifier
 * @author Gustavo Sanchez
 */
@Composable
fun SupplyDetailScreen(navController: NavController, itemId: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Supply Detail Screen - Item ID: $itemId")
    }
}
