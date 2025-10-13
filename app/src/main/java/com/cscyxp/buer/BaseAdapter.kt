package com.cscyxp.buer

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewbinding.ViewBinding

open class BaseAdapter<T, VB: ViewBinding>(
    private val getBinging: (LayoutInflater, ViewGroup, Boolean) -> VB,
    private val items: List<T>
): RecyclerView.Adapter<BaseAdapter.BaseViewHolder<VB>>() {


    class BaseViewHolder<VB: ViewBinding>(
        val viewBinding: VB
    ): ViewHolder(viewBinding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VB> {
        return BaseViewHolder(getBinging(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }


    override fun onBindViewHolder(holder: BaseViewHolder<VB>, position: Int) {

    }
}