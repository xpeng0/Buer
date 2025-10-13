package com.cscyxp.buer

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

private const val TAG = "BaseListAdapter"
open class BaseListAdapter<T: Any, VB: ViewBinding>(
    private val getBinging: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val areItemsTheSame: (T, T) -> Boolean,
    private val areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }

    ): ListAdapter<T, BaseListAdapter.BaseViewHolder<VB>>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return areItemsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return areContentsTheSame(oldItem, newItem)
        }

    }
) {


    class BaseViewHolder<VB: ViewBinding>(
        val viewBinding: VB
    ): ViewHolder(viewBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        val baseViewHolder =
            BaseViewHolder(getBinging(LayoutInflater.from(parent.context), parent, false))
        Log.i(TAG, "onCreateViewHolder: ${baseViewHolder.hashCode()}")
        return baseViewHolder
    }


    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {

    }

}