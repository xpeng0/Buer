package com.cscyxp.buer

import android.util.Log
import androidx.collection.longIntMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val TAG = "AddViewModel"
/**
 * 处理tag跨页面单选逻辑
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,

    ): ViewModel() {
    private val _selectedCategoryState = MutableStateFlow(SelectedCategoryState())

    val selectedCategoryState: StateFlow<SelectedCategoryState> = _selectedCategoryState

    fun onTagClick(
        pagePosition: Int,
        gridPosition: Int,
        category: Category?
    ){
        Log.i(TAG, "onTagClick: pagePosition: $pagePosition, gridPosition: $gridPosition, category: $category")
        if (isReselect(pagePosition, gridPosition)) {
            onTagClick(-1, -1, null)
        }
        _selectedCategoryState.update { it.copy(
            page = pagePosition,
            grid = gridPosition,
            category = category
        ) }
    }

    fun onSonCategoryClick(
        category: Category
    ){
        _selectedCategoryState.update { it.copy(category = category) }
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
                categoryId = selectedCategoryState.value.category?.id ?: 0,
                date = getCheckDate()
            )
            viewModelScope.launch {
                transactionRepository.addTransaction(transaction)
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
        return pagePosition == selectedCategoryState.value.page && gridPosition == selectedCategoryState.value.grid
    }

    suspend fun getTopCategoryGrids(): List<List<Category>>{
        return categoryRepository.getTopCategories().chunked(10)
    }

}
data class SelectedCategoryState(
    val page: Int = -1,
    val grid: Int = -1,
    val category: Category? = null
)