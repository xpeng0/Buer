package com.cscyxp.bookkeeping.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cscyxp.bookkeeping.data.entity.TransactionEntity
import com.cscyxp.bookkeeping.data.entity.TransactionEntityWithCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(transactions: List<TransactionEntity>)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactionsWithCategory(): Flow<List<TransactionEntityWithCategoryEntity>>

    @Query("SELECT * FROM transactions " +
            "WHERE (:startMonthTs IS NULL OR :endMonthTs IS NULL OR date BETWEEN :startMonthTs AND :endMonthTs) " +
            "AND (:categoryId IS NULL OR categoryId = :categoryId) " +
            "ORDER BY date DESC")
    fun getTransactions(
        startMonthTs: Long,
        endMonthTs: Long,
        categoryId: Long? = null
    ): Flow<List<TransactionEntityWithCategoryEntity>>

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity): Int
}
