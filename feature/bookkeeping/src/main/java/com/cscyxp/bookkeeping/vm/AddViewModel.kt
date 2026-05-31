package com.cscyxp.bookkeeping.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cscyxp.bookkeeping.data.repository.CategoryRepository
import com.cscyxp.bookkeeping.data.repository.TransactionRepository
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.KeyAction
import com.cscyxp.bookkeeping.domain.Transaction
import com.cscyxp.bookkeeping.util.TimeHelper
import com.cscyxp.bookkeeping.util.toEndOfDay
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
import javax.inject.Inject

@HiltViewModel
class AddViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val timeHelper: TimeHelper
): ViewModel() {

    companion object {
        const val OPEN_DATE_PICKER = 1
        const val BACK = 1
    }

    private val _selectedCategoryState = MutableStateFlow(SelectedCategoryState())
    val selectedCategoryState: StateFlow<SelectedCategoryState> = _selectedCategoryState

    fun onTagClick(
        pagePosition: Int,
        gridPosition: Int,
        category: Category?
    ){
        if (isReselect(pagePosition, gridPosition)) {
            onTagClick(-1, -1, null)
            return
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

    val date = MutableStateFlow<LocalDate?>(null)
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
            openDatePicker.emit(OPEN_DATE_PICKER)
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
                back.emit(BACK)
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
        return date.value?.toEndOfDay(ZoneId.systemDefault()) ?: timeHelper.currentTimeMillis()
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
