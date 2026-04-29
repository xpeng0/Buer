package com.cscyxp.finance.search.ui.state

import com.cscyxp.finance.entity.StockKey

data class StockSearchItemUiState(
    val stockKey: StockKey,
    val stockName: String,
    val stockTag: StockTag,
    val isWatched: Boolean
)