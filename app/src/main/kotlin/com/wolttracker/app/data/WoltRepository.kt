package com.wolttracker.app.data

import com.wolttracker.app.data.dao.ExpenseDao
import com.wolttracker.app.data.dao.ShiftDao
import com.wolttracker.app.data.model.DailySummary
import com.wolttracker.app.data.model.ExpenseCategory
import com.wolttracker.app.data.model.ExpenseEntity
import com.wolttracker.app.data.model.RecentEntry
import com.wolttracker.app.data.model.ShiftEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import java.util.Calendar

class WoltRepository(
    private val shiftDao: ShiftDao,
    private val expenseDao: ExpenseDao
) {
    fun getDailySummary(startOfDay: Long, endOfDay: Long): Flow<DailySummary> {
        return shiftDao.getShiftsForDate(startOfDay, endOfDay).map { shifts ->
            val grossIncome = shifts.sumOf { it.grossIncome }
            val totalTips = shifts.sumOf { it.tips }
            val totalHours = shifts.sumOf { it.durationHours }
            val totalOrders = shifts.sumOf { it.ordersCount }

            DailySummary(
                grossIncome = grossIncome,
                totalHours = totalHours,
                totalOrders = totalOrders,
                totalTips = totalTips
            )
        }
    }

    fun getRecentEntries(startOfDay: Long, endOfDay: Long): Flow<List<RecentEntry>> {
        return shiftDao.getShiftsForDate(startOfDay, endOfDay).map { shifts ->
            shifts.map { RecentEntry.Shift(it) }
        }
    }

    suspend fun insertShift(shift: ShiftEntity): Long = shiftDao.insertShift(shift)

    suspend fun insertExpense(expense: ExpenseEntity): Long = expenseDao.insertExpense(expense)

    suspend fun deleteShift(shift: ShiftEntity) = shiftDao.deleteShift(shift)

    suspend fun deleteExpense(expense: ExpenseEntity) = expenseDao.deleteExpense(expense)

    fun getAllShifts(): Flow<List<ShiftEntity>> = shiftDao.getAllShifts()

    fun getAllExpenses(): Flow<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    suspend fun deleteAllShifts() = shiftDao.deleteAllShifts()
}
