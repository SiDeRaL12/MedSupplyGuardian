package com.gustavosanchez.medsupplyguardian.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for supply item database operations.
 * 
 * Provides comprehensive CRUD operations for the SupplyItem entity using
 * Room database. All operations are implemented as suspend functions or
 * Flow-based queries to support coroutine-based asynchronous execution.
 * 
 * Operations Include:
 * - Retrieve all items (Flow-based for real-time updates)
 * - Search items by name (case-insensitive)
 * - Filter by category, location, and risk level
 * - Update item quantities
 * - Insert new items (with conflict replacement strategy)
 * - Delete items
 * 
 * @author Gustavo Sanchez
 */
@Dao
interface SupplyItemDao {
    
    /**
     * Retrieves all supply items from the database.
     * 
     * Returns a Flow to enable real-time observation of database changes.
     * The UI will automatically update when items are added, modified, or deleted.
     * 
     * @return Flow emitting List of all SupplyItem entities
     */
    @Query("SELECT * FROM supply_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<SupplyItem>>
    
    /**
     * Searches supply items by name using case-insensitive pattern matching.
     * 
     * @param keyword Search keyword to match against item names
     * @return Flow emitting List of matching SupplyItem entities
     * 
     * @example
     * ```
     * dao.searchItems("%glove%")
     * // Returns all items with "glove" in their name
     * ```
     */
    @Query("SELECT * FROM supply_items WHERE name LIKE '%' || :keyword || '%' COLLATE NOCASE ORDER BY name ASC")
    fun searchItems(keyword: String): Flow<List<SupplyItem>>
    
    /**
     * Filters supply items by category.
     * 
     * @param category Category name to filter by (PPE, Medication, Surgical Kit, Device)
     * @return Flow emitting List of SupplyItem entities matching the category
     */
    @Query("SELECT * FROM supply_items WHERE category = :category ORDER BY name ASC")
    fun filterByCategory(category: String): Flow<List<SupplyItem>>
    
    /**
     * Filters supply items by location.
     * 
     * @param location Physical storage location to filter by
     * @return Flow emitting List of SupplyItem entities at the specified location
     */
    @Query("SELECT * FROM supply_items WHERE location = :location ORDER BY name ASC")
    fun filterByLocation(location: String): Flow<List<SupplyItem>>
    
    /**
     * Filters supply items by risk level.
     * 
     * @param riskLevel Risk assessment level (Critical, Elevated, Normal)
     * @return Flow emitting List of SupplyItem entities with the specified risk level
     */
    @Query("SELECT * FROM supply_items WHERE riskLevel = :riskLevel ORDER BY name ASC")
    fun filterByRisk(riskLevel: String): Flow<List<SupplyItem>>
    
    /**
     * Retrieves a single supply item by its unique identifier.
     * 
     * @param id The unique item ID to retrieve
     * @return Flow emitting the SupplyItem or null if not found
     */
    @Query("SELECT * FROM supply_items WHERE itemId = :id")
    fun getItemById(id: Int): Flow<SupplyItem?>
    
    /**
     * Updates the current quantity of a specific supply item.
     * 
     * This operation recalculates the risk level after quantity update.
     * 
     * @param id The unique item ID to update
     * @param newQuantity The new quantity value to set
     */
    @Query("UPDATE supply_items SET currentQuantity = :newQuantity WHERE itemId = :id")
    suspend fun updateQuantity(id: Int, newQuantity: Int)
    
    /**
     * Inserts a new supply item into the database.
     * 
     * If an item with the same primary key exists, it will be replaced.
     * 
     * @param item The SupplyItem entity to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SupplyItem)
    
    /**
     * Inserts multiple supply items into the database.
     * 
     * Useful for bulk data initialization or import operations.
     * 
     * @param items List of SupplyItem entities to insert
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SupplyItem>)
    
    /**
     * Updates an existing supply item in the database.
     * 
     * @param item The SupplyItem entity with updated values
     */
    @Update
    suspend fun updateItem(item: SupplyItem)
    
    /**
     * Deletes a supply item from the database.
     * 
     * @param item The SupplyItem entity to delete
     */
    @Delete
    suspend fun deleteItem(item: SupplyItem)
    
    /**
     * Retrieves items with critical risk level for dashboard alerts.
     * 
     * @return Flow emitting List of critical SupplyItem entities
     */
    @Query("SELECT * FROM supply_items WHERE riskLevel = 'Critical' ORDER BY name ASC")
    fun getCriticalItems(): Flow<List<SupplyItem>>
    
    /**
     * Retrieves items expiring within a specified number of days.
     * 
     * @param thresholdTimestamp The Unix timestamp threshold for expiry filtering
     * @return Flow emitting List of expiring SupplyItem entities
     */
    @Query("SELECT * FROM supply_items WHERE expiryDate IS NOT NULL AND expiryDate <= :thresholdTimestamp ORDER BY expiryDate ASC")
    fun getExpiringItems(thresholdTimestamp: Long): Flow<List<SupplyItem>>
}