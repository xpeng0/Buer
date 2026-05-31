package com.cscyxp.bookkeeping.data.repository

import com.cscyxp.bookkeeping.data.dao.TransactionDao
import com.cscyxp.bookkeeping.data.entity.TransactionEntityWithCategoryEntity
import com.cscyxp.bookkeeping.domain.DailyTransaction
import com.cscyxp.bookkeeping.domain.Transaction
import com.cscyxp.bookkeeping.util.getLocalDateTime
import com.cscyxp.bookkeeping.util.toEndOfDay
import com.cscyxp.bookkeeping.util.toTransaction
import com.cscyxp.bookkeeping.util.toTransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlin.random.Random

class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getTransactionsFlowByFilter(startMonthTs: Long, endMonthTs: Long, categoryId: Long? = null): Flow<List<Transaction>> {
        return transactionDao.getTransactions(startMonthTs, endMonthTs, categoryId).map { list: List<TransactionEntityWithCategoryEntity> ->
            list.map { it.toTransaction() }
        }
    }

    fun getDailyTransactionsFlowByFilter(startMonthTs: Long, endMonthTs: Long, categoryId: Long?): Flow<List<DailyTransaction>> {
        return toDailyTransactionsFlow(getTransactionsFlowByFilter(startMonthTs, endMonthTs, categoryId))
    }

    private fun toDailyTransactionsFlow(transactionsFlow: Flow<List<Transaction>>): Flow<List<DailyTransaction>> {
        val zone = ZoneId.systemDefault()
        return transactionsFlow.map { value: List<Transaction> ->
            value.groupBy { ts ->
                Instant.ofEpochMilli(ts.date).atZone(zone).toLocalDate()
            }.map { (date, tsList) ->
                DailyTransaction(
                    date = date,
                    expense = tsList.filter { it.type == 0 }.map { BigDecimal.valueOf(it.amount) }.sumOf { it },
                    income = tsList.filter { it.type == 1}.map { BigDecimal.valueOf(it.amount) }.sumOf { it },
                    transactions = tsList
                )
            }
        }
    }

    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction.toTransactionEntity())
    }

    suspend fun addTransactions(transactions: List<Transaction>) {
        transactionDao.insertList(transactions.map { it.toTransactionEntity() })
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction.toTransactionEntity())
    }

    suspend fun addTestTransactions() {
        val start = java.time.LocalDate.of(2025,1,1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val end = java.time.LocalDate.of(2025,12,26)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        addTransactions(List(1000) {
            Transaction(
                categoryId = Random.nextLong(1, 10),
                amount = Random.nextDouble(1.00, 999.99),
                date = Random.nextLong(start, end)
            )
        })
    }
}
