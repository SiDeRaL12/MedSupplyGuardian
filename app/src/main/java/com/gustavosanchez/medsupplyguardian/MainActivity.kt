package com.gustavosanchez.medsupplyguardian

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.gustavosanchez.medsupplyguardian.presentation.navigation.NavGraph
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModel
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModelFactory
import com.gustavosanchez.medsupplyguardian.ui.theme.MedSupplyGuardianTheme

/**
 * Main entry point for MedSupply Guardian application.
 * 
 * This activity serves as the host for the entire Compose-based UI.
 * It initializes the Material Design 3 theme, provides the navigation
 * controller, and manages the ViewModel lifecycle.
 * 
 * Features:
 * - Edge-to-edge display support
 * - ViewModel initialization with dependency injection
 * - Theme switching based on user preferences
 * - Startup loading indicator
 * - Navigation graph integration
 * 
 * @author Gustavo Sanchez
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val application = application as MedSupplyGuardianApplication
            val viewModel: MainViewModel = viewModel(
                factory = MainViewModelFactory(
                    repository = application.repository,
                    preferencesManager = application.preferencesManager
                )
            )
            
            val userPreferences by viewModel.userPreferences.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            
            MedSupplyGuardianTheme(darkTheme = userPreferences.isDarkTheme) {
                 val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (isLoading) {
                        LoadingScreen()
                    } else {
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavGraph(navController = navController)
                        }
                    }
                }
            }
        }
    }
}

/**
 * Loading screen displayed during app initialization.
 * 
 * Shows a centered circular progress indicator while the database
 * is being initialized and sample data is being loaded.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
