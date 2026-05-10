package com.cscyxp.finance.search.vm

import android.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cscyxp.finance.entity.StockInfo
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.repository.StockRepository
import com.cscyxp.finance.search.ui.state.SearchResultState
import com.cscyxp.finance.search.ui.state.SearchScreenUiState
import com.cscyxp.finance.search.ui.state.StockSearchItemUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class SearchViewModel @Inject constructor(
    private val stockRepository: StockRepository
): ViewModel() {
    private val _inputFlow = MutableStateFlow("")
    val inputFlow: StateFlow<String> = _inputFlow

    private val searchResultFlow = inputFlow
        .debounce {
            if (it.isBlank()) { // 清空输入框立即响应
                0
            } else {
                300
            }
        }
        .distinctUntilChanged()
        .flatMapLatest { input ->
            when {
                input.isBlank() -> flow {
                    emit(SearchTaskState.Idle)
                }

                else -> flow {
                    // 先发射加载状态
                    emit(SearchTaskState.Loading(input))
                    val result = stockRepository.searchStockInfo(input).fold(
                        onSuccess = { infos ->
                            SearchTaskState.Success(input, infos)
                        },
                        onFailure = { e ->
                            SearchTaskState.Error(input, e.toString())
                        }
                    )
                    // 再发射结果
                    emit(result)
                }
            }

        }

    private val watchlistFlow = stockRepository.getWatchlistFlow()

    val searchScreenState = combine(
        searchResultFlow,
        watchlistFlow
    ) { searchTaskState, watchlist ->
        when (searchTaskState) {
            SearchTaskState.Idle -> {
                //TODO 展示search board
                SearchScreenUiState.SearchBoard()
            }

            is SearchTaskState.Error -> {
                val resultState = SearchResultState.Error(
                    input = searchTaskState.input,
                    message = searchTaskState.message
                )
                SearchScreenUiState.SearchResult(resultState)
            }

            is SearchTaskState.Loading -> {
                val resultState = SearchResultState.Loading(searchTaskState.input)
                SearchScreenUiState.SearchResult(resultState)
            }

            is SearchTaskState.Success -> {
                val watchMap = watchlist.associateBy { it.stockKey }
                // 将 stockInfo 与 watchlist 转化成search item的状态
                val stockSearchItems = searchTaskState.searchInfos.map { info ->
                    StockSearchItemUiState(
                        stockKey = info.stockKey,
                        stockName = info.stockName,
                        stockTag = info.stockTag,
                        isWatched = watchMap.contains(info.stockKey)
                    )
                }
                val resultState = SearchResultState.Success(
                    input = searchTaskState.input,
                    stockSearchItems = stockSearchItems

                )
                SearchScreenUiState.SearchResult(resultState)
            }
        }
    }

    fun changeSearchInput(input: String) {
        _inputFlow.value = input
    }

    fun watchStock(add: Boolean, stockKey: StockKey, stockName: String) {
        viewModelScope.launch {
            if (add) {
                stockRepository.addStockToWatchlist(stockKey, stockName)
            } else {
                stockRepository.removeStockFromWatchlist(stockKey, stockName)
            }
        }
    }

    // ViewModel 私有的内部状态：只描述“搜股票这个动作”的客观情况，不包含任何 UI 逻辑
    private sealed class SearchTaskState {
        object Idle : SearchTaskState()
        data class Loading(val input: String) : SearchTaskState()
        data class Success(val input: String, val searchInfos: List<StockInfo>) : SearchTaskState()
        data class Error(val input: String, val message: String) : SearchTaskState()
    }
}