package com.cscyxp.buer

import java.math.BigDecimal
import java.time.LocalDate

data class DailyTransaction(
    val date: LocalDate,
    val expense: BigDecimal,
    val income: BigDecimal,
    val transactions: List<Transaction>
)
