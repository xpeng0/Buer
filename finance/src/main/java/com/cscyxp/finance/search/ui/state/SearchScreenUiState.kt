package com.cscyxp.finance.search.ui.state

sealed class SearchScreenUiState {
    // 搜索看板 包含历史搜索记录 + 热门搜索
    data class SearchBoard(
        val searchHistory: List<String> = emptyList()
    ): SearchScreenUiState()

    data class SearchResult(
        val resultState: SearchResultState
    ): SearchScreenUiState()
}