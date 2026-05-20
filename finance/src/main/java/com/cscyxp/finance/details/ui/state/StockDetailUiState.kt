package com.cscyxp.finance.details.ui.state

import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute

sealed class StockDetailUiState {
    abstract val stockKey: StockKey

    data class Success(
        override val stockKey: StockKey,
        val stockName: String,
        val currentPrice: String,
        val todayPercent: String,
        val high: String,
        val low: String,
        val minutes: List<Float>,
    ): StockDetailUiState()

    data class Loading(
        override val stockKey: StockKey
    ): StockDetailUiState()

    data class Error(
        override val stockKey: StockKey
    ): StockDetailUiState()
}