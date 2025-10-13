package com.cscyxp.buer

import java.time.LocalDate

data class DailyTransaction(
    val date: LocalDate,
    val expense: Double,
    val income: Double,
    val transactions: List<Transaction>
)
