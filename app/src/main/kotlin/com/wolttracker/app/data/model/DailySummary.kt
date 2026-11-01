package com.wolttracker.app.data.model

data class DailySummary(
    val grossIncome: Double = 0.0,
    val totalHours: Double = 0.0,
    val totalOrders: Int = 0,
    val totalTips: Double = 0.0
) {
    val totalEarnings: Double
        get() = grossIncome + totalTips
}
