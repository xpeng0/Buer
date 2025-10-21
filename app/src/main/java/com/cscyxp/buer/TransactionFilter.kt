package com.cscyxp.buer

data class TransactionFilter(
    val month: Int,
    val categoryId: Long? = null
) {
}