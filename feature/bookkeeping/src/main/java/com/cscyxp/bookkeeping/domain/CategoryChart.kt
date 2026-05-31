package com.cscyxp.bookkeeping.domain

data class CategoryChart(
    val category: Category,
    val value: Double,
    val progress: Int
)
