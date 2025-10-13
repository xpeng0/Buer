package com.cscyxp.buer

import androidx.lifecycle.ViewModel

/**
 * 处理tag跨页面单选逻辑
 */
class AddViewModel: ViewModel() {
    val categoryGrids = TransactionRepository.categoryGrid
    private var selectedPage = -1
    private var selectedGrid = -1
    val selectedCategoryId: Long
        get() =
            if (selectedPage != -1 && selectedGrid != -1) categoryGrids[selectedPage][selectedGrid].id
            else -1

    fun onTagClick(
        pageAdapter: CategoryPagerAdapter,
        gridAdapter: CategoryGridAdapter,
        pagePosition: Int,
        gridPosition: Int,
    ){
        // 点击同一个
        if (pagePosition == selectedPage && gridPosition == selectedGrid) {
            categoryGrids[selectedPage][selectedGrid].toggleSelection()
            gridAdapter.notifyItemChanged(gridPosition, CategoryGridAdapter.UPDATE_BACKGROUND)
            selectedPage = -1
            selectedGrid = -1
            return
        }

        // 将上次选择的复原
        if (selectedPage != -1 && selectedGrid != -1) {
            categoryGrids[selectedPage][selectedGrid].isSelected = false
            // 上次选中项不属于当前页时直接刷新page
            if (selectedPage != pagePosition) pageAdapter.notifyItemChanged(selectedPage)
            else gridAdapter.notifyItemChanged(selectedGrid, CategoryGridAdapter.UPDATE_BACKGROUND)
        }

        // 处理当前选择item
        selectedPage = pagePosition
        selectedGrid = gridPosition
        categoryGrids[selectedPage][selectedGrid].isSelected = true
        gridAdapter.notifyItemChanged(gridPosition, CategoryGridAdapter.UPDATE_BACKGROUND)
    }
}