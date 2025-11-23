package com.cscyxp.buer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private const val TAG = "AddViewModel"
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

    val date = MutableStateFlow(LocalDate.now())
    val amount = MutableStateFlow("")
    val back = MutableSharedFlow<Int>()
    val openDatePicker = MutableSharedFlow<Int>()

    fun onDateCheckListener(utc: Long) {
        val pickerDate = Instant.ofEpochMilli(utc)
            .atOffset(ZoneOffset.UTC)
            .toLocalDate()
        date.value = pickerDate
    }


    fun handleKeyAction(key: KeyAction) {
        when (key) {
            KeyAction.DATE -> onDateKeyClick()
            KeyAction.DOT -> onDotKeyClick()
            KeyAction.DELETE -> onDeleteKeyClick()
            KeyAction.OK -> onOkKeyClick()
            else -> onDigitalKeyClick(key)
        }

    }

    private fun onDateKeyClick() {
        viewModelScope.launch {
            openDatePicker.emit(1)
        }
    }
    private fun onDotKeyClick() {
        if (amount.value.isEmpty()) {
            amount.value = amount.value.plus("0")
        }
        if (!amount.value.contains(".")) {
            amount.value = amount.value.plus(".")
        }
    }
    private fun onDeleteKeyClick() {
        amount.value = amount.value.dropLast(1)
    }

    private fun onOkKeyClick() {
        val amount = amount.value.toDoubleOrNull()
        if (amount != null && amount != 0.0) {
            val transaction = Transaction(
                amount = amount,
                categoryId = selectedCategoryId,
                date = getCheckDate()
            )
            viewModelScope.launch {
                TransactionRepository.addTransaction(transaction)
                back.emit(1)
            }

        }
    }

    private fun onDigitalKeyClick(key: KeyAction) {

        val indexOf = amount.value.indexOf(".")
        if (indexOf != -1) {
            if (amount.value.length - (indexOf + 1) < 2) {
                amount.value = amount.value.plus(key.keyName)
            }
        } else {
            amount.value = amount.value.plus(key.keyName)

        }
    }

    fun getCheckDate(): Long {
        return if (date.value == LocalDate.now()) {
            System.currentTimeMillis()
        } else {
            date.value.atTime(23, 59)
                .atZone(ZoneId.systemDefault())
                .toInstant().toEpochMilli()
        }
    }

}