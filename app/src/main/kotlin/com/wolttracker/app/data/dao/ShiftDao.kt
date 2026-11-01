package com.wolttracker.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wolttracker.app.data.model.ShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {
    @Query("SELECT * FROM shifts WHERE dateMillis BETWEEN :startOfDay AND :endOfDay ORDER BY startTimeMillis DESC")
    fun getShiftsForDate(startOfDay: Long, endOfDay: Long): Flow<List<ShiftEntity>>

    @Query("SELECT * FROM shifts ORDER BY startTimeMillis DESC")
    fun getAllShifts(): Flow<List<ShiftEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShift(shift: ShiftEntity): Long

    @Update
    suspend fun updateShift(shift: ShiftEntity)

    @Delete
    suspend fun deleteShift(shift: ShiftEntity)

    @Query("SELECT SUM(grossIncome) FROM shifts WHERE dateMillis BETWEEN :startOfDay AND :endOfDay")
    fun getTotalGrossIncome(startOfDay: Long, endOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(tips) FROM shifts WHERE dateMillis BETWEEN :startOfDay AND :endOfDay")
    fun getTotalTips(startOfDay: Long, endOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(durationHours) FROM shifts WHERE dateMillis BETWEEN :startOfDay AND :endOfDay")
    fun getTotalHours(startOfDay: Long, endOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(ordersCount) FROM shifts WHERE dateMillis BETWEEN :startOfDay AND :endOfDay")
    fun getTotalOrders(startOfDay: Long, endOfDay: Long): Flow<Int?>

    @Query("DELETE FROM shifts")
    suspend fun deleteAllShifts()
}
