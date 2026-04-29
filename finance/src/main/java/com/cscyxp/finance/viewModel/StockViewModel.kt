package com.cscyxp.finance.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cscyxp.finance.StockMathUtil
import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.format2f
import com.cscyxp.finance.repository.StockRepository
import com.cscyxp.finance.ui.state.StockItemUiState
import com.cscyxp.finance.toPercent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModel @Inject constructor(
    private val stockRepository: StockRepository
): ViewModel() {

    private val _queryDayState = MutableStateFlow<Int>(30)
    val queryDayState: StateFlow<Int> = _queryDayState

    // 慢流
    private val watchlistKLinesFlow = queryDayState.flatMapLatest {
        stockRepository.getWatchlistKLines(it)
    }
    // 快流 频繁更新
    private val watchlistQuotationFlow = stockRepository.getWatchlistStockQuotation().distinctUntilChanged()

    // 合并后的ui流
    val watchlistUiStateFlow = combine(watchlistKLinesFlow, watchlistQuotationFlow) { kLineResults, quotationResult ->
        val priceMap = quotationResult.getOrNull()
        // todo 更新kLines
        kLineResults.map { kLineResult ->
            val stockKey = kLineResult.stockKey
            kLineResult.result.fold(
                onSuccess = { stockEntity ->
                    val quotation = priceMap?.get(stockEntity.stockKey)
                    if (quotation != null) {
                        // 用实时数据更新
                        stockEntity.copy(
                            currentPrice = quotation.close,
                            todayPercent = quotation.percent,
                        ).toStockItemUiState()
                    } else {
                        stockEntity.toStockItemUiState()
                    }
                },
                onFailure = { e ->
                    StockItemUiState.Error(
                        stockKey = stockKey,
                        message = e.toString()
                    )
                }
            )
        }
    }.onStart {
        stockRepository.getWatchlist().map {

        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addStockToWatchlist(stockKey: StockKey) {
        viewModelScope.launch {
            stockRepository.addStockToWatchlist(stockKey)
        }

    }

}




fun StockEntity.toStockItemUiState(): StockItemUiState {
    val max = kLines.maxOfOrNull { it.close } ?: currentPrice
    val min = kLines.minOfOrNull { it.close } ?: currentPrice
    val todayTrend = if (todayPercent > 0) {
        StockTrend.UP
    } else if (todayPercent < 0) {
        StockTrend.DOWN
    } else {
        StockTrend.FLAT
    }
    return StockItemUiState.Success(
        stockKey = stockKey,
        name = name,
        currentPrice = currentPrice.format2f(),
        todayTrend = todayTrend,
        todayPercent = todayPercent.toPercent(),
        highPrice = max.format2f(),
        highPercent = StockMathUtil.calculateChangePercent(currentPrice, max),
        lowPrice = min.format2f(),
        lowPercent = StockMathUtil.calculateChangePercent(currentPrice, min),
        kLines = kLines
    )
}