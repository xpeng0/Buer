package com.cscyxp.finance.entity

data class StockQuotation(
    val open: Double,    // 开盘价
    val close: Double,   // 收盘价
    val high: Double,    // 最高价
    val low: Double,     // 最低价
    val percent: Double // 当前涨跌
)