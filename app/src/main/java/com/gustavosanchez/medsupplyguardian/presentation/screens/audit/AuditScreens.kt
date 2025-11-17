package com.gustavosanchez.medsupplyguardian.presentation.screens.audit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gustavosanchez.medsupplyguardian.MedSupplyGuardianApplication
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import com.gustavosanchez.medsupplyguardian.presentation.navigation.NavRoutes
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.AuditViewModel
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.AuditViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Audit start screen - Entry point for audit workflow.
 * 
 * Provides overview of audit process and button to begin.
 * Displays total item count and technician information.
 * 
 * @param navController Navigation controller
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditStartScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: AuditViewModel = viewModel(
        factory = AuditViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val allItems by viewModel.allItems.collectAsState(initial = emptyList())
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Start Audit",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Compliance Audit Workflow",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "This audit consists of 5 steps to ensure compliance with healthcare supply regulations.",
                style = MaterialTheme.typography.bodyLarge
            )
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Audit Steps",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Verify Stock Quantity", style = MaterialTheme.typography.bodyMedium)
                    Text("2. Check Expiry Dates", style = MaterialTheme.typography.bodyMedium)
                    Text("3. Validate Storage Conditions", style = MaterialTheme.typography.bodyMedium)
                    Text("4. Identify Missing/Damaged Items", style = MaterialTheme.typography.bodyMedium)
                    Text("5. Review and Generate Summary", style = MaterialTheme.typography.bodyMedium)
                }
            }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Total Items to Review",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${allItems.size}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    viewModel.resetAudit()
                    navController.navigate(NavRoutes.AuditStep.createRoute(1))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text("Begin Audit")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null
                )
            }
        }
    }
}

