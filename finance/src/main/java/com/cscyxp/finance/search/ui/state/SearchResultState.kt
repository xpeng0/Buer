package com.cscyxp.finance.search.ui.state


sealed class SearchResultState {
    data class Success(
        val input: String,
        val stockSearchItems: List<StockSearchItemUiState>,
    ): SearchResultState()

    data class Error(
        val input: String,
        val message: String
    ): SearchResultState()

    data class Loading(
        val input: String
    ): SearchResultState()

}