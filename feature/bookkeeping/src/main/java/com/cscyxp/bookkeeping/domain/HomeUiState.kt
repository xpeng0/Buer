package com.cscyxp.bookkeeping.domain

data class HomeUiState(
    val dailyTransactions: List<DailyTransaction> = emptyList(),
    val expenseSumStr: String = "0.00",
    val incomeSumStr: String = "0.00",
    val balanceStr: String = "0.00"
)
