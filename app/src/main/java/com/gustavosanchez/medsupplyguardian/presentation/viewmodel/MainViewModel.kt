package com.gustavosanchez.medsupplyguardian.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import com.gustavosanchez.medsupplyguardian.data.preferences.PreferencesManager
import com.gustavosanchez.medsupplyguardian.data.repository.SupplyRepository
import com.gustavosanchez.medsupplyguardian.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Main ViewModel for MedSupply Guardian application.
 * 
 * This ViewModel manages the core application state including supply items,
 * user preferences, loading states, and data operations. It serves as the
 * central state holder and business logic coordinator for the UI layer.
 * 
 * Responsibilities:
 * - Supply item data management and filtering
 * - User preferences synchronization
 * - Loading state coordination
 * - Search and filter operations
 * - Quantity updates and data mutations
 * - Critical and expiring items tracking
 * 
 * State Management:
 * - Uses StateFlow for reactive UI updates
 * - Combines multiple data streams efficiently
 * - Survives configuration changes
 * - Cancels operations on ViewModel cleanup
 * 
 * @param repository Repository for supply data operations
 * @param preferencesManager Manager for user preferences
 * 
 * @author Gustavo Sanchez
 */
class MainViewModel(
    private val repository: SupplyRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    // Loading state for startup initialization
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // All supply items from database
    val allItems: StateFlow<List<SupplyItem>> = repository.getAllItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Critical items for dashboard alerts
    val criticalItems: StateFlow<List<SupplyItem>> = repository.getCriticalItems()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Items expiring within 30 days
    private val thirtyDaysFromNow = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 30)
    }.timeInMillis
    
    val expiringItems: StateFlow<List<SupplyItem>> = repository.getExpiringItems(thirtyDaysFromNow)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // User preferences
    val userPreferences: StateFlow<UserPreferences> = preferencesManager.userPreferences
    
    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    // Filter states
    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()
    
    private val _selectedLocation = MutableStateFlow<String?>(null)
    val selectedLocation: StateFlow<String?> = _selectedLocation.asStateFlow()
    
    private val _selectedRiskLevel = MutableStateFlow<String?>(null)
    val selectedRiskLevel: StateFlow<String?> = _selectedRiskLevel.asStateFlow()
    
    // Filtered items combining search and filters
    val filteredItems: StateFlow<List<SupplyItem>> = combine(
        allItems,
        searchQuery,
        selectedCategory,
        selectedLocation,
        selectedRiskLevel
    ) { items, query, category, location, risk ->
        var filtered = items
        
        if (query.isNotEmpty()) {
            filtered = filtered.filter { 
                it.name.contains(query, ignoreCase = true) 
            }
        }
        
        category?.let { cat ->
            filtered = filtered.filter { it.category == cat }
        }
        
        location?.let { loc ->
            filtered = filtered.filter { it.location == loc }
        }
        
        risk?.let { riskLevel ->
            filtered = filtered.filter { it.riskLevel == riskLevel }
        }
        
        filtered
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    init {
        // Simulate database initialization loading
        viewModelScope.launch {
            kotlinx.coroutines.delay(1500) // Simulate loading time
            _isLoading.value = false
        }
    }
    
    /**
     * Updates the search query for filtering supply items.
     * 
     * @param query Search term to filter by item name
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    
    /**
     * Sets the category filter.
     * 
     * @param category Category name to filter by, null to clear filter
     */
    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }
    
    /**
     * Sets the location filter.
     * 
     * @param location Location name to filter by, null to clear filter
     */
    fun setLocationFilter(location: String?) {
        _selectedLocation.value = location
    }
    
    /**
     * Sets the risk level filter.
     * 
     * @param riskLevel Risk level to filter by, null to clear filter
     */
    fun setRiskLevelFilter(riskLevel: String?) {
        _selectedRiskLevel.value = riskLevel
    }
    
    /**
     * Clears all active filters and search query.
     */
    fun clearAllFilters() {
        _searchQuery.value = ""
        _selectedCategory.value = null
        _selectedLocation.value = null
        _selectedRiskLevel.value = null
    }
    
    /**
     * Retrieves a single supply item by ID.
     * 
     * @param itemId Unique identifier of the item
     * @return StateFlow emitting the item or null if not found
     */
    fun getItemById(itemId: Int): StateFlow<SupplyItem?> {
        return repository.getItemById(itemId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = null
            )
    }
    
    /**
     * Updates the quantity of a supply item.
     * 
     * After updating quantity, the risk level should be recalculated
     * in the database layer or repository.
     * 
     * @param itemId Item identifier
     * @param newQuantity New quantity value
     */
    fun updateItemQuantity(itemId: Int, newQuantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(itemId, newQuantity)
        }
    }
    
    /**
     * Updates a complete supply item.
     * 
     * @param item SupplyItem with updated values
     */
    fun updateItem(item: SupplyItem) {
        viewModelScope.launch {
            repository.updateItem(item)
        }
    }
    
    /**
     * Saves updated user preferences.
     * 
     * @param preferences UserPreferences object with new values
     */
    fun savePreferences(preferences: UserPreferences) {
        preferencesManager.savePreferences(preferences)
    }
    
    /**
     * Updates only the theme mode preference.
     * 
     * @param isDark True for dark theme, false for light theme
     */
    fun updateThemeMode(isDark: Boolean) {
        preferencesManager.updateThemeMode(isDark)
    }
}

/**
 * Factory for creating MainViewModel instances with dependencies.
 * 
 * This factory enables proper dependency injection for ViewModel creation,
 * allowing the repository and preferences manager to be provided at runtime.
 * 
 * @param repository Repository for supply data operations
 * @param preferencesManager Manager for user preferences
 * 
 * @author Gustavo Sanchez
 */
class MainViewModelFactory(
    private val repository: SupplyRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    /**
     * Creates a new instance of the specified ViewModel class.
     * 
     * @param modelClass Class of the ViewModel to create
     * @return Newly created ViewModel instance
     * @throws IllegalArgumentException if unknown ViewModel class
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
