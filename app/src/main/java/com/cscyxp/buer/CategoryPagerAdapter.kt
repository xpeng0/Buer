package com.cscyxp.buer

import androidx.recyclerview.widget.GridLayoutManager
import com.cscyxp.buer.databinding.ItemTagPagerBinding

class CategoryPagerAdapter(
    private val categoryPages: List<List<Category>>,
    private val onCategoryClick: (CategoryPagerAdapter, CategoryGridAdapter, gridPosition: Int, pagePosition: Int) -> Unit
): BaseAdapter<List<Category>, ItemTagPagerBinding>(
    ItemTagPagerBinding::inflate,
    categoryPages.toMutableList()
) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTagPagerBinding>, position: Int) {
        val adapter = CategoryGridAdapter {gridAdapter, gridPosition ->
            val pagePosition = holder.adapterPosition
            onCategoryClick(this, gridAdapter, pagePosition, gridPosition)
        }
        adapter.submitList(categoryPages[position])
        holder.viewBinding.rvTagPager.adapter = adapter
        holder.viewBinding.rvTagPager.layoutManager = GridLayoutManager(holder.itemView.context, 5)
    }
}