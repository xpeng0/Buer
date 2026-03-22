package com.cscyxp.buer.utils

import com.cscyxp.buer.DailyTransaction
import com.cscyxp.buer.Transaction
import com.cscyxp.buer.scale2f
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import kotlin.math.min
import kotlin.random.Random

object TransactionGenerator {

    /**
     * 生成随机记账记录
     * @param incomeCount 收入类型数量
     * @param expenseCount 支出类型数量
     * @return 记录列表与总收入/支出
     */
    fun generateRandomTransactions(
        incomeCount: Int = 0,
        expenseCount: Int = 0
    ): GenerateTransaction {
        val transactions = mutableListOf<Transaction>()
        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO

        repeat(incomeCount) {
            val amountBD = BigDecimal.valueOf(Random.nextDouble(0.01, 9999.99)).setScale(2, RoundingMode.HALF_UP)
            transactions.add(Transaction(type = 1, amount = amountBD.toDouble()))
            totalIncome += amountBD
        }

        repeat(expenseCount) {
            val amountBD = BigDecimal.valueOf(Random.nextDouble(0.01, 9999.99)).setScale(2, RoundingMode.HALF_UP)
            transactions.add(Transaction(type = 0, amount = amountBD.toDouble()))
            totalExpense += amountBD
        }

        return GenerateTransaction(
            transactions = transactions.shuffled(), // 打乱原有顺序
            totalIncome = totalIncome.scale2f(),
            totalExpense = totalExpense.scale2f()
        )
    }

    fun generateRandomTransaction(
        type: Int = 0
    ): Transaction {
        val amountBD = BigDecimal.valueOf(Random.nextDouble(0.01, 9999.99)).setScale(2, RoundingMode.HALF_UP)
        return Transaction(type = type, amount = amountBD.toDouble())
    }

    fun generateDailyTransactions(
        count: Int = 30
    ): GenerateDailyTransaction {
        val dailyTransactions = mutableListOf<DailyTransaction>()
        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO
        repeat(count) {
            // 每日随机0 - 9条收入与支出
            val generateTransaction =
                generateRandomTransactions(Random.nextInt(0, 10), Random.nextInt(0, 10))
            val dailyExpense = BigDecimal(generateTransaction.totalExpense)
            val dailyIncome = BigDecimal(generateTransaction.totalIncome)
            dailyTransactions.add(DailyTransaction(
                date = LocalDate.now(), // 不关心日期
                expense = dailyExpense,
                income = dailyIncome,
                transactions = generateTransaction.transactions
            ))
            totalIncome += dailyIncome
            totalExpense += dailyExpense
        }

        return GenerateDailyTransaction(
            dailyTransactions = dailyTransactions,
            totalExpense = totalExpense.scale2f(),
            totalIncome = totalIncome.scale2f(),
            balance = (totalIncome - totalExpense).scale2f()
        )

    }
}


data class GenerateTransaction(
    val transactions: List<Transaction>,
    val totalIncome: String,
    val totalExpense: String
)

data class GenerateDailyTransaction(
    val dailyTransactions: List<DailyTransaction>,
    val totalIncome: String,
    val totalExpense: String,
    val balance: String
)