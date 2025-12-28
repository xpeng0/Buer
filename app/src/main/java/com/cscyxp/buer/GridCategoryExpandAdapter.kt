package com.cscyxp.buer

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cscyxp.buer.databinding.ItemCategoryChildBinding
import com.cscyxp.buer.databinding.ItemTagBinding

private const val TAG = "GridCategoryExpandAdapt"
class GridCategoryExpandAdapter(
    var dataList: MutableList<Category>,
    private val onParentsCategoryClick: ((adapter: GridCategoryExpandAdapter, position: Int, category: Category, binding: ItemTagBinding) -> Unit)? = null,
    private val onSonCategoryClick: ((adapter: GridCategoryExpandAdapter, position: Int, category: Category, binding: ItemTagBinding) -> Unit)? = null,
): Adapter<ViewHolder>() {

    companion object {
        const val TYPE_PARENTS = 0
        const val TYPE_CHILD = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == TYPE_PARENTS) {
            val binding = ItemTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ParentsViewHolder(binding)
        } else {
            val binding = ItemCategoryChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ChildViewHolder(binding)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = dataList[position]
        if (holder is ParentsViewHolder) {
            holder.binding.tvTagName.text = category.name

            val resId = MyApp.appContext.resources.getIdentifier(
                category.icon,       // 文件名
                "drawable",      // 资源类型
                MyApp.appContext.packageName // 包名
            )
            holder.binding.ivTagIcon.tag = resId
            if (resId != 0) { // 找到资源
                holder.binding.ivTagIcon.setImageResource(resId)
            } else {
                holder.binding.ivTagIcon.setImageResource(R.drawable.ic_ledger)
            }

            holder.binding.clTag.setOnClickListener{
                val currentPosition = holder.adapterPosition
                onParentsCategoryClick?.invoke(this, currentPosition, dataList[currentPosition], holder.binding)
            }
        } else if (holder is ChildViewHolder) {
            if (category.sonCategories != null) {
                holder.binding.rvSonCategories.apply {
                    layoutManager = GridLayoutManager(holder.itemView.context, 5, GridLayoutManager.VERTICAL, false)
                    adapter = GridCategoryExpandAdapter(
                        category.sonCategories.toMutableList(),
                        onParentsCategoryClick = onSonCategoryClick
                    )
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return dataList[position].adapterType
    }

    class ParentsViewHolder(val binding: ItemTagBinding): ViewHolder(binding.root) {

    }

    class ChildViewHolder(val binding: ItemCategoryChildBinding): ViewHolder(binding.root) {

    }

    private var expandParentsPosition = -1

    fun handleParentsExpand(position: Int, category: Category, rowCount: Int) {
        val currentExpandPosition = dataList.indexOfFirst { it.adapterType == TYPE_CHILD }
        var currentClickPosition = position
        Log.i(TAG, "handleParentsExpand -- currentExpandPosition: $currentExpandPosition currentClickPosition: $currentClickPosition")
        if (currentExpandPosition != -1) {
            Log.i(TAG, "handleParentsExpand: 折叠旧框")
            // 收起旧的
            dataList.removeAt(currentExpandPosition)
            notifyItemRemoved(currentExpandPosition)
            // 重要!!!  索引修正 因为移除了旧的item
            if (currentClickPosition > currentExpandPosition) {
                Log.i(TAG, "handleParentsExpand: 索引修正")
                currentClickPosition -= 1
            }
        }
        if (currentClickPosition != expandParentsPosition) {
            // 不是操作同一个
            // 展开新的
            expandParentsPosition = currentClickPosition
            val idx = ((currentClickPosition / rowCount) + 1) * rowCount
            Log.i(TAG, "handleParentsExpand: 展开位置: $idx  expandParentsPosition: $expandParentsPosition")
            dataList.add(idx, category.copy(adapterType = TYPE_CHILD))
            notifyItemInserted(idx)
        } else {
            expandParentsPosition = -1
        }

    }
}