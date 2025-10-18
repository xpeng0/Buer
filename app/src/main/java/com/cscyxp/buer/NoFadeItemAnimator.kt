package com.cscyxp.buer

import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView
import com.cscyxp.buer.databinding.ItemDailyTransactionBinding

/**
 * 自定义recycler view动画
 * 修改默认的淡入淡出动画 解决Card View淡入淡出时背景闪烁的问题
 */
class NoFadeItemAnimator : DefaultItemAnimator() {


    override fun animateAdd(holder: RecyclerView.ViewHolder?): Boolean {
        if (holder == null) return false

        val initial = holder.itemView.translationX
        holder.itemView.translationX = 1000f
        dispatchAddStarting(holder)
        holder.itemView.animate()
            .translationX(initial)
            .setDuration(600)
            .withEndAction {
                dispatchAddFinished(holder)
            }.start()

        // todo 让除cardview的内容淡入淡出
        if (holder is BaseListAdapter.BaseViewHolder<*>) {
            val binding = holder.viewBinding
            if (binding is ItemDailyTransactionBinding) {

            }
        }

        return true
    }


}