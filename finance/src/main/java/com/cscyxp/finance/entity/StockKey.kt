package com.cscyxp.finance.entity

import com.cscyxp.finance.StockExchange

data class StockKey(
    val symbol: String,
    val exchange: StockExchange,
)
