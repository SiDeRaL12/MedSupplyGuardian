package com.gustavosanchez.medsupplyguardian.domain.model

/**
 * Data model representing user preferences and settings.
 * 
 * This immutable data class encapsulates all user-configurable settings
 * that persist across application sessions. It provides type-safe access
 * to preference values and supports default initialization.
 * 
 * Fields:
 * @param staffName Full name of the hospital staff member using the application
 * @param staffId Unique identifier for the staff member
 * @param sortingMode Current sorting preference for supply list (Name, Category, Risk)
 * @param alertThreshold Percentage threshold for triggering low-stock alerts (0-100)
 * @param isDarkTheme Boolean indicating if dark theme is enabled
 * @param auditReminderHours Hours between audit reminder notifications
 * 
 * @author Gustavo Sanchez
 */
data class UserPreferences(
    val staffName: String = "",
    val staffId: String = "",
    val sortingMode: String = "Name",
    val alertThreshold: Int = 20,
    val isDarkTheme: Boolean = false,
    val auditReminderHours: Int = 24
)