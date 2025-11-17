package com.gustavosanchez.medsupplyguardian

import android.app.Application
import com.gustavosanchez.medsupplyguardian.data.local.database.AppDatabase
import com.gustavosanchez.medsupplyguardian.data.preferences.PreferencesManager
import com.gustavosanchez.medsupplyguardian.data.repository.SupplyRepository

/**
 * Custom Application class for MedSupply Guardian.
 * 
 * This class serves as the entry point for application-level initialization
 * and dependency provision. It creates and maintains singleton instances of
 * core application components including the database, repository, and
 * preferences manager.
 * 
 * Architecture:
 * This implementation uses a simple manual dependency injection pattern.
 * For larger applications, consider using Hilt or Koin for more robust
 * dependency management.
 * 
 * Lifecycle:
 * The onCreate method is called once when the application process starts,
 * before any activities, services, or receivers are created.
 * 
 * @author Gustavo Sanchez
 */
class MedSupplyGuardianApplication : Application() {
    
    /**
     * Room database instance initialized lazily on first access.
     * Thread-safe singleton pattern ensures single instance across app lifecycle.
     */
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    
    /**
     * Repository instance providing clean data access layer.
     * Depends on the database DAO for data operations.
     */
    val repository: SupplyRepository by lazy { 
        SupplyRepository(database.supplyItemDao()) 
    }
    
    /**
     * Preferences manager instance for handling user settings.
     * Initialized lazily to ensure context is available.
     */
    val preferencesManager: PreferencesManager by lazy { 
        PreferencesManager(this) 
    }
    
    /**
     * Called when the application is starting.
     * 
     * This is where application-level initialization should occur.
     * Avoid performing long-running operations here as it blocks
     * application startup.
     */
    override fun onCreate() {
        super.onCreate()
        // Future initialization can be added here
        // Examples: Logging, Crash reporting, Analytics
    }
}