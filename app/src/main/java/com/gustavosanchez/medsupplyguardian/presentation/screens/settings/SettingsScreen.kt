package com.gustavosanchez.medsupplyguardian.presentation.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gustavosanchez.medsupplyguardian.MedSupplyGuardianApplication
import com.gustavosanchez.medsupplyguardian.domain.model.UserPreferences
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModel
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModelFactory

/**
 * Settings screen for managing user preferences and application configuration.
 * 
 * This screen provides comprehensive settings management using SharedPreferences
 * for persistent storage across app restarts. All preference changes are saved
 * immediately and reflected in the UI.
 * 
 * Settings Categories:
 * - Staff Identity: Name and ID
 * - Display Preferences: Sorting mode
 * - Alert Configuration: Low stock threshold
 * - Appearance: Theme mode (light/dark)
 * - Notifications: Audit reminder interval
 * 
 * UI Components:
 * - OutlinedTextField: Text inputs (Staff Name, Staff ID, Reminder Hours)
 * - ExposedDropdownMenuBox: Sorting mode selection
 * - Slider: Alert threshold percentage
 * - Switch: Theme mode toggle
 * - FilledTonalButton: Save action
 * 
 * Layout Requirements:
 * - LazyColumn with 24dp vertical spacing
 * - 16dp content padding
 * - All inputs full width
 * - Save button always visible at bottom
 * 
 * @param navController Navigation controller for back navigation
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val currentPreferences by viewModel.userPreferences.collectAsState()
    
    var staffName by remember(currentPreferences.staffName) { 
        mutableStateOf(currentPreferences.staffName) 
    }
    var staffId by remember(currentPreferences.staffId) { 
        mutableStateOf(currentPreferences.staffId) 
    }
    var sortingMode by remember(currentPreferences.sortingMode) { 
        mutableStateOf(currentPreferences.sortingMode) 
    }
    var alertThreshold by remember(currentPreferences.alertThreshold) { 
        mutableFloatStateOf(currentPreferences.alertThreshold.toFloat()) 
    }
    var isDarkTheme by remember(currentPreferences.isDarkTheme) { 
        mutableStateOf(currentPreferences.isDarkTheme) 
    }
    var auditReminderHours by remember(currentPreferences.auditReminderHours) { 
        mutableStateOf(currentPreferences.auditReminderHours.toString()) 
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text(
                    text = "Staff Identity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                StaffNameInput(
                    value = staffName,
                    onValueChange = { staffName = it }
                )
            }
            
            item {
                StaffIdInput(
                    value = staffId,
                    onValueChange = { staffId = it }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Display Preferences",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                SortingModeDropdown(
                    selectedMode = sortingMode,
                    onModeSelected = { sortingMode = it }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Alert Configuration",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                AlertThresholdSlider(
                    value = alertThreshold,
                    onValueChange = { alertThreshold = it }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Appearance",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                ThemeModeSwitch(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { isDarkTheme = it }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            item {
                AuditReminderInput(
                    value = auditReminderHours,
                    onValueChange = { auditReminderHours = it }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
                SaveButton(
                    onClick = {
                        val updatedPreferences = UserPreferences(
                            staffName = staffName,
                            staffId = staffId,
                            sortingMode = sortingMode,
                            alertThreshold = alertThreshold.toInt(),
                            isDarkTheme = isDarkTheme,
                            auditReminderHours = auditReminderHours.toIntOrNull() ?: 24
                        )
                        viewModel.savePreferences(updatedPreferences)
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}

/**
 * Staff Name input field.
 * 
 * OutlinedTextField for entering the hospital staff member's full name.
 * This value persists in SharedPreferences and displays in the Home screen greeting.
 * 
 * @param value Current staff name value
 * @param onValueChange Callback when text changes
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StaffNameInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Staff Name") },
        placeholder = { Text("Enter your full name") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Staff ID input field.
 * 
 * OutlinedTextField for entering the unique staff identifier.
 * Used for audit record attribution and compliance tracking.
 * 
 * @param value Current staff ID value
 * @param onValueChange Callback when text changes
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StaffIdInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Staff ID") },
        placeholder = { Text("Enter your staff ID") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Sorting mode dropdown selector.
 * 
 * ExposedDropdownMenuBox allowing selection of supply list sorting order.
 * Options: Name, Category, Risk Level
 * 
 * @param selectedMode Currently selected sorting mode
 * @param onModeSelected Callback when mode is selected
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingModeDropdown(
    selectedMode: String,
    onModeSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val sortingOptions = listOf("Name", "Category", "Risk")
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedMode,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sorting Mode") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            textStyle = MaterialTheme.typography.bodyLarge
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            sortingOptions.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onModeSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Alert threshold slider.
 * 
 * Material 3 Slider for setting the low-stock alert percentage threshold.
 * Range: 0-100%
 * Displays current value as label above slider.
 * 
 * @param value Current threshold percentage
 * @param onValueChange Callback when slider value changes
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun AlertThresholdSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Alert Threshold",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${value.toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 0f..100f,
            steps = 19,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Alert when stock falls below this percentage of minimum required",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Theme mode switch.
 * 
 * Material 3 Switch for toggling between light and dark themes.
 * Changes reflect immediately throughout the application.
 * 
 * @param isDarkTheme Current theme state
 * @param onThemeChange Callback when switch is toggled
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun ThemeModeSwitch(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Dark Theme",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isDarkTheme) "Enabled" else "Disabled",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Switch(
            checked = isDarkTheme,
            onCheckedChange = onThemeChange
        )
    }
}

/**
 * Audit reminder interval input.
 * 
 * OutlinedTextField for setting the number of hours between audit reminders.
 * Accepts numeric input only.
 * 
 * @param value Current reminder interval in hours
 * @param onValueChange Callback when text changes
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun AuditReminderInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        label = { Text("Audit Reminder (Hours)") },
        placeholder = { Text("24") },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        supportingText = {
            Text("How often to remind you to perform audits")
        },
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Save settings button.
 * 
 * FilledTonalButton that persists all settings to SharedPreferences
 * and navigates back to the previous screen.
 * 
 * @param onClick Callback when button is clicked
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun SaveButton(onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = "Save Settings",
            style = MaterialTheme.typography.labelLarge
        )
    }
}
