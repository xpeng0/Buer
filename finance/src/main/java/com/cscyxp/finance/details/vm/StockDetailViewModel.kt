package com.cscyxp.finance.details.vm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.cscyxp.finance.StockExchange
import com.cscyxp.finance.details.ui.state.StockDetailUiState
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.format2f
import com.cscyxp.finance.repository.StockRepository
import com.cscyxp.finance.toPercent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class StockDetailViewModel @Inject constructor(
    private val handle: SavedStateHandle,
    private val stockRepository: StockRepository
) : ViewModel() {
    val stateFlow = handle.getStateFlow("stockKey", StockKey("", StockExchange.SHANG_HAI))
        .mapLatest { key ->
            stockRepository.getStockMinute(key).fold(
                onSuccess = { stockMinute ->
                    StockDetailUiState.Success(
                        stockKey = key,
                        stockName = stockMinute.stockName,
                        currentPrice = stockMinute.currentPrice.format2f(),
                        todayPercent = stockMinute.todayPercent.toPercent(),
                        high = stockMinute.high.format2f(),
                        low = stockMinute.low.format2f(),
                        minutes = stockMinute.minutes.map { it.toFloat() }
                    )
                },
                onFailure = { e ->
                    StockDetailUiState.Error(key)
                }
            )
    }




}