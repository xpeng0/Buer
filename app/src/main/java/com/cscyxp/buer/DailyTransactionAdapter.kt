package com.cscyxp.buer

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.cscyxp.buer.databinding.ItemDailyTransactionBinding
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

private const val TAG = "DailyTransactionAdapter"
class DailyTransactionAdapter(): BaseListAdapter<DailyTransaction, ItemDailyTransactionBinding>(ItemDailyTransactionBinding::inflate,
    areItemsTheSame = {old, new ->
        old.date == new.date
    }
) {

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemDailyTransactionBinding>,
        position: Int
    ) {
        Log.i(TAG, "onBindViewHolder: $position" + " holder code: ${holder.hashCode()}")
        val dailyTransaction = getItem(position)
        holder.viewBinding.tvDate.text = dailyTransaction.date.format(DateTimeFormatter.ofPattern("MM月dd日 E", Locale.CHINA))
        holder.viewBinding.tvIncomeValue.text = dailyTransaction.income
        holder.viewBinding.tvExpenseValue.text = dailyTransaction.expense

        val rv = holder.viewBinding.rvDailyTransactions
        if (rv.layoutManager == null) {
            Log.i(TAG, "设置内层rv layoutManager")
            rv.layoutManager = LinearLayoutManager(holder.itemView.context, LinearLayoutManager.VERTICAL, false)
        }
        if (rv.adapter == null) {
            Log.i(TAG, "设置内层rv adapter")
            rv.adapter = TransactionAdapter{}
        }
        (rv.adapter as TransactionAdapter).submitList(dailyTransaction.transactions)
    }
}