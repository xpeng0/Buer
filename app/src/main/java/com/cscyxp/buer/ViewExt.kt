package com.cscyxp.buer


import android.view.View
import com.cscyxp.buer.db.entity.CategoryEntity

/**
 * 切换 View 的选中状态
 */
fun View.toggleSelection() {
    isSelected = !isSelected
}

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = this.id,
        name = this.name,
        type = this.type,
        icon = this.icon,
        parentId = this.parentId
    )
}
