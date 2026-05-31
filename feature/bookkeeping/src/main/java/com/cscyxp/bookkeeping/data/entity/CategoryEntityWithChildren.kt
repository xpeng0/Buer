package com.cscyxp.bookkeeping.data.entity

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryEntityWithChildren(
    @Embedded
    val categoryEntity: CategoryEntity,
    @Relation(
        entity = CategoryEntity::class,
        parentColumn = "id",
        entityColumn = "parent_id"
    )
    val children: List<CategoryEntity>
)
