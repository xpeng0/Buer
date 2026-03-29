package com.cscyxp.buer


import android.view.View
import com.cscyxp.buer.db.entity.CategoryEntity
import com.cscyxp.buer.db.entity.CategoryEntityWithChildren
import com.cscyxp.buer.db.entity.TransactionEntity
import com.cscyxp.buer.db.entity.TransactionEntityWithCategoryEntity
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Locale

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
        parentId = this.parentId,
        sonCategories = emptyList()
    )
}

fun CategoryEntityWithChildren.toCategory(): Category {
    return Category(
        id = this.categoryEntity.id,
        name = this.categoryEntity.name,
        type = this.categoryEntity.type,
        icon = this.categoryEntity.icon,
        parentId = this.categoryEntity.parentId,
        sonCategories = this.children.map { it.toCategory() }
    )
}

fun TransactionEntityWithCategoryEntity.toTransaction(): Transaction {
    return Transaction(
        id = this.transactionEntity.id,
        title = this.transactionEntity.title,
        type = this.transactionEntity.type,
        amount = this.transactionEntity.amount,
        date = this.transactionEntity.date,
        categoryId = this.transactionEntity.categoryId,
        category = this.categoryEntity?.toCategory() ?: Category.DEFAULT
    )
}

fun Transaction.getLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this.date)  // 将时间戳转换为 Instant
        .atZone(ZoneId.systemDefault())     // 转换为指定时区的 ZonedDateTime
        .toLocalDateTime()
}

fun Transaction.toTransactionEntity(): TransactionEntity {
    return TransactionEntity(
        id = this.id,
        categoryId = this.categoryId,
        title = this.title,
        type = this.type,
        amount = this.amount,
        date = this.date
    )
}

fun TransactionEntity.toTransaction(): Transaction {
    return Transaction(
        id = this.id,
        categoryId = this.categoryId,
        title = this.title,
        type = this.type,
        amount = this.amount,
        date = this.date
    )
}

fun Double.format2f(): String {
    return String.format(Locale.getDefault(), "%.2f", this)
}

// 用于UI展示
fun BigDecimal.format2f(): String {
    return String.format(Locale.getDefault(), "%.2f", this)
}

// 用于计算
fun BigDecimal.scale2f(): String {
    return this.setScale(2, RoundingMode.HALF_UP).toPlainString()
}

fun LocalDate.toEndOfDay(zone: ZoneId): Long {
    return this.atTime(LocalTime.MAX).atZone(zone).toInstant().toEpochMilli()
}
