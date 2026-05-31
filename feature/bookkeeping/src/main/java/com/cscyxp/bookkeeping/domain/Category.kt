package com.cscyxp.bookkeeping.domain

data class Category(
    val id: Long,
    val name: String,
    val type: Int,
    val icon: String,
    val parentId: Long? = null,
    val sonCategories: List<Category>? = emptyList(),
) {
    companion object {
        val DEFAULT = Category(
            id = 10000,
            name = "默认",
            type = 0,
            icon = "ic_default"
        )
        const val TYPE_EXPAND = 0
        const val TYPE_INCOME = 1
    }
}
