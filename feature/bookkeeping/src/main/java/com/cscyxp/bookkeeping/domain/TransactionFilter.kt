package com.cscyxp.bookkeeping.domain

data class TransactionFilter(
    val month: Int,
    val year: Int,
    val category: Category? = null
)
