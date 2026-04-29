package com.cscyxp.finance.ui.state

import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.entity.KLineEntity
import com.cscyxp.finance.entity.StockKey

sealed class StockItemUiState {
    // 🌟 核心魔法：要求所有亲生子类，必须随身携带 stockKey！
    abstract val stockKey: StockKey
    data class Success(
        override val stockKey: StockKey = StockKey("", StockExchange.SHANG_HAI),
        val name: String = "",
        val todayTrend: StockTrend = StockTrend.FLAT,
        val todayPercent: String = "", // 涨跌百分比
        val highPrice: String = "",
        val highPercent: String = "", // 距区间最高点跌幅
        val lowPrice: String = "",
        val lowPercent: String = "", // 距区间最低点的涨幅
        val currentPrice: String = "",
        val kLines: List<KLineEntity> = emptyList()
    ): StockItemUiState()

    data class Error(
        override val stockKey: StockKey,
        val message: String = ""
    ): StockItemUiState()


    data class Loading(
        override val stockKey: StockKey
    ): StockItemUiState()
}