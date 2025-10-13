package com.cscyxp.buer

import com.cscyxp.buer.databinding.ItemTagBinding

class CategoryGridAdapter(
    private val onCategoryClick: (CategoryGridAdapter, Int) -> Unit
): BaseListAdapter<Category, ItemTagBinding>(ItemTagBinding::inflate, {old, new -> old.id == new.id}) {

    companion object {
        const val UPDATE_BACKGROUND = "bg"
    }


    override fun onBindViewHolder(holder: BaseViewHolder<ItemTagBinding>, position: Int) {
        val category = getItem(position)
        holder.viewBinding.clTag.isSelected = category.isSelected
        holder.viewBinding.tvTagName.text = category.name

        val resId = MyApp.appContext.resources.getIdentifier(
            category.icon,       // 文件名
            "drawable",      // 资源类型
            MyApp.appContext.packageName // 包名
        )
        holder.viewBinding.ivTagIcon.tag = resId
        if (resId != 0) { // 找到资源
            holder.viewBinding.ivTagIcon.setImageResource(resId)
        } else {
            holder.viewBinding.ivTagIcon.setImageResource(R.drawable.ic_ledger)
        }

        holder.viewBinding.clTag.setOnClickListener{
            val currentPosition = holder.adapterPosition
            onCategoryClick(this, currentPosition)
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemTagBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else if (payloads.contains(UPDATE_BACKGROUND)) {
            // 局部刷新背景
            val category = getItem(position)
            holder.viewBinding.clTag.isSelected = category.isSelected
        }
    }
}