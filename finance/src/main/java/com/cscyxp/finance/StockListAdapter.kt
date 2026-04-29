package com.cscyxp.finance

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cscyxp.finance.databinding.ItemStockBinding
import com.cscyxp.finance.ui.state.StockItemUiState
import androidx.core.view.isVisible
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager


class StockListAdapter: ListAdapter<StockItemUiState, StockListAdapter.StockViewHolder>(StockDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StockViewHolder {
        val binding = ItemStockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StockViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: StockViewHolder,
        position: Int
    ) {
        val binding = holder.viewBinding
        val context = binding.root.context
        val stockItem = getItem(position)
        when (stockItem) {
            is StockItemUiState.Error -> {

            }
            is StockItemUiState.Success -> {
                binding.tvStockName.text = stockItem.name
                binding.tvStockPrice.text = stockItem.currentPrice
                binding.tvTodayPercent.text = stockItem.todayPercent
                val colorRes = when (stockItem.todayTrend) {
                    StockTrend.UP -> R.color.stock_red
                    StockTrend.DOWN -> R.color.stock_green
                    StockTrend.FLAT -> R.color.stock_flat
                }
                binding.tvTodayPercent.setTextColor(context.getColor(colorRes))
                binding.tvHighPrice.text = stockItem.highPrice
                binding.tvHighPercent.text = stockItem.highPercent
                binding.tvLowPrice.text = stockItem.lowPrice
                binding.tvLowPercent.text = stockItem.lowPercent
                // todo 改为状态控制
                binding.llStockInfo.setOnClickListener { v ->
                    // 🌟 移花接木：拿到父控件 RecyclerView
                    val recyclerView = binding.root.parent as? ViewGroup
                    // 把动画挂在 RecyclerView 上，让它带着卡片一起平滑伸缩！
                    recyclerView?.let {
                        TransitionManager.beginDelayedTransition(it, AutoTransition())
                    }
                    binding.lvTrend.setData(stockItem.kLines.map {
                        it.close.toFloat()
                    })
                    binding.lvTrend.goneOrVisible()
                }
            }

            is StockItemUiState.Loading -> TODO()
        }

    }

    class StockViewHolder(
        val viewBinding: ItemStockBinding
    ): ViewHolder(viewBinding.root)

    class StockDiffCallback : DiffUtil.ItemCallback<StockItemUiState>() {

        // 1. 判断是否是“同一个条目” (身份认同)
        override fun areItemsTheSame(oldItem: StockItemUiState, newItem: StockItemUiState): Boolean {
            // 通常比较它们的唯一 ID。在股票里，就是股票代码 (symbol)。
            // 只要 symbol 一样，系统就认为这是同一行，即便价格变了，它也是那只股票。
            return oldItem.stockKey == newItem.stockKey
        }

        // 2. 判断条目的“内容是否发生了变化” (数据认同)
        // ⚠️ 注意：这个方法只有在 areItemsTheSame 返回 true 时才会被系统调用！
        override fun areContentsTheSame(oldItem: StockItemUiState, newItem: StockItemUiState): Boolean {
            // 如果你的 StockEntity 是一个 data class，直接用 == 极其方便，
            // 它会自动比对里面的所有属性（价格、涨跌幅、K线等）。
            return oldItem == newItem
        }
    }
}