package com.cscyxp.buer

import android.util.Log
import androidx.lifecycle.ViewModel
import com.cscyxp.xpviews.BarChartView
import com.cscyxp.xpviews.PieChartView
import kotlinx.coroutines.flow.Flow
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

    /**
     * 将属于二级分类的记录统计至一级分类下
     */
    suspend fun toTopTransactions(transactions: List<Transaction>): List<Pair<Long, List<Transaction>>> {
        val categoryMap = CategoryRepository.getAllCategories().groupBy { it.id }
        val sonTransactionsMap = transactions
            .filter { categoryMap[it.categoryId]?.get(0)?.parentId != null }
            .groupBy { categoryMap[it.categoryId]?.get(0)?.parentId }

        return transactions
            .filter {
                val category = categoryMap[it.categoryId]?.get(0)
                category != null && category.parentId == null
            }
            .groupBy { it.categoryId }
            .map { (id, transactions) ->
                val allTransactions = sonTransactionsMap.getOrDefault(id, emptyList()).toMutableList()
                allTransactions.addAll(transactions)
                id to allTransactions.toList()
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

    suspend fun toPieEntry2(transactions: List<Transaction>): List<PieChartView.PieEntry> {
        return toTopTransactions(transactions)
            .map { (categoryId, transactions) ->
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

    suspend fun toCategoryChart2(transactions: List<Transaction>): List<CategoryChart> {
        val topTransactions = toTopTransactions(transactions)
        val max = topTransactions.maxOfOrNull { pair ->
            pair.second.sumOf { it.amount }
        }
        if (max == null || max == 0.0) return emptyList()
        return topTransactions
            .map { (categoryId, transactions) ->
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
            toPieEntry2(transactions) to toCategoryChart2(transactions)
        }
    }

    fun setMonth(month: Int) {
        filter.value = filter.value.copy(
            month = month
        )
    }

    fun getCategoryChartUiState(categoryId: Long): Flow<Pair<List<PieChartView.PieEntry>, List<CategoryChart>>> {
        return filterMonthTransactions.map { transactions ->
            Log.i(TAG, "getCategoryChartUiState: ${transactions}")
            val categoryTs = toTopTransactions(transactions)
                .filter { it.first == categoryId }
                .map { it.second }

            if (categoryTs.isEmpty()) {
                emptyList<PieChartView.PieEntry>() to emptyList()
            } else {
                toPieEntry(categoryTs[0]) to toCategoryChart(categoryTs[0])
            }
        }
    }
}