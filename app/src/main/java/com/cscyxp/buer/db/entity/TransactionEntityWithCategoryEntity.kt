package com.cscyxp.buer.db.entity

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
    val categoryEntity: CategoryEntity? // todo 设置为可空 防止意外崩溃
)
