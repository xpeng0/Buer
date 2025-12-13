package com.cscyxp.buer

import com.cscyxp.buer.databinding.ItemCategoryChartBinding

class CategoryChartAdapter(
    val onItemClick: (
        adapter: CategoryChartAdapter,
        position: Int, categoryChart: CategoryChart) -> Unit
): BaseListAdapter<CategoryChart, ItemCategoryChartBinding>(
    getBinging = ItemCategoryChartBinding::inflate,
    areItemsTheSame = {item1, item2 ->
        // 会导致刷新后位置乱跳
        // item1.category == item2.category
        true
    }
) {
    override fun onBindViewHolder(holder: BaseViewHolder<ItemCategoryChartBinding>, position: Int) {
        val (category, value, progress) = getItem(position)
        val viewBinding = holder.viewBinding
        var iconId = MyApp.appContext.resources.getIdentifier(
            category.icon,       // 文件名
            "drawable",      // 资源类型
            MyApp.appContext.packageName // 包名
        )
        if (iconId == 0) iconId = R.drawable.ic_ledger
        viewBinding.ivIcon.setImageResource(iconId)
        viewBinding.tvName.text = category.name
        viewBinding.tvValue.text = "¥%.2f".format(value)
        viewBinding.lpRatio.progress = progress

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            onItemClick(this, currentPosition, getItem(currentPosition))
        }
    }
}