package com.gustavosanchez.medsupplyguardian.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gustavosanchez.medsupplyguardian.MedSupplyGuardianApplication
import com.gustavosanchez.medsupplyguardian.presentation.navigation.NavRoutes
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModel
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModelFactory

/**
 * Home screen displaying the main dashboard for MedSupply Guardian.
 * 
 * This screen serves as the central hub of the application, providing:
 * - Personalized greeting using staff name from SharedPreferences
 * - Critical stock alerts card (errorContainer color scheme)
 * - Expiring items card (secondaryContainer color scheme)
 * - Navigation buttons to core features
 * 
 * UI Components:
 * - CenterAlignedTopAppBar with app title
 * - Greeting section with staff name (titleLarge typography)
 * - Two alert cards (ElevatedCard with color-coded backgrounds)
 * - Three action buttons (FilledButton, FilledTonalButton, OutlinedButton)
 * 
 * Layout Requirements:
 * - 16dp padding throughout
 * - 16dp spacing between buttons
 * - Vertical arrangement of components
 * 
 * @param navController Navigation controller for screen transitions
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val userPreferences by viewModel.userPreferences.collectAsState()
    val criticalItems by viewModel.criticalItems.collectAsState()
    val expiringItems by viewModel.expiringItems.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "MedSupply Guardian",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting Section
            GreetingSection(staffName = userPreferences.staffName)
            
            // Dashboard Alert Cards
            CriticalItemsCard(criticalCount = criticalItems.size)
            ExpiringItemsCard(expiringCount = expiringItems.size)
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Action Buttons
            ActionButtons(navController = navController)
        }
    }
}

/**
 * Displays a personalized greeting with the staff member's name.
 * 
 * Shows "Welcome, [Staff Name]" if name is set, otherwise shows generic greeting.
 * Uses titleLarge typography as specified in requirements.
 * 
 * @param staffName Name from user preferences, may be empty
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun GreetingSection(staffName: String) {
    val displayName = if (staffName.isNotEmpty()) staffName else "Technician"
    
    Text(
        text = "Welcome, $displayName",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(top = 16.dp)
    )
}

/**
 * Critical stock alerts card with errorContainer color scheme.
 * 
 * Displays the count of items requiring immediate attention due to:
 * - Current quantity below minimum required
 * - Expiry within 7 days
 * 
 * Card Styling:
 * - Background: errorContainer (red-tinted for urgency)
 * - Typography: titleMedium for title, bodyMedium for subtitle
 * - Full width with 16dp padding
 * 
 * @param criticalCount Number of critical items from database
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun CriticalItemsCard(criticalCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Critical Stock Alerts",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$criticalCount items require immediate attention",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

/**
 * Expiring items card with secondaryContainer color scheme.
 * 
 * Displays the count of items expiring within 30 days that need monitoring.
 * 
 * Card Styling:
 * - Background: secondaryContainer (teal-tinted for warning)
 * - Typography: titleMedium for title, bodyMedium for subtitle
 * - Full width with 16dp padding
 * 
 * @param expiringCount Number of expiring items from database
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun ExpiringItemsCard(expiringCount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Expiring Soon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "$expiringCount items expiring within 30 days",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Action buttons section with navigation to core features.
 * 
 * Displays three buttons with different visual styles:
 * - FilledButton: Primary action (View Supplies)
 * - FilledTonalButton: Secondary action (Start Audit)
 * - OutlinedButton: Tertiary action (Settings)
 * 
 * Button Requirements:
 * - 16dp spacing between buttons
 * - Full width
 * - 48dp minimum touch target height
 * - Leading icons for clarity
 * 
 * @param navController Navigation controller for routing
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun ActionButtons(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Primary Action: View Supplies
        Button(
            onClick = { navController.navigate(NavRoutes.Supplies.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Inventory,
                contentDescription = "View Supplies",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "View Supplies",
                style = MaterialTheme.typography.labelLarge
            )
        }
        
        // Secondary Action: Start Audit
        FilledTonalButton(
            onClick = { navController.navigate(NavRoutes.AuditStart.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Assignment,
                contentDescription = "Start Audit",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Start Audit",
                style = MaterialTheme.typography.labelLarge
            )
        }
        
        // Tertiary Action: Settings
        OutlinedButton(
            onClick = { navController.navigate(NavRoutes.Settings.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = "Settings",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
