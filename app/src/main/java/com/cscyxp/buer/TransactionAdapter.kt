package com.cscyxp.buer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cscyxp.buer.databinding.ItemDailyTransactionBinding
import com.cscyxp.buer.databinding.ItemTransactionBinding
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : BaseListAdapter<Transaction, ItemTransactionBinding>(ItemTransactionBinding::inflate, {old, new -> old.id == new.id}) {

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTransactionBinding>, position: Int) {
        val item = getItem(position)
        var category = TransactionRepository.categories.firstOrNull() { it.id == item.categoryId }
        if (category == null) {
            category = Category(0, "消费", 0, "ic_ledger")
        }
        holder.viewBinding.tvTitle.text = category.name
        var iconId = MyApp.appContext.resources.getIdentifier(
            category.icon,       // 文件名
            "drawable",      // 资源类型
            MyApp.appContext.packageName // 包名
        )
        if (iconId == 0) iconId = R.drawable.ic_ledger
        holder.viewBinding.ivIcon.setImageResource(iconId)
        var type = "-"
        if (item.type == 1) type = "+"
        holder.viewBinding.tvAmount.text = "${type}${item.amount}"
        holder.viewBinding.tvTime.text = DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
            .format(Instant.ofEpochMilli(item.date))
    }
}
