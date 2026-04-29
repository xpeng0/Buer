package com.cscyxp.finance.entity

import com.cscyxp.finance.search.ui.state.StockTag

data class StockInfo(
    val stockKey: StockKey,
    val stockName: String,
    val stockTag: StockTag
)
