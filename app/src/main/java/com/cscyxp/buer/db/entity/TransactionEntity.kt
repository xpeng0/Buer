package com.cscyxp.buer.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val categoryId: Long = 0,
    val title: String = "交易",
    val type: Int = 0, // 0代表支出，1代表收入
    // val tags: List<Category> = emptyList(),
    val amount: Double,
    val date: Long = System.currentTimeMillis()
)