/**
 * Audit step screen - Handles individual audit steps with progress indicator.
 * 
 * Displays LinearProgressIndicator showing step completion progress.
 * Routes to appropriate step content based on step number.
 * 
 * @param navController Navigation controller
 * @param stepNumber Current step (1-5)
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditStepScreen(navController: NavController, stepNumber: Int) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: AuditViewModel = viewModel(
        factory = AuditViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val progress = stepNumber / 5f
    
    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Audit Step $stepNumber of 5",
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
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    ) { paddingValues ->
        when (stepNumber) {
            1 -> StepOneContent(navController, viewModel, paddingValues)
            2 -> StepTwoContent(navController, viewModel, paddingValues)
            3 -> StepThreeContent(navController, viewModel, paddingValues)
            4 -> StepFourContent(navController, viewModel, paddingValues)
            5 -> StepFiveContent(navController, viewModel, paddingValues)
            else -> {}
        }
    }
}

/**
 * Step 1: Verify Stock Quantity
 * 
 * Displays list of items with current quantities highlighted if critical.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StepOneContent(
    navController: NavController,
    viewModel: AuditViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val allItems by viewModel.allItems.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Verify Stock Quantity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Review current quantities and identify critical shortages.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(allItems) { item ->
                StepOneItemCard(item)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate(NavRoutes.AuditStep.createRoute(2)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Next Step")
        }
    }
}

@Composable
fun StepOneItemCard(item: SupplyItem) {
    val isCritical = item.riskLevel == "Critical"
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isCritical) {
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        } else {
            CardDefaults.elevatedCardColors()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Current: ${item.currentQuantity}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Min: ${item.minimumRequired}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Step 2: Check Expiry Dates
 * 
 * Displays items with expiry dates highlighted by urgency.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StepTwoContent(
    navController: NavController,
    viewModel: AuditViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val expiringItems by viewModel.expiringItems.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Check Expiry Dates",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Review items expiring within 30 days.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(expiringItems) { item ->
                StepTwoItemCard(item)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { navController.navigate(NavRoutes.AuditStep.createRoute(3)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Next Step")
        }
    }
}

@Composable
fun StepTwoItemCard(item: SupplyItem) {
    val now = System.currentTimeMillis()
    val sevenDays = 7L * 24 * 60 * 60 * 1000
    val isCriticalExpiry = item.expiryDate?.let { it - now <= sevenDays } ?: false
    
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = if (isCriticalExpiry) {
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        } else {
            CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            item.expiryDate?.let { timestamp ->
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val expiryDateString = dateFormat.format(Date(timestamp))
                val daysUntilExpiry = ((timestamp - now) / (24 * 60 * 60 * 1000)).toInt()
                
                Text(
                    text = "Expires: $expiryDateString",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "$daysUntilExpiry days remaining",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Step 3: Validate Storage Conditions
 * 
 * Displays checklist of storage validation items.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StepThreeContent(
    navController: NavController,
    viewModel: AuditViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val storageConditions by viewModel.storageConditions.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Validate Storage Conditions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Verify that all storage requirements are met.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                storageConditions.forEach { (condition, checked) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = { viewModel.updateStorageCondition(condition, it) }
                        )
                        Text(
                            text = condition,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { navController.navigate(NavRoutes.AuditStep.createRoute(4)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Next Step")
        }
    }
}

/**
 * Step 4: Identify Missing/Damaged Items
 * 
 * Allows selection of item status and optional comments.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StepFourContent(
    navController: NavController,
    viewModel: AuditViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val selectedOption by viewModel.missingDamagedSelection.collectAsState()
    val comments by viewModel.comments.collectAsState()
    val options = listOf("None", "Missing", "Damaged", "Both")
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Missing/Damaged Items",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Identify items that are missing or damaged.",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Item Status",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = selectedOption == option,
                    onClick = { viewModel.setMissingDamagedSelection(option) },
                    label = { Text(option) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = comments,
            onValueChange = { viewModel.updateComments(it) },
            label = { Text("Comments (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { navController.navigate(NavRoutes.AuditStep.createRoute(5)) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Next Step")
        }
    }
}

/**
 * Step 5: Review and Summary
 * 
 * Displays final audit summary with navigation to complete summary screen.
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun StepFiveContent(
    navController: NavController,
    viewModel: AuditViewModel,
    paddingValues: androidx.compose.foundation.layout.PaddingValues
) {
    val allItems by viewModel.allItems.collectAsState(initial = emptyList())
    val criticalItems by viewModel.criticalItems.collectAsState(initial = emptyList())
    val expiringItems by viewModel.expiringItems.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "Review Summary",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Audit Overview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                SummaryRow("Total Items Reviewed", "${allItems.size}")
                SummaryRow("Items Failing Compliance", "${criticalItems.size}")
                SummaryRow("Items Expiring Soon", "${expiringItems.size}")
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { navController.navigate(NavRoutes.AuditSummary.route) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Complete Audit")
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Audit Summary Screen with upload simulation.
 * 
 * Displays complete audit report and simulates upload with overlay progress indicator.
 * 
 * @param navController Navigation controller
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditSummaryScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: AuditViewModel = viewModel(
        factory = AuditViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val allItems by viewModel.allItems.collectAsState(initial = emptyList())
    val criticalItems by viewModel.criticalItems.collectAsState(initial = emptyList())
    val expiringItems by viewModel.expiringItems.collectAsState(initial = emptyList())
    val isUploading by viewModel.isUploading.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Audit Summary",
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Audit Complete",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = viewModel.getAuditDateTime(),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Summary Statistics",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow("Total Items Reviewed", "${allItems.size}")
                        SummaryRow("Items Failing Compliance", "${criticalItems.size}")
                        SummaryRow("Items Expiring Soon", "${expiringItems.size}")
                    }
                }
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Technician Information",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        SummaryRow("Name", viewModel.getTechnicianName())
                        SummaryRow("ID", viewModel.getTechnicianId())
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                FilledTonalButton(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.simulateUpload {
                                navController.navigate(NavRoutes.Home.route) {
                                    popUpTo(NavRoutes.Home.route) { inclusive = false }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Upload Audit Results")
                }
            }
        }
        
        if (isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Uploading audit report...",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
