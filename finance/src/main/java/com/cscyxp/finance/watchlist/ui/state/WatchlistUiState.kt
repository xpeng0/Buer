package com.cscyxp.finance.watchlist.ui.state

import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.entity.StockKey

sealed class WatchlistUiState {
    abstract val stockKey: StockKey
    abstract val stockName: String


    data class Success(
        override val stockKey: StockKey,
        override val stockName: String,
        val currentPrice: String,
        val todayPercent: String,
        val todayTrend: StockTrend,
        val high: Double,
        val low: Double,
        val minutes: List<Double>
    ): WatchlistUiState()

    data class Loading(
        override val stockKey: StockKey,
        override val stockName: String
    ): WatchlistUiState()


}
