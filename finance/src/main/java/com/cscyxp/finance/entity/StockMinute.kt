package com.cscyxp.finance.entity

data class StockMinute(
    val stockKey: StockKey,
    val stockName: String,
    val currentPrice: Double,
    val todayPercent: Double,
    val high: Double,
    val low: Double,
    val time: Long,
    val minutes: List<Double>
)
