package com.gustavosanchez.medsupplyguardian.presentation.screens.supplies

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.gustavosanchez.medsupplyguardian.MedSupplyGuardianApplication
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import com.gustavosanchez.medsupplyguardian.presentation.navigation.NavRoutes
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModel
import com.gustavosanchez.medsupplyguardian.presentation.viewmodel.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Supplies list screen displaying searchable and filterable inventory.
 * 
 * This screen provides comprehensive inventory management with:
 * - Real-time search by item name
 * - Multi-criteria filtering (Category, Location, Risk Level)
 * - LazyColumn rendering for performance
 * - Color-coded risk indicators
 * - Tap-to-view details navigation
 * 
 * UI Components:
 * - OutlinedTextField: Search bar with icon
 * - FilterChip: Category, Location, and Risk filters
 * - LazyColumn: Scrollable list of items
 * - ElevatedCard: Individual item cards
 * - AssistChip: Color-coded risk level badges
 * 
 * Layout Requirements:
 * - 16dp content padding
 * - 12dp spacing between cards
 * - Full width cards
 * - Proper Material 3 elevation
 * 
 * @param navController Navigation controller for screen transitions
 * 
 * @author Gustavo Sanchez
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuppliesListScreen(navController: NavController) {
    val context = LocalContext.current
    val application = context.applicationContext as MedSupplyGuardianApplication
    
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModelFactory(
            repository = application.repository,
            preferencesManager = application.preferencesManager
        )
    )
    
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val selectedRiskLevel by viewModel.selectedRiskLevel.collectAsState()
    val filteredItems by viewModel.filteredItems.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Supply Inventory",
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
                .padding(16.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onClearClick = { viewModel.updateSearchQuery("") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            FilterSection(
                selectedCategory = selectedCategory,
                selectedLocation = selectedLocation,
                selectedRiskLevel = selectedRiskLevel,
                onCategorySelected = { viewModel.setCategoryFilter(it) },
                onLocationSelected = { viewModel.setLocationFilter(it) },
                onRiskLevelSelected = { viewModel.setRiskLevelFilter(it) },
                onClearFilters = { viewModel.clearAllFilters() }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SupplyItemsList(
                items = filteredItems,
                onItemClick = { item ->
                    navController.navigate(NavRoutes.SupplyDetail.createRoute(item.itemId))
                }
            )
        }
    }
}

/**
 * Search bar for filtering items by name.
 * 
 * OutlinedTextField with search icon and clear button.
 * Performs case-insensitive real-time filtering.
 * 
 * @param query Current search query
 * @param onQueryChange Callback when query text changes
 * @param onClearClick Callback when clear button clicked
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearClick: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search items...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search icon"
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClearClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

/**
 * Filter section with chips for Category, Location, and Risk Level.
 * 
 * Displays horizontal scrollable rows of FilterChip components.
 * Selected filters show filled appearance, unselected show outlined.
 * Includes "Clear All" option to reset filters.
 * 
 * @param selectedCategory Currently selected category filter
 * @param selectedLocation Currently selected location filter
 * @param selectedRiskLevel Currently selected risk level filter
 * @param onCategorySelected Callback when category is selected
 * @param onLocationSelected Callback when location is selected
 * @param onRiskLevelSelected Callback when risk level is selected
 * @param onClearFilters Callback to clear all filters
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun FilterSection(
    selectedCategory: String?,
    selectedLocation: String?,
    selectedRiskLevel: String?,
    onCategorySelected: (String?) -> Unit,
    onLocationSelected: (String?) -> Unit,
    onRiskLevelSelected: (String?) -> Unit,
    onClearFilters: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            if (selectedCategory != null || selectedLocation != null || selectedRiskLevel != null) {
                Text(
                    text = "Clear All",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onClearFilters() }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Category",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(listOf("PPE", "Medication", "Surgical Kit", "Device")) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        onCategorySelected(if (selectedCategory == category) null else category)
                    },
                    label = { Text(category) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Risk Level",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            items(listOf("Critical", "Elevated", "Normal")) { risk ->
                FilterChip(
                    selected = selectedRiskLevel == risk,
                    onClick = {
                        onRiskLevelSelected(if (selectedRiskLevel == risk) null else risk)
                    },
                    label = { Text(risk) }
                )
            }
        }
    }
}

/**
 * Scrollable list of supply items.
 * 
 * LazyColumn rendering ElevatedCard components for each item.
 * Each card displays comprehensive item information and navigates
 * to details screen on tap.
 * 
 * @param items List of supply items to display
 * @param onItemClick Callback when item card is clicked
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun SupplyItemsList(
    items: List<SupplyItem>,
    onItemClick: (SupplyItem) -> Unit
) {
    if (items.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No items found",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items) { item ->
                SupplyItemCard(
                    item = item,
                    onClick = { onItemClick(item) }
                )
            }
        }
    }
}

/**
 * Individual supply item card.
 * 
 * ElevatedCard displaying:
 * - Item name (titleMedium)
 * - Category and Location (bodyMedium)
 * - Current vs Minimum quantity (bodyLarge)
 * - Expiry date if present (bodyMedium)
 * - Color-coded risk level chip (AssistChip)
 * 
 * Card Styling:
 * - Elevated with 4dp elevation
 * - Full width
 * - 16dp padding
 * - Clickable with ripple effect
 * 
 * @param item SupplyItem data to display
 * @param onClick Callback when card is clicked
 * 
 * @author Gustavo Sanchez
 */
@Composable
fun SupplyItemCard(
    item: SupplyItem,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = item.location,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                RiskLevelChip(riskLevel = item.riskLevel)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Current Quantity",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.currentQuantity}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Column {
                    Text(
                        text = "Minimum Required",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${item.minimumRequired}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            item.expiryDate?.let { expiryTimestamp ->
                Spacer(modifier = Modifier.height(8.dp))
                
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val expiryDateString = dateFormat.format(Date(expiryTimestamp))
                
                Text(
                    text = "Expires: $expiryDateString",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Risk level indicator chip.
 * 
 * AssistChip with color-coded background based on risk severity:
 * - Critical: errorContainer (red)
 * - Elevated: tertiaryContainer (amber/orange)
 * - Normal: primaryContainer (blue)
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
