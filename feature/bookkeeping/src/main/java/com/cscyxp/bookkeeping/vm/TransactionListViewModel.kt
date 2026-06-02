package com.cscyxp.bookkeeping.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cscyxp.bookkeeping.data.repository.CategoryRepository
import com.cscyxp.bookkeeping.data.repository.TransactionRepository
import com.cscyxp.bookkeeping.domain.Category
import com.cscyxp.bookkeeping.domain.Transaction
import com.cscyxp.bookkeeping.domain.TransactionFilter
import com.cscyxp.bookkeeping.ui.state.TransactionListUiState
import com.cscyxp.bookkeeping.util.TimeHelper
import com.cscyxp.bookkeeping.util.format2f
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TransactionListViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val timeHelper: TimeHelper,
): ViewModel() {

    private val _filter = MutableStateFlow(TransactionFilter(LocalDate.now().monthValue, LocalDate.now().year))
    private val _categoryFilter = MutableStateFlow(Category.TYPE_EXPAND)

    private val categoryDialogState = _categoryFilter.flatMapLatest { type ->
        categoryRepository.getTopCategories(type).map { categories ->
            type to categories
        }
    }

    val uiState: StateFlow<TransactionListUiState> = combine(
        _filter.flatMapLatest { filter ->
            transactionRepository.getDailyTransactionsFlowByFilter(
                startMonthTs = timeHelper.getMonthStartTimeMillis(filter.year, filter.month),
                endMonthTs = timeHelper.getMonthEndTimeMillis(filter.year, filter.month),
                categoryId = filter.category?.id
            ).map { dailyTransactions ->
                val expenseSum = dailyTransactions.sumOf { it.expense }
                val incomeSum = dailyTransactions.sumOf { it.income }
                TransactionListUiState.Content(
                    filter = filter,
                    dailyTransactions = dailyTransactions,
                    expenseSumStr = expenseSum.format2f(),
                    incomeSumStr = incomeSum.format2f(),
                    balanceStr = (incomeSum - expenseSum).format2f()
                )
            }
        },
        categoryDialogState
    ) { content, (categoryFilter, topCategories) ->
        content.copy(
            categoryDialogFilterType = categoryFilter,
            topCategories = topCategories
        ) as TransactionListUiState
    }
        .catch { emit(TransactionListUiState.Error(it.message ?: "加载交易失败")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TransactionListUiState.Loading
        )

    fun setMonth(month: Int) {
        updateFilter(_filter.value.copy(month = month))
    }

    fun setYear(year: Int) {
        updateFilter(_filter.value.copy(year = year))
    }

    fun setCategory(category: Category) {
        updateFilter(_filter.value.copy(category = category))
    }

    fun updateFilter(filter: TransactionFilter) {
        _filter.value = filter
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }

    fun setCategoryDialogFilterType(type: Int) {
        _categoryFilter.value = type
    }
}
