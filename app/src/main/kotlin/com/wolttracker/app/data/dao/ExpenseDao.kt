package com.wolttracker.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wolttracker.app.data.model.ExpenseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM expenses WHERE dateMillis BETWEEN :startOfDay AND :endOfDay ORDER BY timestampMillis DESC")
    fun getExpensesForDate(startOfDay: Long, endOfDay: Long): Flow<List<ExpenseEntity>>

    @Query("SELECT * FROM expenses ORDER BY timestampMillis DESC")
    fun getAllExpenses(): Flow<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("SELECT SUM(amount) FROM expenses WHERE dateMillis BETWEEN :startOfDay AND :endOfDay")
    fun getTotalExpenses(startOfDay: Long, endOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM expenses WHERE category = 'FUEL' AND dateMillis BETWEEN :startOfDay AND :endOfDay")
    fun getFuelExpenses(startOfDay: Long, endOfDay: Long): Flow<Double?>
}
