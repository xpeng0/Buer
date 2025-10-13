package com.cscyxp.buer

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.ZoneId
import java.util.Locale
import kotlin.math.roundToInt

object TransactionRepository {
    private val transactionDao = AppDataBase.instance.transactionDao()

    val transactions = transactionDao.getAllTransactions()

    fun getDailyTransactionsFlow(): Flow<List<DailyTransaction>> {
        val zone = ZoneId.systemDefault()
        return transactionDao.getAllTransactions()
            .map { value: List<Transaction> ->
                value.groupBy { ts ->
                    Instant.ofEpochMilli(ts.date).atZone(zone).toLocalDate()
                }.map { (date, tsList) ->
                    DailyTransaction(
                        date = date,
                        expense = String.format(Locale.getDefault(), "%.2f", tsList.filter { it.type == 0 }.sumOf { it.amount }), // 保留2位小数 double计算结果会出现无限小数
                        income = String.format(Locale.getDefault(),"%.2f", tsList.filter { it.type == 1 }.sumOf { it.amount }), // 保留2位小数 double计算结果出现无限小数
                        transactions = tsList
                    )
                }
            }
    }


    private val _categories: List<Category> = loadCategoriesFromRaw()
    val categories = _categories.map { it.copy() }

    val categoryGrid get(): List<List<Category>> = _categories.map { it.copy() }.chunked(10)

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
}