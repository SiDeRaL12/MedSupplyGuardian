package com.gustavosanchez.medsupplyguardian.data.repository

import com.gustavosanchez.medsupplyguardian.data.local.dao.SupplyItemDao
import com.gustavosanchez.medsupplyguardian.data.local.entity.SupplyItem
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing supply item data operations.
 * 
 * This repository implements the Repository pattern to abstract data sources
 * from the rest of the application. It serves as a single source of truth for
 * supply item data, coordinating between the local Room database and any
 * future remote data sources.
 * 
 * Responsibilities:
 * - Provide clean API for data access to ViewModels
 * - Abstract database implementation details
 * - Support future expansion to remote data sources
 * - Enable easier testing through dependency injection
 * 
 * @param supplyItemDao DAO instance for database operations
 * 
 * @author Gustavo Sanchez
 */
class SupplyRepository(private val supplyItemDao: SupplyItemDao) {
    
    /**
     * Retrieves all supply items as a Flow.
     * 
     * @return Flow emitting List of all SupplyItem entities
     */
    fun getAllItems(): Flow<List<SupplyItem>> = supplyItemDao.getAllItems()
    
    /**
     * Searches supply items by keyword.
     * 
     * @param keyword Search term to match against item names
     * @return Flow emitting List of matching SupplyItem entities
     */
    fun searchItems(keyword: String): Flow<List<SupplyItem>> = supplyItemDao.searchItems(keyword)
    
    /**
     * Filters items by category.
     * 
     * @param category Category name to filter by
     * @return Flow emitting List of filtered SupplyItem entities
     */
    fun filterByCategory(category: String): Flow<List<SupplyItem>> = 
        supplyItemDao.filterByCategory(category)
    
    /**
     * Filters items by location.
     * 
     * @param location Location name to filter by
     * @return Flow emitting List of filtered SupplyItem entities
     */
    fun filterByLocation(location: String): Flow<List<SupplyItem>> = 
        supplyItemDao.filterByLocation(location)
    
    /**
     * Filters items by risk level.
     * 
     * @param riskLevel Risk level to filter by
     * @return Flow emitting List of filtered SupplyItem entities
     */
    fun filterByRisk(riskLevel: String): Flow<List<SupplyItem>> = 
        supplyItemDao.filterByRisk(riskLevel)
    
    /**
     * Retrieves a single item by ID.
     * 
     * @param id Unique item identifier
     * @return Flow emitting SupplyItem or null
     */
    fun getItemById(id: Int): Flow<SupplyItem?> = supplyItemDao.getItemById(id)
    
    /**
     * Updates item quantity.
     * 
     * @param id Item identifier
     * @param newQuantity New quantity value
     */
    suspend fun updateQuantity(id: Int, newQuantity: Int) = 
        supplyItemDao.updateQuantity(id, newQuantity)
    
    /**
     * Inserts a new item.
     * 
     * @param item SupplyItem to insert
     */
    suspend fun insertItem(item: SupplyItem) = supplyItemDao.insertItem(item)
    
    /**
     * Updates an existing item.
     * 
     * @param item SupplyItem with updated values
     */
    suspend fun updateItem(item: SupplyItem) = supplyItemDao.updateItem(item)
    
    /**
     * Deletes an item.
     * 
     * @param item SupplyItem to delete
     */
    suspend fun deleteItem(item: SupplyItem) = supplyItemDao.deleteItem(item)
    
    /**
     * Retrieves all critical items.
     * 
     * @return Flow emitting List of critical SupplyItem entities
     */
    fun getCriticalItems(): Flow<List<SupplyItem>> = supplyItemDao.getCriticalItems()
    
    /**
     * Retrieves items expiring before threshold.
     * 
     * @param thresholdTimestamp Expiry threshold timestamp
     * @return Flow emitting List of expiring SupplyItem entities
     */
    fun getExpiringItems(thresholdTimestamp: Long): Flow<List<SupplyItem>> = 
        supplyItemDao.getExpiringItems(thresholdTimestamp)
}