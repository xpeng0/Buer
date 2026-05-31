package com.cscyxp.finance.details.vm

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.details.ui.state.StockDetailUiState
import com.cscyxp.finance.details.ui.state.TouchInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.format2f
import com.cscyxp.finance.navigation.FinanceDetail
import com.cscyxp.finance.repository.StockRepository
import com.cscyxp.finance.toPercent
import com.cscyxp.finance.toTrend
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StockDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val stockRepository: StockRepository
) : ViewModel() {

    private val route = savedStateHandle.toRoute<FinanceDetail>()
    private val _stockKey = MutableStateFlow(
        StockKey(route.symbol, StockExchange.valueOf(route.exchange))
    )
    private val _touchIndex = MutableStateFlow<Int?>(null)

    val stateFlow: StateFlow<StockDetailUiState> = combine(
        _stockKey,
        stockRepository.watchlistCacheMap,
        _touchIndex,
    ) { key, cacheMap, touchIndex ->
        val cache = cacheMap[key]
        if (cache == null) {
            StockDetailUiState.Loading(
                stockKey = key
            )
        } else {
            val minutes = cache.minutes
            StockDetailUiState.Success(
                stockKey = key,
                stockName = cache.stockName,
                currentPrice = cache.currentPrice.format2f(),
                todayPercent = cache.todayPercent.toPercent(),
                todayTrend = cache.todayPercent.toTrend(),
                high = cache.high.format2f(),
                low = cache.low.format2f(),
                minutes = minutes.map { it.toFloat() },
                touchInfo = touchIndex?.let {
                    val price = minutes.getOrNull(touchIndex)
                    if (price != null) {
                        val preClosePrice = cache.preClosePrice
                        TouchInfo(
                            index = touchIndex,
                            price = price.format2f(),
                            percent = ((price - preClosePrice) * 100 / preClosePrice).toPercent()
                        )
                    } else {
                        null
                    }
                }
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = StockDetailUiState.Loading(_stockKey.value)
    )

    init {
        startPolling()
    }

    fun onPointSelected(index: Int?) {
        _touchIndex.value = index
    }

    fun startPolling() {
        viewModelScope.launch {
            _stockKey.collectLatest { key ->
                if (key.symbol.isNotEmpty()) {
                    while (true) {
                        stockRepository.updateCacheWithQt(listOf(key))
                        delay(5 * 1000)
                    }
                }
            }
        }
    }

}
