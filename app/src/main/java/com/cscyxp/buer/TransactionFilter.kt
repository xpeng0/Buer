package com.cscyxp.buer

data class TransactionFilter(
    val month: Int,
    val year: Int,
    val category: Category? = null
) {
}