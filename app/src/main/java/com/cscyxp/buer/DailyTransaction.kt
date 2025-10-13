package com.cscyxp.buer

import java.time.LocalDate

data class DailyTransaction(
    val date: LocalDate,
    val expense: String,
    val income: String,
    val transactions: List<Transaction>
)
