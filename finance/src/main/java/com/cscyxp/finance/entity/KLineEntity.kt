package com.cscyxp.finance.entity

data class KLineEntity (
    val date: String,    // 日期
    val open: Double,    // 开盘价
    val close: Double,   // 收盘价
    val high: Double,    // 最高价
    val low: Double,     // 最低价
    val volume: Double   // 成交量
)