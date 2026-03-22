package com.cscyxp.buer

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.doOnAttach
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.cscyxp.buer.databinding.ItemTagBinding
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

private const val TAG = "CategoryGridAdapter"
class CategoryGridAdapter(
    private val selectedCategoryPositionFlow: Flow<CategoryGridAdapterState>? = null,
    private val onCategoryClick: (CategoryGridAdapter, Int, Category, ItemTagBinding) -> Unit
): BaseListAdapter<Category, ItemTagBinding>(ItemTagBinding::inflate, {old, new -> old.id == new.id}) {

    companion object {
        const val UPDATE_BACKGROUND_CHECKED = "bg_checked"
        const val UPDATE_BACKGROUND_UNCHECKED = "bg_unchecked"
        const val UPDATE_CATEGORY_INFO = "category"
    }

    private var state = CategoryGridAdapterState()
    private var selectionJob: Job? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // 获取宿主生命周期 开始监听
        recyclerView.doOnAttach {
            val lifecycleOwner = recyclerView.findViewTreeLifecycleOwner()
            Log.i(TAG, "onAttachedToRecyclerView: lifecycleScope: ${lifecycleOwner?.lifecycleScope}")
            selectionJob = lifecycleOwner?.lifecycleScope?.launch {
                selectedCategoryPositionFlow?.collect { newSelectedState ->
                    Log.i(TAG, "newSelectedState: $newSelectedState")
                    if (state.position != -1) {
                        notifyItemChanged(state.position, UPDATE_BACKGROUND_UNCHECKED)
                    }

                    if (newSelectedState.position != -1) {
                        notifyItemChanged(newSelectedState.position, UPDATE_BACKGROUND_CHECKED)

                        // todo状态恢复时 collect在onBind之后 可能会闪
                        if (getItem(newSelectedState.position) != newSelectedState.category) {
                            // 更新icon与text
                            notifyItemChanged(newSelectedState.position, UPDATE_CATEGORY_INFO)
                        }
                    }
                    state = newSelectedState
                }
            }
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        selectionJob?.cancel()
        selectionJob = null
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemTagBinding>, position: Int) {
        val category = getItem(position)
        changeText(holder.viewBinding.tvTagName, category)
        changeIcon(holder.viewBinding.ivTagIcon, category)


        holder.viewBinding.clTag.setOnClickListener{
            val currentPosition = holder.adapterPosition
            onCategoryClick(this, currentPosition, getItem(currentPosition), holder.viewBinding)
        }
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<ItemTagBinding>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
            return
        }
        if (payloads.contains(UPDATE_BACKGROUND_CHECKED)) {
            // 局部刷新背景
            holder.viewBinding.clTag.isSelected = true
        } else if (payloads.contains(UPDATE_BACKGROUND_UNCHECKED)) {
            // 局部刷新背景
            holder.viewBinding.clTag.isSelected = false
        }

        if (payloads.contains(UPDATE_CATEGORY_INFO)) {
            val selectedCategory = state.category
            if (selectedCategory != null) {
                changeText(holder.viewBinding.tvTagName, selectedCategory)
                changeIcon(holder.viewBinding.ivTagIcon, selectedCategory)
            }
        }
    }

    private fun changeIcon(imageView: ImageView, category: Category) {
        val resId = MyApp.appContext.resources.getIdentifier(
            category.icon,       // 文件名
            "drawable",      // 资源类型
            MyApp.appContext.packageName // 包名
        )
        imageView.tag = resId
        if (resId != 0) { // 找到资源
            imageView.setImageResource(resId)
        } else {
            imageView.setImageResource(R.drawable.ic_ledger)
        }
    }

    private fun changeText(textView: TextView, category: Category) {
        textView.text = category.name
    }
}

data class CategoryGridAdapterState(
    val position: Int = -1,
    val category: Category? = null
)