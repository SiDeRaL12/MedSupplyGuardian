package com.gustavosanchez.medsupplyguardian.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gustavosanchez.medsupplyguardian.data.preferences.PreferencesManager
import com.gustavosanchez.medsupplyguardian.data.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ViewModel for managing audit workflow state and progression.
 * 
 * This ViewModel manages the multi-step audit process, tracking:
 * - Current step progression (1-5)
 * - Storage condition checklist
 * - Missing/damaged item selections
 * - Audit metadata (timestamp, technician)
 * - Upload simulation state
 * 
 * Features:
 * - Step navigation (next/previous)
 * - Progress calculation
 * - Storage validation tracking
 * - Missing/damaged item selection
 * - Audit summary generation
 * - Upload simulation
 * 
 * @param repository Repository for supply data access
 * @param preferencesManager Manager for technician identity
 * 
 * @author Gustavo Sanchez
 */
class AuditViewModel(
    private val repository: SupplyRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()
    
    private val _storageConditions = MutableStateFlow(
        mapOf(
            "Temperature within safe range" to false,
            "Humidity acceptable" to false,
            "Storage area clean" to false,
            "Items properly sealed" to false,
            "No obstructions to access" to false
        )
    )
    val storageConditions: StateFlow<Map<String, Boolean>> = _storageConditions.asStateFlow()
    
    private val _missingDamagedSelection = MutableStateFlow<String?>(null)
    val missingDamagedSelection: StateFlow<String?> = _missingDamagedSelection.asStateFlow()
    
    private val _comments = MutableStateFlow("")
    val comments: StateFlow<String> = _comments.asStateFlow()
    
    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()
    
    val allItems = repository.getAllItems()
    val criticalItems = repository.getCriticalItems()
    
    private val thirtyDaysFromNow = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_YEAR, 30)
    }.timeInMillis
    val expiringItems = repository.getExpiringItems(thirtyDaysFromNow)
    
    /**
     * Advances to the next audit step.
     * Maximum step is 5.
     */
    fun nextStep() {
        if (_currentStep.value < 5) {
            _currentStep.value += 1
        }
    }
    
    /**
     * Returns to the previous audit step.
     * Minimum step is 1.
     */
    fun previousStep() {
        if (_currentStep.value > 1) {
            _currentStep.value -= 1
        }
    }
    
    /**
     * Updates a storage condition checklist item.
     * 
     * @param condition The condition key to update
     * @param checked The new checked state
     */
    fun updateStorageCondition(condition: String, checked: Boolean) {
        _storageConditions.value = _storageConditions.value.toMutableMap().apply {
            this[condition] = checked
        }
    }
    
    /**
     * Sets the missing/damaged item selection.
     * 
     * @param selection Selected option (Missing, Damaged, Both, None)
     */
    fun setMissingDamagedSelection(selection: String) {
        _missingDamagedSelection.value = selection
    }
    
    /**
     * Updates the audit comments.
     * 
     * @param text New comment text
     */
    fun updateComments(text: String) {
        _comments.value = text
    }
    
    /**
     * Calculates the current progress as a fraction (0.0 to 1.0).
     * 
     * @return Progress value for LinearProgressIndicator
     */
    fun getProgress(): Float {
        return _currentStep.value / 5f
    }
    
    /**
     * Retrieves the technician name from preferences.
     * 
     * @return Staff name or "Unknown" if not set
     */
    fun getTechnicianName(): String {
        return preferencesManager.getStaffName().ifEmpty { "Unknown" }
    }
    
    /**
     * Retrieves the technician ID from preferences.
     * 
     * @return Staff ID or "N/A" if not set
     */
    fun getTechnicianId(): String {
        return preferencesManager.getStaffId().ifEmpty { "N/A" }
    }
    
    /**
     * Gets the current date and time formatted for audit reports.
     * 
     * @return Formatted date string (e.g., "Nov 17, 2025 7:30 AM")
     */
    fun getAuditDateTime(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }
    
    /**
     * Simulates uploading audit results with a delay.
     * 
     * @param onComplete Callback when upload completes
     */
    suspend fun simulateUpload(onComplete: () -> Unit) {
        _isUploading.value = true
        kotlinx.coroutines.delay(2500)
        _isUploading.value = false
        onComplete()
    }
    
    /**
     * Resets the audit workflow to initial state.
     */
    fun resetAudit() {
        _currentStep.value = 1
        _storageConditions.value = _storageConditions.value.mapValues { false }
        _missingDamagedSelection.value = null
        _comments.value = ""
        _isUploading.value = false
    }
}

/**
 * Factory for creating AuditViewModel instances with dependencies.
 * 
 * @param repository Repository for supply data operations
 * @param preferencesManager Manager for user preferences
 * 
 * @author Gustavo Sanchez
 */
class AuditViewModelFactory(
    private val repository: SupplyRepository,
    private val preferencesManager: PreferencesManager
) : ViewModelProvider.Factory {
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuditViewModel::class.java)) {
            return AuditViewModel(repository, preferencesManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
