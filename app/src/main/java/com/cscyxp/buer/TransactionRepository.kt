package com.cscyxp.buer

import com.cscyxp.buer.db.AppDataBase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.math.BigDecimal
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.random.Random

private const val TAG = "TransactionRepository"
object TransactionRepository {
    private val transactionDao = AppDataBase.instance.transactionDao()

    val transactions = transactionDao.getAllTransactions()

    fun getDailyTransactionsFlow(): Flow<List<DailyTransaction>> {
        return toDailyTransactionsFlow(transactionDao.getAllTransactions())
    }

    fun getTransactionsFlowByFilter(startMonthTs: Long, endMonthTs: Long, categoryId: Long?): Flow<List<Transaction>> {
        return transactionDao.getTransactions(startMonthTs, endMonthTs, categoryId)
    }

    fun getDailyTransactionsFlowByFilter(startMonthTs: Long, endMonthTs: Long, categoryId: Long?): Flow<List<DailyTransaction>> {
        return toDailyTransactionsFlow(transactionDao.getTransactions(startMonthTs, endMonthTs, categoryId))
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


    private val _categories: List<Category> = loadCategoriesFromRaw()
    val categories = _categories.map { it.copy() }
    val topCategories = _categories.map { it.copy() }.filter { it.parentId == null }

    val categoryGrid get(): List<List<Category>> = topCategories.chunked(10)

    fun loadCategoriesFromRaw(): List<Category> {
        val json = MyApp.appContext.resources.openRawResource(R.raw.categories)
            .bufferedReader()
            .use { it.readText() }

        val type = object : TypeToken<List<Category>>() {}.type
        val fromJson = Gson().fromJson<List<Category>>(json, type)
        return fromJson
    }



    suspend fun addTransaction(transaction: Transaction) {
        transactionDao.insert(transaction)
    }

    suspend fun addTransactions(transactions: List<Transaction>) {
        transactionDao.insertList(transactions)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        transactionDao.updateTransaction(transaction)
    }


    suspend fun addTestTransactions() {
        val start = LocalDate.of(2025,1,1)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
            .toEpochMilli()
        val end = LocalDate.of(2025,10,18)
            .atStartOfDay()
            .toInstant(ZoneOffset.UTC)
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