package com.cscyxp.buer

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cscyxp.xpviews.BarChartView
import com.cscyxp.xpviews.PieChartView
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.math.RoundingMode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZoneId

private const val TAG = "ChartViewModel"
class ChartViewModel: ViewModel() {
    // 当前选中的月份
    val filter = MutableStateFlow(TransactionFilter(LocalDate.now().monthValue))

    // 根据月份动态切换查询 Flow
    val filterMonthTransactions = filter.flatMapLatest { filter ->
        val startMonthTs = LocalDate.of(2025, filter.month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endMonthTs = LocalDate.of(2025, filter.month, 1)
            .plusMonths(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        TransactionRepository.getTransactionsFlowByFilter(startMonthTs, endMonthTs, filter.category?.id) // 返回 Flow<List<Record>>
    }

    val recentSixMonthBarEntry = let {
        val startMonth = LocalDate.now().minusMonths(5).monthValue
        val endMonth = LocalDate.now().monthValue
        val startMonthTs = LocalDate.now()
            .minusMonths(5)
            .withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val barEntries = MutableList(6) {
            BarChartView.BarEntry("${it + startMonth}月", 0.00f)
        }
        TransactionRepository.getDailyTransactionsFlowByFilter(startMonthTs, System.currentTimeMillis(), null)
            .map { dailyTransactions ->
                Log.i(TAG, "on recentSixMonthTransactions Collected ---- dailyTransactions: $dailyTransactions")
                dailyTransactions.groupBy { dailyTransaction ->
                    dailyTransaction.date.monthValue
                }.forEach { (month, dailyTransaction) ->
                    barEntries[month - startMonth] = BarChartView.BarEntry("${month}月", dailyTransaction.sumOf { it.expense }.toFloat())
                }
                barEntries
            }
    }

    suspend fun toPieEntry(transactions: List<Transaction>): List<PieChartView.PieEntry> {
        return transactions.groupBy {
            it.categoryId
        }.map { (categoryId, transactions) ->
            PieChartView.PieEntry(
                TransactionRepository.categories.find { it.id == categoryId }?.name ?: "空",
                transactions.sumOf { it.amount }
            )
        }.sortedBy {
            -it.value
        }

    }

    suspend fun toCategoryChart(transactions: List<Transaction>): List<CategoryChart> {
        val max = transactions.groupBy { it.categoryId }.maxOfOrNull { it.value.sumOf { it.amount } }
        if (max == null) return emptyList()
        return transactions.groupBy {
            it.categoryId
        }.map { (categoryId, transactions) ->
            CategoryChart(
                TransactionRepository.categories.find { it.id == categoryId } ?: Category(0, "", 0, ""),
                value = transactions.sumOf { it.amount },
                progress = Math.round(transactions.sumOf { it.amount } * 100 / max).toInt()
            )
        }.sortedBy {
            -it.value
        }
    }

    val chartUIState = let {
        filterMonthTransactions.map { transactions ->
            toPieEntry(transactions) to toCategoryChart(transactions)
        }
    }

    fun setMonth(month: Int) {
        filter.value = filter.value.copy(
            month = month
        )
    }
}