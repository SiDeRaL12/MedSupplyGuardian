package com.gustavosanchez.medsupplyguardian.data.local.converter

import androidx.room.TypeConverter
import java.util.Date

/**
 * Type converter for Room database to handle Date and Long conversions.
 * 
 * Room does not natively support Date objects, so this converter enables
 * storage of Date fields as Long timestamps in the SQLite database.
 * 
 * Conversions:
 * - Date to Long: Converts Date objects to Unix timestamps for storage
 * - Long to Date: Converts Unix timestamps back to Date objects for retrieval
 * 
 * @author Gustavo Sanchez
 */
class DateConverter {
    
    /**
     * Converts a Long timestamp to a Date object.
     * 
     * @param value The Long timestamp to convert, nullable
     * @return Date object or null if value is null
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }
    
    /**
     * Converts a Date object to a Long timestamp.
     * 
     * @param date The Date object to convert, nullable
     * @return Long timestamp or null if date is null
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}