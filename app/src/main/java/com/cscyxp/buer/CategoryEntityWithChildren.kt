package com.cscyxp.buer

import androidx.room.Embedded
import androidx.room.Relation
import com.cscyxp.buer.db.entity.CategoryEntity

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
