package com.cscyxp.finance.search.ui

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cscyxp.finance.R
import com.cscyxp.finance.databinding.ItemStockSearchBinding
import com.cscyxp.finance.entity.StockKey
import com.cscyxp.finance.search.ui.state.StockSearchItemUiState

class StockSearchListAdapter(
    val onToggleClick: (Boolean, StockKey) -> Unit
): ListAdapter<StockSearchItemUiState, StockSearchListAdapter.StockSearchViewHolder>(StockDiffCallback()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockSearchViewHolder {
        val binding = ItemStockSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockSearchViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StockSearchViewHolder,
        position: Int
    ) {
        val binding = holder.viewBinding
        val item = getItem(position)
        val context = binding.root.context
        binding.tvStockName.text = item.stockName
        binding.tvStockSymbol.text = item.stockKey.symbol
        binding.tvStockTag.text = item.stockTag.displayName
        binding.tvStockTag.setTextColor(context.getColor(item.stockTag.textColorRes))
        binding.tvStockTag.backgroundTintList = ColorStateList.valueOf(context.getColor(item.stockTag.bgColorRes))
        if (item.isWatched) {
            binding.ivToggleAdd.setImageResource(R.drawable.ic_remove_round_24)
            binding.ivToggleAdd.imageTintList = ColorStateList.valueOf(context.getColor(R.color.white))
            binding.ivToggleAdd.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.black))
        } else {
            binding.ivToggleAdd.setImageResource(R.drawable.ic_add_round_24)
            binding.ivToggleAdd.imageTintList = ColorStateList.valueOf(context.getColor(R.color.black))
            binding.ivToggleAdd.backgroundTintList = ColorStateList.valueOf("#F0F0F0".toColorInt())
        }
        binding.ivToggleAdd.setOnClickListener {
            onToggleClick(!item.isWatched, item.stockKey)
        }
    }

    class StockSearchViewHolder(
        val viewBinding: ItemStockSearchBinding
    ): ViewHolder(viewBinding.root)

    class StockDiffCallback : DiffUtil.ItemCallback<StockSearchItemUiState>() {

        // 1. 判断是否是“同一个条目” (身份认同)
        override fun areItemsTheSame(oldItem: StockSearchItemUiState, newItem: StockSearchItemUiState): Boolean {
            return oldItem.stockKey == newItem.stockKey
        }

        // 2. 判断条目的“内容是否发生了变化” (数据认同)
        override fun areContentsTheSame(oldItem: StockSearchItemUiState, newItem: StockSearchItemUiState): Boolean {
            return oldItem == newItem
        }
    }
}