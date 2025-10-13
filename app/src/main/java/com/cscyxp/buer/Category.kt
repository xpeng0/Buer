package com.cscyxp.buer

data class Category(
    val id: Long,
    val name: String,
    val type: Int,
    val icon: String,
    var isSelected: Boolean = false

) {
    fun toggleSelection() {
        isSelected = !isSelected
    }
}
