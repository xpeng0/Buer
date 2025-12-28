package com.cscyxp.buer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

private const val TAG = "MainViewModel"


class MainViewModel: ViewModel() {


    // 过滤器
    private val _filter = MutableStateFlow(TransactionFilter(LocalDate.now().monthValue))

    val filter: StateFlow<TransactionFilter> = _filter

    // 当日支出
    private val _todayTransactions = MutableStateFlow("0.00")
    val todayExpand: StateFlow<String> = _todayTransactions

    // 本月支出
    private val _monthTransactions = MutableStateFlow("0.00")
    val monthExpand: StateFlow<String> = _monthTransactions


    init {
        viewModelScope.launch {
            launch {
                val startTs = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endTs = LocalDate.now().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                TransactionRepository.getTransactionsFlowByFilter(startTs, endTs).collect { list ->
                    _todayTransactions.value = list.filter { it.type == 0 }.sumOf { it.amount }.format2f()
                }
            }

            launch {
                val startTs = LocalDate.now().withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                val endTs = YearMonth.now().atEndOfMonth().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                TransactionRepository.getTransactionsFlowByFilter(startTs, endTs).collect { list ->
                    _monthTransactions.value = list.filter { it.type == 0 }.sumOf { it.amount }.format2f()
                }
            }
        }
    }

    // 根据月份动态切换查询 Flow
    val dailyTransactions = filter.flatMapLatest { filter ->
        val startMonthTs = LocalDate.of(2025, filter.month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val lastDay = YearMonth.of(2025, filter.month).lengthOfMonth()
        val endMonthTs = LocalDate.of(2025, filter.month, lastDay)
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        TransactionRepository.getDailyTransactionsFlowByFilter(startMonthTs, endMonthTs, filter.category?.id) // 返回 Flow<List<Record>>
    }

    fun setMonth(month: Int) {
        _filter.value = filter.value.copy(
            month = month
        )
    }

    fun getMonth(): Int {
        return filter.value.month
    }

    fun setCategory(category: Category) {
        _filter.value = filter.value.copy(
            category = category
        )
    }

    fun getCategory(): Category? {
        return filter.value.category
    }

    // 更新Transaction
    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            TransactionRepository.updateTransaction(transaction)
        }
    }
}