package com.cscyxp.bookkeeping.ui.state

import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.DailyTransaction
import com.cscyxp.bookkeeping.domain.TransactionFilter

sealed class TransactionListUiState {
    data object Loading : TransactionListUiState()

    data class Content(
        val filter: TransactionFilter,
        val dailyTransactions: List<DailyTransaction> = emptyList(),
        val expenseSumStr: String = "0.00",
        val incomeSumStr: String = "0.00",
        val balanceStr: String = "0.00",
        val categoryDialogFilterType: Int = Category.Companion.TYPE_EXPAND,
        val topCategories: List<Category> = emptyList()
    ) : TransactionListUiState()

    data class Error(val message: String) : TransactionListUiState()
}