package com.cscyxp.buer


import android.view.View

/**
 * 切换 View 的选中状态
 */
fun View.toggleSelection() {
    isSelected = !isSelected
}
