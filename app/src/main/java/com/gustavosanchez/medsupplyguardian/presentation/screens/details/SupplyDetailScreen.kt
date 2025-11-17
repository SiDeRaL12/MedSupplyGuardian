package com.gustavosanchez.medsupplyguardian.presentation.screens.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModel
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Supply detail screen displaying comprehensive item information.
 * 
 * This screen provides a detailed view of a single supply item with:
 * - Complete item information display
 * - Color-coded risk level indicator
 * - Quantity update functionality via dialog
 * - Input validation for quantity changes
 * - Immediate database persistence
 * 
 * UI Components:
 * - CenterAlignedTopAppBar: Navigation and title
 * - Card: Information display sections
 * - AssistChip: Risk level badge
 * - ExtendedFloatingActionButton: Quantity update trigger
 * - AlertDialog: Quantity adjustment interface
 * 
 * Layout Requirements:
 * - 16dp content padding
 * - 12dp spacing between elements
 * - Proper typography hierarchy
 * - Input validation with error states
 * 
 * @param navController Navigation controller for back navigation
 * @param itemId Unique identifier of the supply item to display
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplyDetailScreen(navController: NavController, itemId: Int) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val item by remember { viewModel.getItemById(itemId) }.collectAsState(initial = null)
    var showUpdateDialog by remember { mutableStateOf(false) }
    
    if (item == null) {
        LoadingDetailScreen()
        return
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = item!!.name,
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showUpdateDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Update quantity"
                    )
                },
                text = { Text("Update Quantity") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item?.let { supplyItem ->
                ItemDetailsCard(item = supplyItem)
                QuantityCard(item = supplyItem)
                LocationCard(item = supplyItem)
                ExpiryCard(item = supplyItem)
                
                if (showUpdateDialog) {
                    QuantityUpdateDialog(
                        currentQuantity = supplyItem.currentQuantity,
                        onDismiss = { showUpdateDialog = false },
                        onConfirm = { newQuantity ->
                            viewModel.updateItemQuantity(itemId, newQuantity)
                            showUpdateDialog = false
                        }
                    )
                }
            }
        }
    }
}

/**
 * Loading screen displayed while fetching item details.
 * 
 * Shows centered CircularProgressIndicator while data is retrieved
 * from the database.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun LoadingDetailScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Loading item details...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

/**
 * Card displaying basic item information.
 * 
 * Shows item name, category, and color-coded risk level chip.
 * 
 * @param item SupplyItem to display
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun ItemDetailsCard(item: SupplyItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Item Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            DetailRow(label = "Name", value = item.name)
            DetailRow(label = "Category", value = item.category)
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Risk Level",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                RiskLevelChip(riskLevel = item.riskLevel)
            }
        }
    }
}

/**
 * Card displaying quantity information.
 * 
 * Shows current quantity and minimum required with visual comparison.
 * 
 * @param item SupplyItem to display
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun QuantityCard(item: SupplyItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Quantity Details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Current",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.currentQuantity}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Minimum",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.minimumRequired}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Card displaying storage location information.
 * 
 * @param item SupplyItem to display
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun LocationCard(item: SupplyItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Storage Location",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = item.location,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Card displaying expiry date information.
 * 
 * Shows formatted expiry date if available, otherwise shows "No expiry date".
 * 
 * @param item SupplyItem to display
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun ExpiryCard(item: SupplyItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Expiry Information",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            val expiryText = item.expiryDate?.let { timestamp ->
                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())
                dateFormat.format(Date(timestamp))
            } ?: "No expiry date"
            
            Text(
                text = expiryText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

/**
 * Reusable detail row component.
 * 
 * Displays a label-value pair in a horizontal layout.
 * 
 * @param label Field label
 * @param value Field value
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Risk level indicator chip.
 * 
 * Color-coded AssistChip based on risk severity.
 * 
 * @param riskLevel Risk level string (Critical, Elevated, Normal)
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun RiskLevelChip(riskLevel: String) {
    val (containerColor, labelColor) = when (riskLevel) {
        "Critical" -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        "Elevated" -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    
    AssistChip(
        onClick = { },
        label = {
            Text(
                text = riskLevel,
                fontWeight = FontWeight.Bold
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = labelColor
        )
    )
}

/**
 * Quantity update dialog.
 * 
 * Material 3 AlertDialog for adjusting item quantity with:
 * - Manual text input field
 * - Increment and decrement buttons
 * - Input validation (no negative values)
 * - Error state display
 * - Confirm and Cancel actions
 * 
 * Validation Rules:
 * - Quantity must be numeric
 * - Quantity cannot be negative
 * - Empty input shows error
 * 
 * @param currentQuantity Current item quantity
 * @param onDismiss Callback when dialog is dismissed
 * @param onConfirm Callback when quantity update is confirmed
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun QuantityUpdateDialog(
    currentQuantity: Int,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var quantityText by remember { mutableStateOf(currentQuantity.toString()) }
    var isError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    fun validateQuantity(text: String): Boolean {
        return when {
            text.isEmpty() -> {
                isError = true
                errorMessage = "Quantity cannot be empty"
                false
            }
            text.toIntOrNull() == null -> {
                isError = true
                errorMessage = "Must be a valid number"
                false
            }
            text.toInt() < 0 -> {
                isError = true
                errorMessage = "Quantity cannot be negative"
                false
            }
            else -> {
                isError = false
                errorMessage = ""
                true
            }
        }
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Update Quantity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Adjust the current quantity for this item",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 0
                            if (current > 0) {
                                quantityText = (current - 1).toString()
                                validateQuantity(quantityText)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease quantity"
                        )
                    }
                    
                    OutlinedTextField(
                        value = quantityText,
                        onValueChange = { newValue ->
                            if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                                quantityText = newValue
                                validateQuantity(newValue)
                            }
                        },
                        modifier = Modifier.width(120.dp),
                        textStyle = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        singleLine = true,
                        isError = isError,
                        supportingText = if (isError) {
                            { Text(errorMessage) }
                        } else null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    
                    IconButton(
                        onClick = {
                            val current = quantityText.toIntOrNull() ?: 0
                            quantityText = (current + 1).toString()
                            validateQuantity(quantityText)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase quantity"
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validateQuantity(quantityText)) {
                        onConfirm(quantityText.toInt())
                    }
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
