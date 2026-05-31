package com.cscyxp.bookkeeping.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class TransactionEntityWithCategoryEntity(
    @Embedded
    val transactionEntity: TransactionEntity,
    @Relation(
        entity = CategoryEntity::class,
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val categoryEntity: CategoryEntity?
)
