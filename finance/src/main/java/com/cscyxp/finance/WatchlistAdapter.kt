package com.cscyxp.finance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.cscyxp.finance.databinding.ItemStockBinding
import com.cscyxp.finance.watchlist.ui.state.WatchlistUiState


class WatchlistAdapter: ListAdapter<WatchlistUiState, WatchlistAdapter.StockViewHolder>(StockDiffCallback()) {

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
        val watchlistItem = getItem(position)
        when (watchlistItem) {
            is WatchlistUiState.Success -> {
                binding.tvStockName.text = watchlistItem.stockName
                binding.tvTodayPercent.text = watchlistItem.todayPercent
                val colorRes = when (watchlistItem.todayTrend) {
                    StockTrend.UP -> R.color.stock_red
                    StockTrend.DOWN -> R.color.stock_green
                    StockTrend.FLAT -> R.color.stock_flat
                }
                binding.tvTodayPercent.setTextColor(context.getColor(colorRes))
                binding.lvTrendMinute.setData(
                    watchlistItem.minutes.filterIndexed {idx, _ ->
                        idx % 3 == 0 || idx == watchlistItem.minutes.lastIndex
                    }.map { it.toFloat() }
                )
            }

            is WatchlistUiState.Loading -> {

            }
        }

    }

    class StockViewHolder(
        val viewBinding: ItemStockBinding
    ): ViewHolder(viewBinding.root)

    class StockDiffCallback : DiffUtil.ItemCallback<WatchlistUiState>() {

        // 1. 判断是否是“同一个条目” (身份认同)
        override fun areItemsTheSame(oldItem: WatchlistUiState, newItem: WatchlistUiState): Boolean {
            // 通常比较它们的唯一 ID。在股票里，就是股票代码 (symbol)。
            // 只要 symbol 一样，系统就认为这是同一行，即便价格变了，它也是那只股票。
            return oldItem.stockKey == newItem.stockKey
        }

        // 2. 判断条目的“内容是否发生了变化” (数据认同)
        // ⚠️ 注意：这个方法只有在 areItemsTheSame 返回 true 时才会被系统调用！
        override fun areContentsTheSame(oldItem: WatchlistUiState, newItem: WatchlistUiState): Boolean {
            // 如果你的 StockEntity 是一个 data class，直接用 == 极其方便，
            // 它会自动比对里面的所有属性（价格、涨跌幅、K线等）。
            return oldItem == newItem
        }
    }
}