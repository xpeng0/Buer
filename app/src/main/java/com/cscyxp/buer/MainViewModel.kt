package com.cscyxp.buer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

private const val TAG = "MainViewModel"


class MainViewModel: ViewModel() {

    val adapter by lazy { DailyTransactionAdapter() }

    // 当前选中的月份
    val filter = MutableStateFlow(TransactionFilter(LocalDate.now().monthValue))

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
        filter.value = filter.value.copy(
            month = month
        )
    }

    fun getMonth(): Int {
        return filter.value.month
    }

    fun setCategory(category: Category) {
        filter.value = filter.value.copy(
            category = category
        )
    }

    fun getCategory(): Category? {
        return filter.value.category
    }
}