package com.cscyxp.buer

import com.cscyxp.buer.databinding.ItemTagBinding

data class Category(
    val id: Long,
    val name: String,
    val type: Int,
    val icon: String,
    val parentId: Long? = null,
    val adapterType: Int = GridCategoryExpandAdapter.TYPE_PARENTS,
    val sonCategories: List<Category>? = emptyList(),
)
