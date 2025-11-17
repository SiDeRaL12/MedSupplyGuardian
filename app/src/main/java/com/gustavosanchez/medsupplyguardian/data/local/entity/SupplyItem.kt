package com.gustavosanchez.medsupplyguardian.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

/**
 * Entity representing a medical supply item in the inventory database.
 * 
 * This entity defines the schema for supply items stored in the Room database.
 * Each item tracks essential inventory details including quantities, expiry dates,
 * location information, and risk assessment levels for compliance auditing.
 * 
 * Database Table: supply_items
 * 
 * Fields:
 * @param itemId Unique identifier for the supply item (auto-generated primary key)
 * @param name Name of the medical supply item
 * @param category Category classification (PPE, Medication, Surgical Kit, Device)
 * @param minimumRequired Minimum quantity threshold required for compliance
 * @param currentQuantity Current quantity available in inventory
 * @param expiryDate Expiration date of the supply item (nullable, stored as Long timestamp)
 * @param location Physical storage location within the facility
 * @param riskLevel Computed risk assessment level (Critical, Elevated, Normal)
 * 
 * Risk Level Computation Logic:
 * - Critical: currentQuantity < minimumRequired OR expiring within 7 days
 * - Elevated: currentQuantity <= minimumRequired * 1.5 OR expiring within 30 days
 * - Normal: All other cases
 * 
 * @author Gustavo Sanchez
 */
@Entity(tableName = "supply_items")
data class SupplyItem(
    @PrimaryKey(autoGenerate = true)
    val itemId: Int = 0,
    
    val name: String,
    
    val category: String,
    
    val minimumRequired: Int,
    
    val currentQuantity: Int,
    
    val expiryDate: Long? = null,
    
    val location: String,
    
    val riskLevel: String
) {
    companion object {
        /**
         * Computes the risk level for a supply item based on quantity and expiry status.
         * 
         * Risk Assessment Rules:
         * - Critical: Stock below minimum OR expires in 7 days
         * - Elevated: Stock at or below 150% of minimum OR expires in 30 days
         * - Normal: Stock adequate and no immediate expiry concerns
         * 
         * @param currentQuantity Current available quantity
         * @param minimumRequired Minimum threshold quantity
         * @param expiryDate Expiration date timestamp (nullable)
         * @return String representing risk level: "Critical", "Elevated", or "Normal"
         * 
         * @example
         * ```
         * val risk = SupplyItem.computeRiskLevel(5, 10, null)
         * // Returns "Critical" because current < minimum
         * ```
         */
        fun computeRiskLevel(
            currentQuantity: Int,
            minimumRequired: Int,
            expiryDate: Long?
        ): String {
            val now = System.currentTimeMillis()
            val sevenDaysInMillis = 7L * 24 * 60 * 60 * 1000
            val thirtyDaysInMillis = 30L * 24 * 60 * 60 * 1000
            
            return when {
                currentQuantity < minimumRequired -> "Critical"
                expiryDate != null && (expiryDate - now) <= sevenDaysInMillis -> "Critical"
                currentQuantity <= (minimumRequired * 1.5).toInt() -> "Elevated"
                expiryDate != null && (expiryDate - now) <= thirtyDaysInMillis -> "Elevated"
                else -> "Normal"
            }
        }
    }
}