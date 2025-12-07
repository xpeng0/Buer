package com.cscyxp.buer

import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import com.cscyxp.buer.databinding.ItemTagBinding
import com.cscyxp.buer.databinding.ItemTagPagerBinding

class CategoryPagerAdapter(
    private val categoryPages: List<List<Category>>,
    private val onCategoryClick: (CategoryPagerAdapter, CategoryGridAdapter, pagePosition: Int, gridPosition: Int, category: Category, binding: ItemTagBinding) -> Unit
): BaseAdapter<List<Category>, ItemTagPagerBinding>(
    ItemTagPagerBinding::inflate,
    categoryPages.toMutableList()
) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTagPagerBinding>, position: Int) {
        val adapter = CategoryGridAdapter {gridAdapter, gridPosition, category, binding ->
            val pagePosition = holder.adapterPosition
            onCategoryClick(this, gridAdapter, pagePosition, gridPosition, category, binding)
        }
        adapter.submitList(categoryPages[position])
        holder.viewBinding.rvTagPager.adapter = adapter
        holder.viewBinding.rvTagPager.layoutManager = GridLayoutManager(holder.itemView.context, 5)
    }
}