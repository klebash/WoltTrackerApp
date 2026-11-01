package com.wolttracker.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class ShiftEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String = "Βάρδια",
    val dateMillis: Long,
    val startTimeMillis: Long,
    val endTimeMillis: Long,
    val durationHours: Double,
    val ordersCount: Int,
    val grossIncome: Double,
    val tips: Double = 0.0,
    val notes: String? = null
)
