package com.cscyxp.bookkeeping.vm

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cscyxp.bookkeeping.data.repository.TransactionRepository
import com.cscyxp.bookkeeping.domain.CategoryChart
import com.cscyxp.bookkeeping.domain.Transaction
import com.cscyxp.bookkeeping.domain.TransactionFilter
import com.cscyxp.xpviews.BarChartView
import com.cscyxp.xpviews.PieChartView
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

private const val TAG = "ChartViewModel"

@HiltViewModel
class ChartViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
): ViewModel() {
    val filter = MutableStateFlow(TransactionFilter(LocalDate.now().monthValue, LocalDate.now().year))

    val filterMonthTransactions = filter.flatMapLatest { filter ->
        var yearValue = LocalDate.now().year
        if (filter.month > LocalDate.now().monthValue) {
            yearValue = LocalDate.now().minusYears(1).year
        }
        val startMonthTs = LocalDate.of(yearValue, filter.month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val endMonthTs = LocalDate.of(yearValue, filter.month, 1)
            .plusMonths(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        transactionRepository.getTransactionsFlowByFilter(startMonthTs, endMonthTs, filter.category?.id)
    }

    val recentSixMonthBarEntry = let {
        val startMonth = LocalDate.now().minusMonths(5).monthValue
        val startMonthTs = LocalDate.now()
            .minusMonths(5)
            .withDayOfMonth(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val barEntries = MutableList(6) {
            BarChartView.BarEntry("${(it + startMonth) % 12}月", 0.00f)
        }
        transactionRepository.getDailyTransactionsFlowByFilter(startMonthTs, System.currentTimeMillis(), null)
            .map { dailyTransactions ->
                Log.i(TAG, "on recentSixMonthTransactions Collected ---- dailyTransactions: $dailyTransactions")
                dailyTransactions.groupBy { dailyTransaction ->
                    dailyTransaction.date.monthValue
                }.forEach { (month, dailyTransaction) ->
                    barEntries[(month + 12 - startMonth) % 12] = BarChartView.BarEntry("${month}月", dailyTransaction.sumOf { it.expense }.toFloat())
                }
                barEntries
            }
    }

    private suspend fun toTopTransactions(transactions: List<Transaction>): List<Pair<com.cscyxp.bookkeeping.domain.Category, List<Transaction>>> {
        val sonTransactionsMap = transactions
            .filter { it.category.parentId != null }
            .groupBy { it.category.parentId }

        return transactions
            .filter { it.category.parentId == null }
            .groupBy { it.category }
            .map { (category, transactions) ->
                val allTransactions = sonTransactionsMap.getOrDefault(category.id, emptyList()).toMutableList()
                allTransactions.addAll(transactions)
                category to allTransactions.toList()
            }
    }

    private suspend fun toPieEntry(transactions: List<Transaction>): List<PieChartView.PieEntry> {
        return transactions.groupBy { it.category }
            .map { (category, transactions) ->
                PieChartView.PieEntry(category.name, transactions.sumOf { it.amount })
            }.sortedBy { -it.value }
    }

    private suspend fun toPieEntry2(transactions: List<Transaction>): List<PieChartView.PieEntry> {
        return toTopTransactions(transactions)
            .map { (category, transactions) ->
                PieChartView.PieEntry(category.name, transactions.sumOf { it.amount })
            }.sortedBy { -it.value }
    }

    private suspend fun toCategoryChart(transactions: List<Transaction>): List<CategoryChart> {
        val max = transactions.groupBy { it.categoryId }.maxOfOrNull { it.value.sumOf { it.amount } }
        if (max == null) return emptyList()
        return transactions.groupBy { it.category }
            .map { (category, transactions) ->
                CategoryChart(
                    category,
                    value = transactions.sumOf { it.amount },
                    progress = Math.round(transactions.sumOf { it.amount } * 100 / max).toInt()
                )
            }.sortedBy { -it.value }
    }

    private suspend fun toCategoryChart2(transactions: List<Transaction>): List<CategoryChart> {
        val topTransactions = toTopTransactions(transactions)
        val max = topTransactions.maxOfOrNull { pair ->
            pair.second.sumOf { it.amount }
        }
        if (max == null || max == 0.0) return emptyList()
        return topTransactions
            .map { (category, transactions) ->
                CategoryChart(
                    category,
                    value = transactions.sumOf { it.amount },
                    progress = Math.round(transactions.sumOf { it.amount } * 100 / max).toInt()
                )
            }.sortedBy { -it.value }
    }

    val chartUIState = let {
        filterMonthTransactions.map { transactions ->
            toPieEntry2(transactions) to toCategoryChart2(transactions)
        }
    }

    fun setMonth(month: Int) {
        filter.value = filter.value.copy(month = month)
    }

    fun getCategoryChartUiState(categoryId: Long): Flow<Pair<List<PieChartView.PieEntry>, List<CategoryChart>>> {
        return filterMonthTransactions.map { transactions ->
            Log.i(TAG, "getCategoryChartUiState: ${transactions}")
            val categoryTs = toTopTransactions(transactions)
                .filter { it.first.id == categoryId }
                .map { it.second }

            if (categoryTs.isEmpty()) {
                emptyList<PieChartView.PieEntry>() to emptyList()
            } else {
                toPieEntry(categoryTs[0]) to toCategoryChart(categoryTs[0])
            }
        }
    }
}
