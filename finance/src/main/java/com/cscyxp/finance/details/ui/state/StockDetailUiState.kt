package com.cscyxp.finance.details.ui.state

import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.entity.StockMinute

sealed class StockDetailUiState {
    abstract val stockKey: StockKey

    data class Success(
        override val stockKey: StockKey,
        val stockName: String,
        val currentPrice: String,
        val todayPercent: String,
        val todayTrend: StockTrend,
        val high: String,
        val low: String,
        val minutes: List<Float>,
        val touchInfo: TouchInfo? = null
    ): StockDetailUiState() {
        val displayPrice: String
            get() = touchInfo?.price ?: currentPrice

        // UI 想要展示的涨跌幅
        val displayPercent: String
            get() = touchInfo?.percent ?: todayPercent
    }

    data class Loading(
        override val stockKey: StockKey
    ): StockDetailUiState()

    data class Error(
        override val stockKey: StockKey
    ): StockDetailUiState()
}

data class TouchInfo(
    val index: Int,
    val price: String,
    val percent: String
)