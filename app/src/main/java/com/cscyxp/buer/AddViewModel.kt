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
    var selectedPage = -1
    var selectedGrid = -1
    var selectedCategory: Category? = null

    fun onTagClick(
        pagePosition: Int,
        gridPosition: Int,
        category: Category?
    ){
        selectedPage = pagePosition
        selectedGrid = gridPosition
        selectedCategory = category
    }

    fun onSonCategoryClick(
        category: Category
    ){
        selectedCategory = category
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
                categoryId = selectedCategory?.id ?: 0,
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

    fun isReselect(pagePosition: Int, gridPosition: Int): Boolean {
        return pagePosition == selectedPage && gridPosition == selectedGrid
    }

}