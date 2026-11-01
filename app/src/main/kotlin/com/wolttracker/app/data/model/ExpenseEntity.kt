package com.wolttracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ExpenseCategory(val displayName: String) {
    FUEL("Βενζίνη"),
    FOOD("Καφές & Σνακ"),
    MAINTENANCE("Σέρβις / Επισκευή"),
    OTHER("Άλλο")
}

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val shiftId: Long? = null,
    val dateMillis: Long,
    val timestampMillis: Long,
    val category: ExpenseCategory,
    val title: String,
    val amount: Double,
    val notes: String? = null
)
