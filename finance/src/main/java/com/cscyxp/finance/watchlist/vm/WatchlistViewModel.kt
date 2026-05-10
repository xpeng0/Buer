package com.cscyxp.finance.watchlist.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cscyxp.finance.StockMathUtil
import com.cscyxp.finance.StockTrend
import com.cscyxp.finance.entity.StockEntity
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.format2f
import com.cscyxp.finance.repository.StockRepository
import com.cscyxp.finance.watchlist.ui.state.StockItemUiState
import com.cscyxp.finance.toPercent
import com.cscyxp.finance.watchlist.ui.state.WatchlistUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class WatchlistViewModel @Inject constructor(
    private val stockRepository: StockRepository,

): ViewModel() {

    private val _queryDayState = MutableStateFlow<Int>(30)
    val queryDayState: StateFlow<Int> = _queryDayState

    private val _visibleWatchStock = MutableStateFlow<Set<StockKey>>(emptySet())
    private val _nearbyWatchStock = MutableStateFlow<Set<StockKey>>(emptySet())
    private val isActive = true
    private val watchlistFlow = stockRepository.getWatchlistFlow()


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



    val watchlistUiStates =
        combine(watchlistFlow, stockRepository.watchlistCacheMap) { watchlist, cacheMap ->
            watchlist.map {
                val cache = cacheMap[it.stockKey]
                if (cache == null) {
                    WatchlistUiState.Loading(
                        stockKey = it.stockKey,
                        stockName = it.stockName
                    )
                } else {
                    WatchlistUiState.Success(
                        stockKey = it.stockKey,
                        stockName = it.stockName,
                        currentPrice = cache.currentPrice.toString(),
                        todayPercent = cache.todayPercent.toPercent(),
                        todayTrend = if (cache.todayPercent > 0) {
                            StockTrend.UP
                        } else if (cache.todayPercent < 0) {
                            StockTrend.DOWN
                        } else {
                            StockTrend.FLAT
                        },
                        high = cache.high,
                        low = cache.low,
                        minutes = cache.minutes
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        startVisibleFreqPolling()
        startNearbyFreqPolling()
    }

    fun updateVisibleWatchStock(set: Set<StockKey>) {
        Log.d("yxp", "updateVisibleWatchStock: $set")
        _visibleWatchStock.update {
            set
        }
    }

    fun updateNearbyWatchStock(set: Set<StockKey>) {
        _nearbyWatchStock.update {
            set
        }
    }

    private fun startVisibleFreqPolling() {
        viewModelScope.launch {

            _visibleWatchStock
                .debounce(200)
                .collectLatest { keys ->
                    while (isActive) {
                        if (keys.isNotEmpty()) {
                            stockRepository.updateCacheWithQt(keys.toList())
                        }
                        delay(1000 * 5) // 严格 5 秒心跳
                    }
                }
        }

    }

    private fun startNearbyFreqPolling() {
        viewModelScope.launch {
            _nearbyWatchStock
                .debounce(200)
                .collectLatest {
                    while (true) {
                        val keys = _nearbyWatchStock.value.toList()
                        if (keys.isNotEmpty()) {
                            // 防止蓝区太大（比如 40 个），分块发送保护 URL 长度
                            keys.chunked(20).forEach { chunk ->
                                launch {
                                    stockRepository.updateCacheWithQt(chunk)
                                }
                            }
                        }
                        delay(1000 * 60 * 2) // 严格 2 分钟心跳
                    }
                }
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