package com.cscyxp.finance.entity

data class StockQuotation(
    val open: Double,
    val close: Double,
    val high: Double,
    val low: Double,
    val percent: Double,
    val time: Long
)