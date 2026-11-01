package com.wolttracker.app.data.model

sealed interface RecentEntry {
    val timestampMillis: Long

    data class Shift(
        val shift: ShiftEntity
    ) : RecentEntry {
        override val timestampMillis: Long get() = shift.endTimeMillis
    }

    data class Expense(
        val expense: ExpenseEntity
    ) : RecentEntry {
        override val timestampMillis: Long get() = expense.timestampMillis
    }
}
