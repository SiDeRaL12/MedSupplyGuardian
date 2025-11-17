package com.gustavosanchez.medsupplyguardian.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.gustavosanchez.medsupplyguardian.data.local.converter.DateConverter
import com.gustavosanchez.medsupplyguardian.data.local.dao.SupplyItemDao
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Room database configuration for MedSupply Guardian application.
 * 
 * This class serves as the main database access point, managing the
 * SQLite database instance and providing access to DAOs. The database
 * includes automatic initialization with sample healthcare supply data
 * for demonstration and testing purposes.
 * 
 * Database Details:
 * - Name: medsupply_guardian_db
 * - Version: 1
 * - Entities: SupplyItem
 * - Type Converters: DateConverter (for Date/Long conversions)
 * 
 * Features:
 * - Singleton pattern for single database instance
 * - Pre-populated sample data on first creation
 * - Thread-safe initialization
 * 
 * @author Gustavo Sanchez
 */
@Database(
    entities = [SupplyItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    /**
     * Provides access to supply item data operations.
     * 
     * @return SupplyItemDao instance for database operations
     */
    abstract fun supplyItemDao(): SupplyItemDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        /**
         * Gets the singleton database instance.
         * 
         * Creates a new database instance if one does not exist, using
         * double-checked locking for thread safety. On first creation,
         * the database is pre-populated with sample supply data.
         * 
         * @param context Application context for database creation
         * @return Singleton AppDatabase instance
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "medsupply_guardian_db"
                )
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * Room database callback for handling database lifecycle events.
         * 
         * Implements onCreate to populate the database with initial sample
         * data representing typical healthcare supply inventory items.
         */
        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database.supplyItemDao())
                    }
                }
            }
        }
        
        /**
         * Populates the database with sample healthcare supply items.
         * 
         * Creates a realistic inventory dataset with various categories,
         * locations, and risk levels to demonstrate application functionality.
         * 
         * Sample Data Includes:
         * - PPE (Personal Protective Equipment)
         * - Medications
         * - Surgical supplies
         * - Medical devices
         * 
         * Each item has computed risk levels based on quantity and expiry dates.
         * 
         * @param dao SupplyItemDao instance for data insertion
         */
        private suspend fun populateDatabase(dao: SupplyItemDao) {
            val calendar = Calendar.getInstance()
            
            val sampleItems = listOf(
                SupplyItem(
                    name = "N95 Respirator Masks",
                    category = "PPE",
                    minimumRequired = 500,
                    currentQuantity = 350,
                    expiryDate = calendar.apply { add(Calendar.DAY_OF_YEAR, 5) }.timeInMillis,
                    location = "Storage Room A",
                    riskLevel = SupplyItem.computeRiskLevel(350, 500, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Surgical Gloves (Medium)",
                    category = "PPE",
                    minimumRequired = 1000,
                    currentQuantity = 1200,
                    expiryDate = calendar.apply { add(Calendar.MONTH, 6) }.timeInMillis,
                    location = "Storage Room A",
                    riskLevel = SupplyItem.computeRiskLevel(1200, 1000, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Paracetamol 500mg",
                    category = "Medication",
                    minimumRequired = 200,
                    currentQuantity = 180,
                    expiryDate = calendar.apply { add(Calendar.DAY_OF_YEAR, 25) }.timeInMillis,
                    location = "Pharmacy Cabinet 3",
                    riskLevel = SupplyItem.computeRiskLevel(180, 200, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Insulin Vials",
                    category = "Medication",
                    minimumRequired = 50,
                    currentQuantity = 30,
                    expiryDate = calendar.apply { add(Calendar.DAY_OF_YEAR, 3) }.timeInMillis,
                    location = "Refrigerated Storage",
                    riskLevel = SupplyItem.computeRiskLevel(30, 50, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Sterile Scalpel Blades",
                    category = "Surgical Kit",
                    minimumRequired = 100,
                    currentQuantity = 95,
                    expiryDate = calendar.apply { add(Calendar.YEAR, 2) }.timeInMillis,
                    location = "Operating Theater 2",
                    riskLevel = SupplyItem.computeRiskLevel(95, 100, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Suture Kits (Absorbable)",
                    category = "Surgical Kit",
                    minimumRequired = 75,
                    currentQuantity = 120,
                    expiryDate = calendar.apply { add(Calendar.MONTH, 8) }.timeInMillis,
                    location = "Operating Theater 1",
                    riskLevel = SupplyItem.computeRiskLevel(120, 75, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Blood Pressure Monitors",
                    category = "Device",
                    minimumRequired = 20,
                    currentQuantity = 25,
                    expiryDate = null,
                    location = "Ward Equipment Room",
                    riskLevel = SupplyItem.computeRiskLevel(25, 20, null)
                ),
                SupplyItem(
                    name = "IV Infusion Sets",
                    category = "Device",
                    minimumRequired = 300,
                    currentQuantity = 280,
                    expiryDate = calendar.apply { add(Calendar.MONTH, 3) }.timeInMillis,
                    location = "Emergency Department",
                    riskLevel = SupplyItem.computeRiskLevel(280, 300, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Disposable Face Shields",
                    category = "PPE",
                    minimumRequired = 400,
                    currentQuantity = 150,
                    expiryDate = calendar.apply { add(Calendar.MONTH, 12) }.timeInMillis,
                    location = "Storage Room B",
                    riskLevel = SupplyItem.computeRiskLevel(150, 400, calendar.timeInMillis)
                ),
                SupplyItem(
                    name = "Antibiotic Amoxicillin 250mg",
                    category = "Medication",
                    minimumRequired = 150,
                    currentQuantity = 200,
                    expiryDate = calendar.apply { add(Calendar.DAY_OF_YEAR, 45) }.timeInMillis,
                    location = "Pharmacy Cabinet 1",
                    riskLevel = SupplyItem.computeRiskLevel(200, 150, calendar.timeInMillis)
                )
            )
            
            dao.insertAll(sampleItems)
        }
    }
}