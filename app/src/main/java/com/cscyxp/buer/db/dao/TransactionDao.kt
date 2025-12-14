package com.cscyxp.buer.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cscyxp.buer.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(transactions: List<Transaction>)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions " +
            "WHERE (:startMonthTs IS NULL OR :endMonthTs IS NULL OR date BETWEEN :startMonthTs AND :endMonthTs) " +
            "AND (:categoryId IS NULL OR categoryId = :categoryId) " +
            "ORDER BY date DESC")
    fun getTransactions(
        startMonthTs: Long,
        endMonthTs: Long,
        categoryId: Long? = null
    ): Flow<List<Transaction>>

    @Update
    suspend fun updateTransaction(transaction: Transaction): Int

}