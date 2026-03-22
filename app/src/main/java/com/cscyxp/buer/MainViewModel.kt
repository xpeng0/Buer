package com.cscyxp.buer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cscyxp.buer.utils.TimeHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

private const val TAG = "MainViewModel"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val timeHelper: TimeHelper,
): ViewModel() {


    // 过滤器
    private val _filter = MutableStateFlow(TransactionFilter(LocalDate.now().monthValue, LocalDate.now().year))

    val filter: StateFlow<TransactionFilter> = _filter

    // 当日支出
    val todayExpend = getExpendStateFlow(
        timeHelper.getTodayStartTimeMillis(),
        timeHelper.getTodayEndTimeMillis()
    )

    // 本月支出
    val monthExpend = getExpendStateFlow(
        timeHelper.getCurrentMonthStartTimeMillis(),
        timeHelper.getCurrentMonthEndTimeMillis()
    )


    private fun getTransactionExpendSum(list: List<Transaction>): String {
        return list.filter { it.type == 0 }.sumOf { it.amount }.format2f()
    }

    private fun getExpendStateFlow(startTime: Long, endTime: Long): StateFlow<String> {
        return transactionRepository
            .getTransactionsFlowByFilter(startTime, endTime)
            .map { getTransactionExpendSum(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "0.00")
    }

    // 根据月份动态切换查询 Flow
    val homeUiState = filter
        .flatMapLatest { filter ->
            transactionRepository.getDailyTransactionsFlowByFilter(
                timeHelper.getMonthStartTimeMillis(filter.year, filter.month),
                timeHelper.getMonthEndTimeMillis(filter.year, filter.month),
                filter.category?.id
            )
        }
        .map { dailyTransactions ->
            val expenseSum = dailyTransactions.sumOf { it.expense }
            val incomeSum = dailyTransactions.sumOf { it.income }
            HomeUiState(
                dailyTransactions = dailyTransactions,
                expenseSumStr = expenseSum.format2f(),
                incomeSumStr = incomeSum.format2f(),
                balanceStr = (incomeSum - expenseSum).format2f()
            )
        }
        .stateIn(
            scope = viewModelScope, // 作用域
            started = SharingStarted.WhileSubscribed(5_000), // 没有订阅者5s停止监听
            initialValue = HomeUiState() // 初始值
        )

    fun setMonth(month: Int) {
        updateFilter(filter.value.copy(month = month))
    }

    fun getMonth(): Int {
        return filter.value.month
    }

    fun setYear(year: Int) {
        updateFilter(filter.value.copy(year = year))
    }

    fun getYear(): Int {
        return filter.value.year
    }

    fun setCategory(category: Category) {
        updateFilter(filter.value.copy(category = category))
    }

    fun getCategory(): Category? {
        return filter.value.category
    }

    fun updateFilter(filter: TransactionFilter) {
        _filter.value = filter
    }

    // 更新Transaction
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionRepository.updateTransaction(transaction)
        }
    }


    // ------------------ 分类相关 -------------------

    // 分类过滤器
    private val _categoryFilter = MutableStateFlow(Category.TYPE_EXPAND)
    val categoryFilter = _categoryFilter

    // 分类数据
    val topCategoriesByFilter = categoryFilter.flatMapLatest { type ->
        categoryRepository.getTopCategories(type)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategoryDialogFilterType(type: Int) {
        _categoryFilter.value = type
    }
}

data class HomeUiState(
    val dailyTransactions: List<DailyTransaction> = emptyList(),
    val expenseSumStr: String = "0.00",
    val incomeSumStr: String = "0.00",
    val balanceStr: String = "0.00"
)