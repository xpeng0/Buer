package com.cscyxp.finance.entity

data class StockEntity(
    val name: String,
    val stockKey: StockKey, // 代码
    val currentPrice: Double,
    val todayPercent: Double, // 当日的涨跌幅百分值
    val kLines: List<KLineEntity>
)
