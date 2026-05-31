package com.cscyxp.bookkeeping.domain

data class Transaction(
    val id: Long = 0,
    val categoryId: Long = 0,
    val title: String = "交易",
    val type: Int = 0, // 0代表支出，1代表收入
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val category: Category = Category(id = categoryId, type = type, name = "default", icon = "")
)
