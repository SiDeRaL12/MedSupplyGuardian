package com.gustavosanchez.medsupplyguardian.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.gustavosanchez.medsupplyguardian.domain.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Manager class for persisting and retrieving user preferences using SharedPreferences.
 * 
 * This class implements a reactive preferences management system using StateFlow
 * to enable real-time UI updates when preferences change. It abstracts the
 * SharedPreferences API and provides a clean, type-safe interface for preference
 * access throughout the application.
 * 
 * Features:
 * - Reactive updates via StateFlow
 * - Type-safe preference access
 * - Immediate persistence on write
 * - Default value handling
 * - Theme change propagation
 * 
 * Architecture:
 * This class follows the Repository pattern for preferences, serving as the
 * single source of truth for all user settings.
 * 
 * @param context Application context for accessing SharedPreferences
 * 
 * @author Gustavo Sanchez
 */
class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    private val _userPreferences = MutableStateFlow(loadPreferences())
    val userPreferences: StateFlow<UserPreferences> = _userPreferences.asStateFlow()
    
    companion object {
        private const val PREFS_NAME = "medsupply_guardian_prefs"
        
        private const val KEY_STAFF_NAME = "staff_name"
        private const val KEY_STAFF_ID = "staff_id"
        private const val KEY_SORTING_MODE = "sorting_mode"
        private const val KEY_ALERT_THRESHOLD = "alert_threshold"
        private const val KEY_DARK_THEME = "dark_theme"
        private const val KEY_AUDIT_REMINDER_HOURS = "audit_reminder_hours"
        
        private const val DEFAULT_STAFF_NAME = ""
        private const val DEFAULT_STAFF_ID = ""
        private const val DEFAULT_SORTING_MODE = "Name"
        private const val DEFAULT_ALERT_THRESHOLD = 20
        private const val DEFAULT_DARK_THEME = false
        private const val DEFAULT_AUDIT_REMINDER_HOURS = 24
    }
    
    /**
     * Loads all preferences from SharedPreferences storage.
     * 
     * Retrieves stored preference values or returns defaults if not found.
     * This method is called during initialization to populate the StateFlow.
     * 
     * @return UserPreferences object containing all current settings
     */
    private fun loadPreferences(): UserPreferences {
        return UserPreferences(
            staffName = sharedPreferences.getString(KEY_STAFF_NAME, DEFAULT_STAFF_NAME) ?: DEFAULT_STAFF_NAME,
            staffId = sharedPreferences.getString(KEY_STAFF_ID, DEFAULT_STAFF_ID) ?: DEFAULT_STAFF_ID,
            sortingMode = sharedPreferences.getString(KEY_SORTING_MODE, DEFAULT_SORTING_MODE) ?: DEFAULT_SORTING_MODE,
            alertThreshold = sharedPreferences.getInt(KEY_ALERT_THRESHOLD, DEFAULT_ALERT_THRESHOLD),
            isDarkTheme = sharedPreferences.getBoolean(KEY_DARK_THEME, DEFAULT_DARK_THEME),
            auditReminderHours = sharedPreferences.getInt(KEY_AUDIT_REMINDER_HOURS, DEFAULT_AUDIT_REMINDER_HOURS)
        )
    }
    
    /**
     * Saves all preferences to SharedPreferences storage.
     * 
     * Persists the complete UserPreferences object to storage and updates
     * the StateFlow to notify observers of the change. Changes are committed
     * synchronously to ensure immediate persistence.
     * 
     * @param preferences UserPreferences object containing values to save
     */
    fun savePreferences(preferences: UserPreferences) {
        sharedPreferences.edit().apply {
            putString(KEY_STAFF_NAME, preferences.staffName)
            putString(KEY_STAFF_ID, preferences.staffId)
            putString(KEY_SORTING_MODE, preferences.sortingMode)
            putInt(KEY_ALERT_THRESHOLD, preferences.alertThreshold)
            putBoolean(KEY_DARK_THEME, preferences.isDarkTheme)
            putInt(KEY_AUDIT_REMINDER_HOURS, preferences.auditReminderHours)
            apply()
        }
        _userPreferences.value = preferences
    }
    
    /**
     * Updates the staff name preference.
     * 
     * @param name New staff name to save
     */
    fun updateStaffName(name: String) {
        savePreferences(_userPreferences.value.copy(staffName = name))
    }
    
    /**
     * Updates the staff ID preference.
     * 
     * @param id New staff ID to save
     */
    fun updateStaffId(id: String) {
        savePreferences(_userPreferences.value.copy(staffId = id))
    }
    
    /**
     * Updates the sorting mode preference.
     * 
     * @param mode New sorting mode (Name, Category, Risk)
     */
    fun updateSortingMode(mode: String) {
        savePreferences(_userPreferences.value.copy(sortingMode = mode))
    }
    
    /**
     * Updates the alert threshold percentage.
     * 
     * @param threshold New threshold value (0-100)
     */
    fun updateAlertThreshold(threshold: Int) {
        savePreferences(_userPreferences.value.copy(alertThreshold = threshold))
    }
    
    /**
     * Updates the theme mode preference.
     * 
     * This change should immediately reflect in the UI through the
     * StateFlow observation mechanism.
     * 
     * @param isDark True for dark theme, false for light theme
     */
    fun updateThemeMode(isDark: Boolean) {
        savePreferences(_userPreferences.value.copy(isDarkTheme = isDark))
    }
    
    /**
     * Updates the audit reminder interval.
     * 
     * @param hours Number of hours between audit reminders
     */
    fun updateAuditReminderHours(hours: Int) {
        savePreferences(_userPreferences.value.copy(auditReminderHours = hours))
    }
    
    /**
     * Retrieves the current staff name.
     * 
     * @return Current staff name or empty string if not set
     */
    fun getStaffName(): String = _userPreferences.value.staffName
    
    /**
     * Retrieves the current staff ID.
     * 
     * @return Current staff ID or empty string if not set
     */
    fun getStaffId(): String = _userPreferences.value.staffId
    
    /**
     * Retrieves the current sorting mode.
     * 
     * @return Current sorting mode (Name, Category, Risk)
     */
    fun getSortingMode(): String = _userPreferences.value.sortingMode
    
    /**
     * Retrieves the current alert threshold.
     * 
     * @return Current threshold percentage (0-100)
     */
    fun getAlertThreshold(): Int = _userPreferences.value.alertThreshold
    
    /**
     * Checks if dark theme is currently enabled.
     * 
     * @return True if dark theme is enabled, false otherwise
     */
    fun isDarkTheme(): Boolean = _userPreferences.value.isDarkTheme
    
    /**
     * Retrieves the current audit reminder interval.
     * 
     * @return Number of hours between audit reminders
     */
    fun getAuditReminderHours(): Int = _userPreferences.value.auditReminderHours
    
    /**
     * Clears all stored preferences and resets to defaults.
     * 
     * This method should be used with caution as it removes all user settings.
     * Typically used for logout or app reset functionality.
     */
    fun clearAllPreferences() {
        sharedPreferences.edit().clear().apply()
        _userPreferences.value = UserPreferences()
    }
}