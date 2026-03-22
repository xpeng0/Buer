package com.cscyxp.buer

import com.cscyxp.buer.db.AppDataBase
import com.cscyxp.buer.db.entity.TransactionEntityWithCategoryEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "TransactionRepository"
class TransactionRepository @Inject constructor(

) {
    private val transactionDao = AppDataBase.instance.transactionDao()


    fun getTransactionsFlowByFilter(startMonthTs: Long, endMonthTs: Long, categoryId: Long? = null): Flow<List<Transaction>> {
        return transactionDao.getTransactions(startMonthTs, endMonthTs, categoryId).map { list: List<TransactionEntityWithCategoryEntity> ->
            list.map {
                it.toTransaction()
            }
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
                    expense = tsList.filter { it.type == 0 }.map { BigDecimal.valueOf(it.amount) }.sumOf { it }, // double计算结果会出现精度丢失 转为BigDecimal
                    income = tsList.filter { it.type == 1}.map { BigDecimal.valueOf(it.amount) }.sumOf { it }, // double计算结果会出现精度丢失 转为BigDecimal
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
        val start = LocalDate.of(2025,1,1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        val end = LocalDate.of(2025,12,26)
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